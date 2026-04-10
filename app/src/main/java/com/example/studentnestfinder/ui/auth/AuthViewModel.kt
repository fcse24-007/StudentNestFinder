package com.example.studentnestfinder.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.dao.UserPreferenceDao
import com.example.studentnestfinder.db.entities.User
import com.example.studentnestfinder.db.entities.UserPreference
import com.example.studentnestfinder.security.PasswordHasher
import com.example.studentnestfinder.validation.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao,
    private val userPreferenceDao: UserPreferenceDao,
    private val passwordHasher: PasswordHasher
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun toggleMode() = _uiState.update { it.copy(isLogin = !it.isLogin, error = null) }
    fun updateRole(role: String) = _uiState.update { it.copy(role = role) }
    fun updateStudentId(id: String) =
        _uiState.update { it.copy(studentId = InputValidator.sanitizeText(id, 20), error = null) }
    fun updatePassword(p: String) = _uiState.update { it.copy(password = p.take(64), error = null) }
    fun updateName(n: String) =
        _uiState.update { it.copy(name = InputValidator.sanitizeText(n, 100), error = null) }
    fun updateEmail(e: String) =
        _uiState.update { it.copy(email = InputValidator.sanitizeText(e, 120), error = null) }
    fun updateUniversity(u: String) =
        _uiState.update { it.copy(selectedUniversity = InputValidator.sanitizeText(u, 120), error = null) }

    fun login() {
        viewModelScope.launch {
            val state = _uiState.value
            val validationError = InputValidator.validateLogin(state.studentId, state.password)
            if (validationError != null) {
                _uiState.update { it.copy(error = validationError, isLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userDao.getByStudentId(normalizeStudentId(state.studentId, state.role))
                if (user != null && passwordHasher.verify(state.password, user.passwordHash)) {
                    UserSession.login(user)
                    _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Invalid credentials.") }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                _uiState.update { it.copy(isLoading = false, error = "Login failed. Please try again.") }
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            val state = _uiState.value
            val validationError = InputValidator.validateRegistration(
                studentId = state.studentId,
                name = state.name,
                email = state.email,
                password = state.password,
                role = state.role,
                university = state.selectedUniversity
            )
            if (validationError != null) {
                _uiState.update { it.copy(error = validationError, isLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val newUser = User(
                    studentId = normalizeStudentId(state.studentId, state.role),
                    name = state.name.trim(),
                    email = state.email.trim(),
                    passwordHash = passwordHasher.hash(state.password),
                    role = state.role,
                    university = if (state.role == "STUDENT") state.selectedUniversity.trim() else "N/A"
                )
                val result = userDao.insert(newUser)
                if (result > -1) {
                    val registeredUser = userDao.getById(result.toInt())
                    if (registeredUser != null) {
                        UserSession.login(registeredUser)
                        userPreferenceDao.upsert(UserPreference(userId = registeredUser.id))
                    }
                    _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "User already exists.") }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                _uiState.update { it.copy(isLoading = false, error = "Registration failed. Please try again.") }
            }
        }
    }

    private fun normalizeStudentId(id: String, role: String): String {
        val trimmed = id.trim()
        return if (role == "PROVIDER") trimmed.uppercase() else trimmed.lowercase()
    }
}
