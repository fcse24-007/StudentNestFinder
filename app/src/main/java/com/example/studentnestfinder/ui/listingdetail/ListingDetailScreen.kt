package com.example.studentnestfinder.ui.listingdetail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ListingImageDao
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.ui.common.parseStoredAmenities
import com.example.studentnestfinder.ui.common.resolveListingImageModel
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.theme.BorderLightColor
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: Int,
    currentUserId: Int,
    isProvider: Boolean,
    listingDao: ListingDao,
    listingImageDao: ListingImageDao,
    reservationDao: ReservationDao,
    userDao: UserDao,
    onReserveClick: (Int) -> Unit,
    onChatClick: (providerId: Int, providerName: String) -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val listing by listingDao.getById(listingId).collectAsState(initial = null)
    val listingImages by listingImageDao.getForListing(listingId).collectAsState(initial = emptyList())
    val context = LocalContext.current
    val hasActiveReservation by reservationDao.getActiveForStudent(currentUserId).collectAsState(initial = emptyList())
    var providerName by remember { mutableStateOf("Provider") }

    LaunchedEffect(listing) {
        listing?.let {
            val provider = userDao.getById(it.providerId)
            providerName = provider?.name ?: "Provider"
        }
    }

    if (listing == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
        return
    }

    val currentListing = listing!!
    val canChatWithLandlord = isProvider || hasActiveReservation.any { it.listingId == currentListing.id }
    val hasAnyActiveReservation = hasActiveReservation.isNotEmpty()
    val canReserve = !isProvider && currentListing.status == "AVAILABLE" && !hasAnyActiveReservation

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecondaryColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralColor)
                    }
                },
                title = { Text("Listing Details", color = NeutralColor) },
                actions = {
                    AppOverflowMenu(
                        onSettingsClick = onSettingsClick,
                        onHelpClick = onHelpClick,
                        onFaqClick = onFaqClick,
                        onLogout = onLogout
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SecondaryColor)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(BorderLightColor)
                ) {
                    AsyncImage(
                        model = resolveListingImageModel(context, listingImages.firstOrNull()?.imagePath),
                        contentDescription = "Property image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                                    startY = 0f
                                )
                            )
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "P${currentListing.price.toInt()} / Month",
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        currentListing.title,
                        color = NeutralColor,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(currentListing.location, color = TextSecondaryColor, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "About",
                        color = NeutralColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        currentListing.description,
                        color = TextSecondaryColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Property Details",
                        color = NeutralColor,
                        style = MaterialTheme.typography.titleLarge
                    )

                    DetailRow("Type:", currentListing.type)
                    DetailRow("Distance to Campus:", "${currentListing.distanceToCampusKm}km")
                    DetailRow("Availability:", currentListing.availabilityDate)
                    DetailRow("Deposit:", "P${currentListing.depositAmount}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Amenities",
                        color = NeutralColor,
                        style = MaterialTheme.typography.titleLarge
                    )

                    val amenitiesList = parseStoredAmenities(currentListing.amenities)

                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        amenitiesList.forEach { amenity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(amenity, color = TextSecondaryColor, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onChatClick(currentListing.providerId, providerName) },
                            enabled = canChatWithLandlord,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat")
                        }
                        Button(
                            onClick = { onReserveClick(currentListing.id) },
                            enabled = canReserve,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                when {
                                    isProvider -> "Students Only"
                                    currentListing.status != "AVAILABLE" -> "Reserved"
                                    !canReserve -> "Limit Reached"
                                    else -> "Reserve Now"
                                },
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (!canChatWithLandlord) {
                        Text(
                            "Reserve this property first to chat with the landlord.",
                            color = TextSecondaryColor,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondaryColor, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = NeutralColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ListingDetailScreenPreview() {
    // ListingDetailScreen(listingId = 1, onReserveClick = {}, onChatClick = {}, onBack = {})
}
