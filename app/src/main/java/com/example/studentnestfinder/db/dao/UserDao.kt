package com.example.studentnestfinder.db.dao

import androidx.room.*
import com.example.studentnestfinder.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<User>)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE studentId = :studentId AND passwordHash = :passwordHash LIMIT 1")
    suspend fun login(studentId: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): User?

    @Query("SELECT * FROM users WHERE studentId = :studentId LIMIT 1")
    suspend fun getByStudentId(studentId: String): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}