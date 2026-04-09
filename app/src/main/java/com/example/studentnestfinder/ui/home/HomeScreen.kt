package com.example.studentnestfinder.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.theme.BorderLightColor
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("unused")
fun HomeScreen(
    isProvider: Boolean,
    viewModel: HomeViewModel,
    onListingClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onProviderDashboardClick: () -> Unit,
    onOpenDrawer: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val locations = listOf("All", "Block 6", "Block 7", "Broadhurst", "Tlokweng", "Mogoditshane")

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            HomeTopBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onProfileClick = onProfileClick,
                showProviderDashboard = isProvider,
                onProviderDashboardClick = onProviderDashboardClick,
                onOpenDrawer = onOpenDrawer,
                onHelpClick = onHelpClick,
                onFaqClick = onFaqClick,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            item {
                Text("Find accommodation by location", color = NeutralColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.padding(vertical = 12.dp)) {
                    items(locations) { location ->
                        UniversityChip(location, state.selectedUniversity == location) {
                            viewModel.onLocationSelected(location)
                        }
                    }
                }
            }

            items(state.listings) { listing ->
                ListingCard(listing, onClick = { onListingClick(listing.id) })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ListingCard(listing: Listing, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(BorderLightColor)
            ) {
                Text("Property Image", color = TextSecondaryColor, modifier = Modifier.align(Alignment.Center))

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = PrimaryColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "P${listing.price.toInt()}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    listing.title,
                    color = NeutralColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(listing.location, color = TextSecondaryColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = TextSecondaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "${listing.distanceToCampusKm}km to Campus",
                        color = TextSecondaryColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun UniversityChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable { onClick() },
        color = if (isSelected) PrimaryColor else BorderLightColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            label,
            color = if (isSelected) Color.White else NeutralColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    showProviderDashboard: Boolean,
    onProviderDashboardClick: () -> Unit,
    onOpenDrawer: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SecondaryColor
        ),
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Navigation drawer", tint = NeutralColor)
            }
        },
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search Nests...", color = TextSecondaryColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = NeutralColor,
                    unfocusedTextColor = NeutralColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryColor) }
            )
        },
        actions = {
            if (showProviderDashboard) {
                IconButton(onClick = onProviderDashboardClick) {
                    Icon(Icons.Default.Home, contentDescription = "Dashboard", tint = NeutralColor)
                }
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = NeutralColor)
            }
            AppOverflowMenu(
                onSettingsClick = onProfileClick,
                onHelpClick = onHelpClick,
                onFaqClick = onFaqClick,
                onLogout = onLogout
            )
        }
    )
}

// ===== PREVIEWS (Top-level functions) =====

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ListingCardPreview() {
    val sampleListing = Listing(
        id = 1,
        providerId = 1,
        title = "Cozy Studio Near Campus",
        description = "Beautiful studio apartment",
        price = 2500f,
        location = "Gaborone",
        type = "STUDIO",
        amenities = "WiFi, Kitchen, Parking",
        depositAmount = 500,
        availabilityDate = "2026-05-01",
        status = "AVAILABLE",
        distanceToCampusKm = 0.5f
    )
    ListingCard(listing = sampleListing, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ListingCardPreviewHighPrice() {
    val sampleListing = Listing(
        id = 2,
        providerId = 2,
        title = "Luxury 2-Bedroom Apartment",
        description = "Luxury apartment with full amenities",
        price = 5500f,
        location = "Broadhurst",
        type = "FLAT",
        amenities = "WiFi, AC, Kitchen, Parking, Pool",
        depositAmount = 1000,
        availabilityDate = "2026-06-01",
        status = "AVAILABLE",
        distanceToCampusKm = 2.3f
    )
    ListingCard(listing = sampleListing, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun UniversityChipSelectedPreview() {
    UniversityChip(label = "UB", isSelected = true, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun UniversityChipUnselectedPreview() {
    UniversityChip(label = "Botho", isSelected = false, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun UniversityChipsRowPreview() {
    val universities = listOf("All", "UB", "Botho", "BAC")
    Row(modifier = Modifier.padding(16.dp)) {
        universities.forEach { uni ->
            UniversityChip(uni, uni == "UB") {}
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, widthDp = 400)
@Composable
fun HomeTopBarPreview() {
    HomeTopBar(
        query = "",
        onQueryChange = {},
        onProfileClick = {},
        showProviderDashboard = true,
        onProviderDashboardClick = {},
        onOpenDrawer = {},
        onHelpClick = {},
        onFaqClick = {},
        onLogout = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, widthDp = 400)
@Composable
fun HomeTopBarWithTextPreview() {
    HomeTopBar(
        query = "studio",
        onQueryChange = {},
        onProfileClick = {},
        showProviderDashboard = false,
        onProviderDashboardClick = {},
        onOpenDrawer = {},
        onHelpClick = {},
        onFaqClick = {},
        onLogout = {}
    )
}

// Mock ListingDao for preview
@Suppress("unused")
class MockListingDao : com.example.studentnestfinder.db.dao.ListingDao {
    override suspend fun insert(listing: Listing): Long = 1
    override suspend fun insertAll(listings: List<Listing>) {}
    override suspend fun update(listing: Listing) {}
    override suspend fun deleteById(listingId: Int, providerId: Int) {}
    override suspend fun updateStatus(listingId: Int, status: String) {}
    override suspend fun count(): Int = 3
    override suspend fun findNewMatchingListings(
        sinceTimestamp: Long,
        minPrice: Float?,
        maxPrice: Float?,
        location: String?,
        type: String?
    ): List<Listing> = emptyList()
    override fun getAllAvailable(): kotlinx.coroutines.flow.Flow<List<Listing>> =
        kotlinx.coroutines.flow.flowOf(
            listOf(
                Listing(
                    id = 1,
                    providerId = 1,
                    title = "Studio 1",
                    description = "Cozy studio",
                    price = 2500f,
                    location = "Gaborone",
                    type = "STUDIO",
                    amenities = "WiFi",
                    depositAmount = 500,
                    availabilityDate = "2026-05-01",
                    status = "AVAILABLE",
                    distanceToCampusKm = 0.5f
                ),
                Listing(
                    id = 2,
                    providerId = 2,
                    title = "Apartment 2",
                    description = "Modern apartment",
                    price = 3500f,
                    location = "Broadhurst",
                    type = "FLAT",
                    amenities = "AC",
                    depositAmount = 750,
                    availabilityDate = "2026-05-15",
                    status = "AVAILABLE",
                    distanceToCampusKm = 1.2f
                ),
                Listing(
                    id = 3,
                    providerId = 3,
                    title = "House 3",
                    description = "Spacious house",
                    price = 5000f,
                    location = "Tlokweng",
                    type = "FLAT",
                    amenities = "Pool",
                    depositAmount = 1000,
                    availabilityDate = "2026-06-01",
                    status = "AVAILABLE",
                    distanceToCampusKm = 3.0f
                )
            )
        )
    override fun filter(
        minPrice: Float?,
        maxPrice: Float?,
        location: String?,
        type: String?
    ): kotlinx.coroutines.flow.Flow<List<Listing>> =
        kotlinx.coroutines.flow.flowOf(emptyList())
    override fun getById(id: Int): kotlinx.coroutines.flow.Flow<Listing?> =
        kotlinx.coroutines.flow.flowOf(
            Listing(
                id = 1,
                providerId = 1,
                title = "Sample Listing",
                description = "Sample description",
                price = 2500f,
                location = "Gaborone",
                type = "STUDIO",
                amenities = "WiFi, Kitchen",
                depositAmount = 500,
                availabilityDate = "2026-05-01",
                status = "AVAILABLE",
                distanceToCampusKm = 0.5f
            )
        )
    override fun getByProvider(providerId: Int): kotlinx.coroutines.flow.Flow<List<Listing>> =
        kotlinx.coroutines.flow.flowOf(emptyList())
}
