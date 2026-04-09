package com.example.studentnestfinder.ui.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentnestfinder.R
import com.example.studentnestfinder.db.dao.UserPreferenceDao
import com.example.studentnestfinder.db.entities.UserPreference
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import kotlinx.coroutines.launch

// Color Constants
private object Colors {
    val Primary = Color(0xFF0066FF)           // Royal Blue
    val Secondary = Color(0xFFF8F9FA)         // Off-White/Light Gray
    val Accent = Color(0xFFE63946)            // Muted Red
    val Neutral = Color(0xFF212529)           // Deep Charcoal
    val TextSecondary = Color(0xFF6C757D)     // Medium Gray
    val Border = Color(0xFFE9ECEF)            // Light Border
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    userId: Int,
    preferenceDao: UserPreferenceDao,
    onBack: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val preference by preferenceDao.getForUser(userId).collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var minPrice by remember { mutableStateOf("0") }
    var maxPrice by remember { mutableStateOf("5000") }
    var location by remember { mutableStateOf("") }
    var preferredType by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(preference) {
        preference?.let {
            minPrice = it.minPrice.toInt().toString()
            maxPrice = it.maxPrice.toInt().toString()
            location = it.preferredLocation
            preferredType = it.preferredType
            notificationsEnabled = it.notificationsEnabled
        }
    }

    Scaffold(
        containerColor = Colors.Secondary,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colors.Secondary
                ),
                title = {
                    Text(
                        context.getString(R.string.preferences_title),
                        color = Colors.Neutral,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Colors.Neutral
                        )
                    }
                },
                actions = {
                    AppOverflowMenu(
                        onSettingsClick = {},
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                context.getString(R.string.notification_settings),
                color = Colors.Primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    context.getString(R.string.enable_alerts),
                    color = Colors.Neutral
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Colors.Primary
                    )
                )
            }

            HorizontalDivider(color = Colors.Border)

            Text(
                context.getString(R.string.price_range_label),
                color = Colors.Primary,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it },
                    label = { Text(context.getString(R.string.price_min)) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Colors.Primary,
                        unfocusedBorderColor = Colors.Border,
                        focusedTextColor = Colors.Neutral,
                        unfocusedTextColor = Colors.Neutral,
                        focusedLabelColor = Colors.Primary,
                        unfocusedLabelColor = Colors.TextSecondary
                    )
                )
                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    label = { Text(context.getString(R.string.price_max)) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Colors.Primary,
                        unfocusedBorderColor = Colors.Border,
                        focusedTextColor = Colors.Neutral,
                        unfocusedTextColor = Colors.Neutral,
                        focusedLabelColor = Colors.Primary,
                        unfocusedLabelColor = Colors.TextSecondary
                    )
                )
            }

            Text(
                context.getString(R.string.location_label),
                color = Colors.Primary,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text(context.getString(R.string.location_hint)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Colors.Primary,
                    unfocusedBorderColor = Colors.Border,
                    focusedTextColor = Colors.Neutral,
                    unfocusedTextColor = Colors.Neutral,
                    focusedLabelColor = Colors.Primary,
                    unfocusedLabelColor = Colors.TextSecondary
                )
            )

            Text(
                context.getString(R.string.room_type_label),
                color = Colors.Primary,
                fontWeight = FontWeight.Bold
            )
            val types = listOf("EN_SUITE", "SHARED", "STUDIO", "FLAT")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                types.forEach { type ->
                    val isSelected = preferredType == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { preferredType = if (isSelected) "" else type },
                        color = if (isSelected) Colors.Primary else Colors.Border,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                type.replace("_", " "),
                                color = if (isSelected) Colors.Secondary else Colors.Neutral,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        val updatedPref = UserPreference(
                            id = preference?.id ?: 0,
                            userId = userId,
                            minPrice = minPrice.toFloatOrNull() ?: 0f,
                            maxPrice = maxPrice.toFloatOrNull() ?: 5000f,
                            preferredLocation = location,
                            preferredType = preferredType,
                            notificationsEnabled = notificationsEnabled,
                            lastCheckedTimestamp = preference?.lastCheckedTimestamp ?: System.currentTimeMillis()
                        )
                        preferenceDao.upsert(updatedPref)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Colors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    context.getString(R.string.save_preferences),
                    color = Colors.Secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
