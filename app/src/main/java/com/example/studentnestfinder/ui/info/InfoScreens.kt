package com.example.studentnestfinder.ui.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    onFaqClick: () -> Unit
) {
    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryColor),
                title = { Text("Help", color = NeutralColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralColor)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpCard("Getting started", "Log in as Student or Landlord, then browse or manage listings.")
            HelpCard("Listings", "Students can search and reserve listings. Landlords can add, edit and delete only their own listings.")
            HelpCard("Chat", "Open a listing and tap Chat to message the landlord directly.")
            HelpCard("Preferences", "Use Settings to save price range, location and room type filters.")
            HelpCard("Need quick answers?", "Open FAQ for common questions.")
            androidx.compose.material3.Button(onClick = onFaqClick, modifier = Modifier.fillMaxWidth()) {
                Text("Open FAQ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    onBack: () -> Unit,
    onHelpClick: () -> Unit
) {
    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryColor),
                title = { Text("FAQ", color = NeutralColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralColor)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpCard("How do I search listings?", "Use the search field and location chips on Home. Your saved preferences are applied automatically.")
            HelpCard("How do I contact a landlord?", "Open listing details and tap Chat.")
            HelpCard("How do landlords manage listings?", "Landlords open the Landlord Dashboard to create, edit, or delete their own listings.")
            HelpCard("How do I log out?", "Use the top-right menu and choose Logout.")
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Button(onClick = onHelpClick, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Help")
            }
        }
    }
}

@Composable
private fun HelpCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(body, color = TextSecondaryColor, fontSize = 14.sp)
        }
    }
}
