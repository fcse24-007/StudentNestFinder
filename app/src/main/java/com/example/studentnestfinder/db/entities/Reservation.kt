package com.example.studentnestfinder.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = Listing::class,
            parentColumns = ["id"],
            childColumns = ["listingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("listingId"),
        Index("studentId"),
        Index(value = ["referenceNumber"], unique = true)
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val studentId: Int,                  // Links to User.id
    val referenceNumber: String,         // e.g. "RES-2024-UB-001"
    val status: String = "PENDING",      // "PENDING" | "ACTIVE" | "CANCELLED"
    val moveInDate: Long,                // Timestamp
    val moveOutDate: Long,               // Timestamp
    val amountPaid: Int,                 // Total amount in BWP
    val reservedAt: Long = System.currentTimeMillis(),
    val studentNotified: Boolean = false,
    val providerNotified: Boolean = false
)
