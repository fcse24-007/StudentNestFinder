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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.entities.Listing
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
    val canChatLandlord = isProvider || hasActiveReservation.any { it.listingId == currentListing.id }
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
                    Text("Property Image", color = TextSecondaryColor, modifier = Modifier.align(Alignment.Center))
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        color = PrimaryColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "P${currentListing.price.toInt()} / Month",
                            color = Color.White,
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        currentListing.title,
                        color = NeutralColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
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
                        Text(currentListing.location, color = TextSecondaryColor, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "About",
                        color = NeutralColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        currentListing.description,
                        color = TextSecondaryColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Property Details",
                        color = NeutralColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    DetailRow("Type:", currentListing.type)
                    DetailRow("Distance to Campus:", "${currentListing.distanceToCampusKm}km")
                    DetailRow("Availability:", currentListing.availabilityDate)
                    DetailRow("Deposit:", "P${currentListing.depositAmount}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Amenities",
                        color = NeutralColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Handle amenities (might be comma separated or JSON)
                    val amenitiesList = try {
                        if (currentListing.amenities.startsWith("[")) {
                            // Simple manual parse for seeded JSON
                            currentListing.amenities.removeSurrounding("[", "]")
                                .split(",")
                                .map { it.trim().removeSurrounding("\"") }
                        } else {
                            currentListing.amenities.split(", ")
                        }
                    } catch (e: Exception) {
                        listOf(currentListing.amenities)
                    }

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
                                Text(amenity, color = TextSecondaryColor, fontSize = 13.sp)
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
                        Button(
                            onClick = { onChatClick(currentListing.providerId, providerName) },
                            enabled = canChatLandlord,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BorderLightColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = NeutralColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat", color = NeutralColor)
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
                                    !canReserve -> "Active Reservation Limit Reached"
                                    else -> "Reserve Now"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (!canChatLandlord) {
                        Text(
                            "Reserve this property first to chat with the landlord.",
                            color = TextSecondaryColor,
                            fontSize = 11.sp,
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
        Text(label, color = TextSecondaryColor, fontSize = 14.sp)
        Text(value, color = NeutralColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ListingDetailScreenPreview() {
    // ListingDetailScreen(listingId = 1, onReserveClick = {}, onChatClick = {}, onBack = {})
}
