package com.example.studentnestfinder.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["id"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("reservationId"),
        Index(value = ["referenceNumber"], unique = true)
    ]
)
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reservationId: Int,
    val referenceNumber: String,         // Usually matches Reservation ref
    val amount: Int,
    val cardLastFour: String,            // For security, only store last 4 digits
    val paidAt: Long = System.currentTimeMillis()
)