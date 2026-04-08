package com.example.studentnestfinder.db.dao

import androidx.room.*
import com.example.studentnestfinder.db.entities.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reservation: Reservation): Long

    @Update
    suspend fun update(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE studentId = :studentId ORDER BY reservedAt DESC")
    fun getForStudent(studentId: Int): Flow<List<Reservation>>

    // Used by Providers to see who has booked their properties
    @Query("""
        SELECT * FROM reservations 
        WHERE listingId IN (SELECT id FROM listings WHERE providerId = :providerId)
    """)
    fun getForProvider(providerId: Int): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE referenceNumber = :ref LIMIT 1")
    suspend fun getByReference(ref: String): Reservation?

    // CRITICAL: Checks if a room is already taken before allowing a new booking
    @Query("SELECT COUNT(*) FROM reservations WHERE listingId = :listingId AND status IN ('PENDING','ACTIVE')")
    suspend fun countActiveForListing(listingId: Int): Int
}