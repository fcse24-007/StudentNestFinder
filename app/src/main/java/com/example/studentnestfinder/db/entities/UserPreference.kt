package com.example.studentnestfinder.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_preferences",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)

data class UserPreference(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val minPrice: Float = 0f,
    val maxPrice: Float = 5000f,
    val preferredLocation: String = "",
    val availabilityDate: String = "",
    val preferredType: String = "",      // "EN_SUITE" | "SHARED" | "STUDIO" | "FLAT" | ""
    val notificationsEnabled: Boolean = true,
    val lastCheckedTimestamp: Long = System.currentTimeMillis()
)
