package com.example.studentnestfinder.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "listing_images",
    foreignKeys = [
        ForeignKey(
            entity = Listing::class,
            parentColumns = ["id"],
            childColumns = ["listingId"], // Fixed: Was listing_id
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listingId")]
)
data class ListingImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val imagePath: String,           // Changed from ImagePath to imagePath
    val sortOrder: Int = 0
)