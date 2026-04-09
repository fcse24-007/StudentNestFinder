package com.example.studentnestfinder.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.studentnestfinder.db.AppDatabase
import com.example.studentnestfinder.db.entities.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Context
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetByStudentId_returnsSavedUser() = runBlocking {
        val id = userDao.insert(
            User(
                studentId = "STU1234",
                name = "Student User",
                email = "student@example.com",
                passwordHash = "hashed-password"
            )
        )

        val saved = userDao.getByStudentId("STU1234")
        assertEquals(1L, id)
        assertNotNull(saved)
        assertEquals("Student User", saved?.name)
    }
}
