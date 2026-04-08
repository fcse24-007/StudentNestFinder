package com.example.studentnestfinder.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["studentId"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val studentId: String,           // Unique
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String = "STUDENT",    // "STUDENT" | "PROVIDER"
    val university: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
