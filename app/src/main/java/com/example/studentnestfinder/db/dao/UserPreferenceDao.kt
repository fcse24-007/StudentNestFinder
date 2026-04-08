package com.example.studentnestfinder.db.dao

import androidx.room.*
import com.example.studentnestfinder.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(preference: UserPreference)

    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    fun getForUser(userId: Int): Flow<UserPreference?>

    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    suspend fun getForUserOnce(userId: Int): UserPreference?

    @Query("SELECT * FROM user_preferences WHERE notificationsEnabled = 1")
    suspend fun getAllWithNotificationsEnabled(): List<UserPreference>
}