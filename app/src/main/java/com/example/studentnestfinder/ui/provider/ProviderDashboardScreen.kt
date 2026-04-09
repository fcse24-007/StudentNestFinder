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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.theme.ErrorColor
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.SuccessColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    providerId: Int,
    listingDao: ListingDao,
    onListingClick: (Int) -> Unit,
    onBack: () -> Unit,
    onAddListing: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val listings by listingDao.getByProvider(providerId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryColor),
                title = { Text("Landlord Dashboard", color = NeutralColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralColor)
                    }
                },
                actions = {
                    AppOverflowMenu(
                        onSettingsClick = onSettingsClick,
                        onHelpClick = onHelpClick,
                        onFaqClick = onFaqClick,
                        onLogout = onLogout
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddListing,
                containerColor = PrimaryColor,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Listing", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Your Managed Listings", color = NeutralColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (listings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No listings yet. Tap + to add.", color = TextSecondaryColor)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(listings) { listing ->
                        ProviderListingCard(
                            listing = listing,
                            onEditClick = { onListingClick(listing.id) },
                            onDeleteClick = {
                                scope.launch { listingDao.deleteById(listing.id, providerId) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderListingCard(
    listing: Listing,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, color = NeutralColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Status: ${listing.status}", color = if (listing.status == "RESERVED") ErrorColor else SuccessColor, fontSize = 12.sp)
                Text("Price: P${listing.price.toInt()}", color = TextSecondaryColor, fontSize = 12.sp)
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit listing", tint = PrimaryColor)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete listing", tint = ErrorColor)
                }
            }
        }
    }
}
