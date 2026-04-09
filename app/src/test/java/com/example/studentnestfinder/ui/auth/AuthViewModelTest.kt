package com.example.studentnestfinder.ui.auth

import com.example.studentnestfinder.MainDispatcherRule
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.dao.UserPreferenceDao
import com.example.studentnestfinder.db.entities.User
import com.example.studentnestfinder.db.entities.UserPreference
import com.example.studentnestfinder.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDao: FakeUserDao
    private lateinit var preferenceDao: FakeUserPreferenceDao
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        UserSession.logout()
        userDao = FakeUserDao()
        preferenceDao = FakeUserPreferenceDao()
        viewModel = AuthViewModel(userDao, preferenceDao, FakePasswordHasher())
    }

    @After
    fun teardown() {
        UserSession.logout()
    }

    @Test
    fun register_success_createsUserAndAuthenticates() = runTest {
        viewModel.toggleMode()
        viewModel.updateStudentId("200001")
        viewModel.updateName("Test User")
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.updateUniversity("University of Botswana")

        viewModel.register()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.authSuccess)
        assertNotNull(UserSession.currentUser.value)
        assertEquals(1, preferenceDao.upserted.size)
    }

    @Test
    fun login_invalidPassword_setsError() = runTest {
        userDao.insert(
            User(
                studentId = "200001",
                name = "Existing User",
                email = "existing@example.com",
                passwordHash = "hashed:password123"
            )
        )
        viewModel.updateStudentId("200001")
        viewModel.updatePassword("wrongpass")

        viewModel.login()
        advanceUntilIdle()

        assertEquals("Invalid credentials.", viewModel.uiState.value.error)
    }
}

private class FakePasswordHasher : PasswordHasher {
    override fun hash(password: String): String = "hashed:$password"
    override fun verify(password: String, hash: String): Boolean = hash == hash(password)
}

private class FakeUserDao : UserDao {
    private val users = mutableListOf<User>()
    private var idCounter = 1

    override suspend fun insert(user: User): Long {
        if (users.any { it.studentId == user.studentId || it.email == user.email }) return -1
        val created = user.copy(id = idCounter++)
        users.add(created)
        return created.id.toLong()
    }

    override suspend fun insertAll(users: List<User>) {
        users.forEach { insert(it) }
    }

    override suspend fun update(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) users[index] = user
    }

    override suspend fun login(studentId: String, passwordHash: String): User? =
        users.firstOrNull { it.studentId == studentId && it.passwordHash == passwordHash }

    override suspend fun getById(id: Int): User? = users.firstOrNull { it.id == id }

    override suspend fun getByStudentId(studentId: String): User? = users.firstOrNull { it.studentId == studentId }

    override suspend fun count(): Int = users.size
}

private class FakeUserPreferenceDao : UserPreferenceDao {
    val upserted = mutableListOf<UserPreference>()

    override suspend fun upsert(preference: UserPreference) {
        upserted.add(preference)
    }

    override fun getForUser(userId: Int): Flow<UserPreference?> = flowOf(null)

    override suspend fun getForUserOnce(userId: Int): UserPreference? = null

    override suspend fun getAllWithNotificationsEnabled(): List<UserPreference> = emptyList()
}
