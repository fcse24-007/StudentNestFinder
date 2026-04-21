package com.example.studentnestfinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.ui.xml.AuthFragment
import com.example.studentnestfinder.ui.xml.HomeFragment
import com.example.studentnestfinder.ui.xml.ListingDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    AuthFragment.Callbacks,
    HomeFragment.Callbacks,
    ListingDetailFragment.Callbacks {
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestNotificationPermissionIfNeeded()

        if (savedInstanceState == null) {
            if (UserSession.isLoggedIn) {
                showHome()
            } else {
                showAuth()
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showAuth() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AuthFragment())
            .commit()
    }

    private fun showHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }

    override fun onAuthSuccess() = showHome()

    override fun onListingSelected(listingId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ListingDetailFragment.newInstance(listingId))
            .addToBackStack(null)
            .commit()
    }

    override fun onLogout() {
        UserSession.logout()
        showAuth()
    }

    override fun onBackToHome() {
        supportFragmentManager.popBackStack()
    }
}
