package com.example.studentnestfinder.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentnestfinder.R
import com.example.studentnestfinder.db.AppDatabase
import com.example.studentnestfinder.db.entities.Listing

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val prefDao = database.userPreferenceDao()
        val listingDao = database.listingDao()

        val allPrefs = prefDao.getAllWithNotificationsEnabled()

        for (pref in allPrefs) {
            val newListings = listingDao.findNewMatchingListings(
                sinceTimestamp = pref.lastCheckedTimestamp,
                minPrice = pref.minPrice,
                maxPrice = pref.maxPrice,
                location = if (pref.preferredLocation.isEmpty()) null else pref.preferredLocation,
                type = if (pref.preferredType.isEmpty()) null else pref.preferredType
            )

            if (newListings.isNotEmpty()) {
                sendNotification(pref.userId, newListings)
                
                // Update last checked timestamp
                prefDao.upsert(pref.copy(lastCheckedTimestamp = System.currentTimeMillis()))
            }
        }

        return Result.success()
    }

    private fun sendNotification(userId: Int, listings: List<Listing>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "new_listings_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "New Listings",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new listings matching your preferences"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val title = "New Listings Found!"
        val content = "${listings.size} new rooms match your preferences. Check them out now!"

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon for simplicity
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(userId, notification)
    }
}
