package com.example.studentnestfinder.di

import android.content.Context
import com.example.studentnestfinder.db.AppDatabase
import com.example.studentnestfinder.db.ChatRepository
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.dao.UserPreferenceDao
import com.example.studentnestfinder.security.BCryptPasswordHasher
import com.example.studentnestfinder.security.PasswordHasher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideListingDao(db: AppDatabase): ListingDao = db.listingDao()

    @Provides
    fun provideReservationDao(db: AppDatabase): ReservationDao = db.reservationDao()

    @Provides
    fun provideUserPreferenceDao(db: AppDatabase): UserPreferenceDao = db.userPreferenceDao()

    @Provides
    @Singleton
    fun provideChatRepository(db: AppDatabase): ChatRepository = ChatRepository(db.chatMessageDao())

    @Provides
    @Singleton
    fun providePasswordHasher(hasher: BCryptPasswordHasher): PasswordHasher = hasher
}
