package com.example.studentnestfinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.db.AppDatabase
import com.example.studentnestfinder.ui.navigation.AppNavigation
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (@Suppress("UNUSED_VARIABLE") e: Exception) {
            // Firebase already initialized or no google-services.json
        }

        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "student_nest_finder_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        setContent {
            val currentUser by UserSession.currentUser.collectAsState()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var hasPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted -> hasPermission = isGranted }
                )

                LaunchedEffect(Unit) {
                    if (!hasPermission) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            AppNavigation(
                database = database,
                isLoggedIn = currentUser != null
            )
        }
    }
}