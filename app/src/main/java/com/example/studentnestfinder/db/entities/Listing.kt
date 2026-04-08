package com.example.studentnestfinder.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "listings",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("providerId")]
)
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val providerId: Int,             // Foreign key to User.id (where role = "PROVIDER")
    val title: String,
    val description: String,
    val price: Float,
    val location: String,            // e.g., "Block 6", "Tlokweng"
    val type: String,                // "EN_SUITE" | "SHARED" | "STUDIO" | "FLAT"
    val amenities: String,           // Stored as a JSON string or comma-separated list
    val depositAmount: Int,
    val availabilityDate: String,    // "YYYY-MM-DD" format
    val status: String = "AVAILABLE", // "AVAILABLE" | "RESERVED"
    val distanceToCampusKm: Float,
    val createdAt: Long = System.currentTimeMillis()
)