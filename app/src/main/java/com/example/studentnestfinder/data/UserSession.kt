package com.example.studentnestfinder.data

import com.example.studentnestfinder.db.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UserSession {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    val isLoggedIn: Boolean
        get() = _currentUser.value != null

    val currentUserId: Int
        get() = _currentUser.value?.id ?: -1
}
