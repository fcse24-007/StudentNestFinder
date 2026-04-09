package com.example.studentnestfinder.security

import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String, hash: String): Boolean
}

@Singleton
class BCryptPasswordHasher @Inject constructor() : PasswordHasher {
    override fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

    override fun verify(password: String, hash: String): Boolean =
        runCatching { BCrypt.checkpw(password, hash) }.getOrDefault(false)
}
