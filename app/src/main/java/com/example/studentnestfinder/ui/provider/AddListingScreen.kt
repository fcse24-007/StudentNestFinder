package com.example.studentnestfinder.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.entities.Listing
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    providerId: Int,
    listingDao: ListingDao,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("STUDIO") }
    var amenities by remember { mutableStateOf("") }
    var deposit by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    
    val types = listOf("EN_SUITE", "SHARED", "STUDIO", "FLAT")

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212)),
                title = { Text("Add New Listing", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (P)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = deposit,
                    onValueChange = { deposit = it },
                    label = { Text("Deposit (P)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
            )

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance to Campus (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
            )

            Text("Room Type", color = Color(0xFFBB86FC), fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.forEach { t ->
                    val isSelected = type == t
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { type = t },
                        color = if (isSelected) Color(0xFFBB86FC) else Color(0xFF252525),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(t.replace("_", " "), color = if (isSelected) Color.Black else Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = amenities,
                onValueChange = { amenities = it },
                label = { Text("Amenities (comma separated)") },
                placeholder = { Text("WiFi, Parking, Laundry") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFBB86FC), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && price.isNotEmpty() && location.isNotEmpty()) {
                        scope.launch {
                            val listing = Listing(
                                providerId = providerId,
                                title = title,
                                description = description,
                                price = price.toFloatOrNull() ?: 0f,
                                location = location,
                                type = type,
                                amenities = amenities,
                                depositAmount = deposit.toIntOrNull() ?: 0,
                                availabilityDate = "2024-05-01", // Default for now
                                distanceToCampusKm = distance.toFloatOrNull() ?: 0f
                            )
                            listingDao.insert(listing)
                            onSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Listing", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Re-using simplified clickable surface logic if needed, but since I'm in the same package I can just use clickable.
// Wait, I need to make sure clickable is imported.

