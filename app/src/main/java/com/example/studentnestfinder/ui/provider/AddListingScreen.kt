package com.example.studentnestfinder.ui.provider

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ListingImageDao
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.ui.common.normalizeAmenitiesInput
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.common.resolveListingImageModel
import com.example.studentnestfinder.ui.theme.BorderLightColor
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor
import com.example.studentnestfinder.validation.InputValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    providerId: Int,
    listingDao: ListingDao,
    listingImageDao: ListingImageDao,
    listingId: Int? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val existingListing by remember(listingId) {
        if (listingId == null) kotlinx.coroutines.flow.flowOf(null)
        else listingDao.getById(listingId)
    }.collectAsState(initial = null)
    val existingImages by remember(listingId) {
        if (listingId == null) kotlinx.coroutines.flow.flowOf(emptyList())
        else listingImageDao.getForListing(listingId)
    }.collectAsState(initial = emptyList())
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("STUDIO") }
    var amenities by remember { mutableStateOf("") }
    var deposit by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("AVAILABLE") }
    var formError by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            selectedImageUri = uri.toString()
        }
    }

    val types = listOf("EN_SUITE", "SHARED", "STUDIO", "FLAT")

    LaunchedEffect(existingListing) {
        existingListing?.let {
            title = it.title
            description = it.description
            price = it.price.toInt().toString()
            location = it.location
            type = it.type
            amenities = it.amenities
            deposit = it.depositAmount.toString()
            distance = it.distanceToCampusKm.toString()
            status = it.status
        }
    }
    LaunchedEffect(existingImages) {
        if (selectedImageUri == null) {
            selectedImageUri = existingImages.firstOrNull()?.imagePath
        }
    }

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryColor),
                title = { Text(if (listingId == null) "Add New Listing" else "Edit Listing", color = NeutralColor) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SecondaryColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = InputValidator.sanitizeText(it, 120) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderLightColor,
                    unfocusedTextColor = NeutralColor,
                    focusedTextColor = NeutralColor
                )
            )

            OutlinedTextField(
                    value = description,
                    onValueChange = { description = InputValidator.sanitizeText(it, 1000) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderLightColor,
                    unfocusedTextColor = NeutralColor,
                    focusedTextColor = NeutralColor
                )
            )

            Text("Property Image", color = PrimaryColor, fontWeight = FontWeight.Bold)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, BorderLightColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!selectedImageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = resolveListingImageModel(context, selectedImageUri),
                            contentDescription = "Selected property image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = TextSecondaryColor,
                                modifier = Modifier.size(48.dp)
                            )
                            Text("No image selected", color = TextSecondaryColor)
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { imagePickerLauncher.launch(arrayOf("image/*")) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
            ) {
                Text(
                    if (selectedImageUri.isNullOrBlank() && existingImages.isEmpty()) {
                        "Upload Property Image"
                    } else {
                        "Change Property Image"
                    }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { char -> char.isDigit() || char == '.' }.take(10) },
                    label = { Text("Price (P)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = BorderLightColor,
                        unfocusedTextColor = NeutralColor,
                        focusedTextColor = NeutralColor
                    )
                )
                OutlinedTextField(
                    value = deposit,
                    onValueChange = { deposit = it.filter { char -> char.isDigit() }.take(10) },
                    label = { Text("Deposit (P)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = BorderLightColor,
                        unfocusedTextColor = NeutralColor,
                        focusedTextColor = NeutralColor
                    )
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = InputValidator.sanitizeText(it, 120) },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderLightColor,
                    unfocusedTextColor = NeutralColor,
                    focusedTextColor = NeutralColor
                )
            )

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it.filter { char -> char.isDigit() || char == '.' }.take(10) },
                label = { Text("Distance to Campus (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderLightColor,
                    unfocusedTextColor = NeutralColor,
                    focusedTextColor = NeutralColor
                )
            )

            Text("Room Type", color = PrimaryColor, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.forEach { t ->
                    val isSelected = type == t
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { type = t },
                        color = if (isSelected) PrimaryColor else BorderLightColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(t.replace("_", " "), color = if (isSelected) Color.White else NeutralColor, fontSize = 10.sp)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = amenities,
                onValueChange = { amenities = InputValidator.sanitizeText(it, 300) },
                label = { Text("Amenities (comma separated)") },
                placeholder = { Text("WiFi, Parking, Laundry") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = BorderLightColor,
                    unfocusedTextColor = NeutralColor,
                    focusedTextColor = NeutralColor,
                    unfocusedPlaceholderColor = TextSecondaryColor
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            formError?.let {
                Text(text = it, color = Color.Red)
            }

            Button(
                onClick = {
                    val validationError = InputValidator.validateListingInput(
                        title = title,
                        description = description,
                        location = location,
                        price = price,
                        deposit = deposit.ifBlank { "0" },
                        distance = distance.ifBlank { "0" }
                    )
                    if (validationError != null) {
                        formError = validationError
                        return@Button
                    }
                    if (selectedImageUri.isNullOrBlank() && existingImages.isEmpty()) {
                        formError = "Please upload a property image."
                        return@Button
                    }
                    formError = null
                    scope.launch {
                        runCatching {
                            val listing = Listing(
                                id = listingId ?: 0,
                                providerId = providerId,
                                title = title.trim(),
                                description = description.trim(),
                                price = price.toFloatOrNull() ?: 0f,
                                location = location.trim(),
                                type = type,
                                amenities = normalizeAmenitiesInput(amenities).joinToString(", "),
                                depositAmount = deposit.toIntOrNull() ?: 0,
                                availabilityDate = existingListing?.availabilityDate ?: "2026-05-01",
                                status = status,
                                distanceToCampusKm = distance.toFloatOrNull() ?: 0f
                            )
                            val savedListingId = if (listingId == null) {
                                listingDao.insert(listing).toInt()
                            } else {
                                listingDao.update(listing)
                                listingId
                            }
                            if (!selectedImageUri.isNullOrBlank()) {
                                listingImageDao.replaceCoverImage(savedListingId, selectedImageUri!!)
                            }
                            onSuccess()
                        }.onFailure { error ->
                            Log.e("AddListingScreen", "Failed to save listing", error)
                            formError = "Failed to save listing. Please try again."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (listingId == null) "Create Listing" else "Save Listing", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
