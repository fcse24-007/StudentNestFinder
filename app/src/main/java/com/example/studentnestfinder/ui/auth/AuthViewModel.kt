package com.example.studentnestfinder.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.db.AppDatabase
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.entities.User
import com.example.studentnestfinder.db.entities.UserPreference
import com.example.studentnestfinder.db.md5
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val userDao: UserDao, private val database: AppDatabase) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun toggleMode() = _uiState.update { it.copy(isLogin = !it.isLogin, error = null) }
    fun updateRole(role: String) = _uiState.update { it.copy(role = role) }
    fun updateStudentId(id: String) = _uiState.update { it.copy(studentId = id) }
    fun updatePassword(p: String) = _uiState.update { it.copy(password = p) }
    fun updateName(n: String) = _uiState.update { it.copy(name = n) }
    fun updateEmail(e: String) = _uiState.update { it.copy(email = e) }
    fun updateUniversity(u: String) = _uiState.update { it.copy(selectedUniversity = u) }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val hash = md5(_uiState.value.password)
            val user = userDao.login(_uiState.value.studentId, hash)

            if (user != null) {
                UserSession.login(user)
                _uiState.update { it.copy(isLoading = false, authSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Invalid Credentials") }
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val newUser = User(
                studentId = _uiState.value.studentId,
                name = _uiState.value.name,
                email = _uiState.value.email,
                passwordHash = md5(_uiState.value.password),
                role = _uiState.value.role,
                university = if (_uiState.value.role == "STUDENT") _uiState.value.selectedUniversity else "N/A"
            )
            val result = userDao.insert(newUser)
            if (result > -1) {
                val registeredUser = userDao.getById(result.toInt())
                if (registeredUser != null) {
                    UserSession.login(registeredUser)
                    // Create default preferences
                    database.userPreferenceDao().upsert(UserPreference(userId = registeredUser.id))
                }
                _uiState.update { it.copy(isLoading = false, authSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "User already exists") }
            }
        }
    }
}