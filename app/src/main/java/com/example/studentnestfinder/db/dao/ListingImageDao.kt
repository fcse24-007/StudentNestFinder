package com.example.studentnestfinder.db.dao

import androidx.room.*
import com.example.studentnestfinder.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<ListingImage>)

    @Query("SELECT * FROM listing_images WHERE listingId = :listingId ORDER BY sortOrder ASC")
    fun getForListing(listingId: Int): Flow<List<ListingImage>>

    @Query("SELECT * FROM listing_images WHERE listingId = :listingId ORDER BY sortOrder ASC LIMIT 1")
    suspend fun getCoverImage(listingId: Int): ListingImage?
}