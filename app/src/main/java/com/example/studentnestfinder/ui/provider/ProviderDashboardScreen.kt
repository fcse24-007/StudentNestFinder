package com.example.studentnestfinder.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.entities.Listing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    providerId: Int,
    listingDao: ListingDao,
    onListingClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val listings by listingDao.getByProvider(providerId).collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212)),
                title = { Text("Provider Dashboard", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Implement Add Listing Logic */ },
                containerColor = Color(0xFFBB86FC),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Listing", tint = Color.Black)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Your Managed Listings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (listings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No listings yet. Tap + to add.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(listings) { listing ->
                        ProviderListingCard(listing, onClick = { onListingClick(listing.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderListingCard(listing: Listing, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Status: ${listing.status}", color = if (listing.status == "RESERVED") Color.Yellow else Color.Green, fontSize = 12.sp)
                Text("Price: P${listing.price.toInt()}", color = Color.Gray, fontSize = 12.sp)
            }
            
            Surface(
                color = Color(0xFF252525),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "EDIT",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color(0xFFBB86FC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
