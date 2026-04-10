package com.example.studentnestfinder.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.entities.StudentReservationDetails
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentReservationsScreen(
    studentId: Int,
    reservationDao: ReservationDao,
    onOpenChat: (providerId: Int, providerName: String, listingId: Int) -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val reservations by reservationDao.getStudentReservationDetails(studentId).collectAsState(initial = emptyList())
    var showConfirmationNotification by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        val pending = reservationDao.getPendingStudentNotifications(studentId).first()
        if (pending.isNotEmpty()) {
            reservationDao.markStudentNotifiedBatch(pending.map { it.id })
            showConfirmationNotification = true
        }
    }

    if (showConfirmationNotification) {
        AlertDialog(
            onDismissRequest = { showConfirmationNotification = false },
            title = { Text("Reservation Confirmed") },
            text = { Text("Your reservation has been confirmed successfully.") },
            confirmButton = {
                TextButton(onClick = { showConfirmationNotification = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryColor),
                title = { Text("My Reservations", color = NeutralColor) },
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
        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("You have no reservations yet.", color = TextSecondaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(SecondaryColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservations) { reservation ->
                    StudentReservationCard(
                        reservation = reservation,
                        onOpenChat = {
                            onOpenChat(reservation.providerId, reservation.providerName, reservation.listingId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentReservationCard(
    reservation: StudentReservationDetails,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(reservation.listingTitle, color = NeutralColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Landlord: ${reservation.providerName}", color = TextSecondaryColor, fontSize = 13.sp)
            Text("Location: ${reservation.location}", color = TextSecondaryColor, fontSize = 13.sp)
            Text("Monthly rent: P${reservation.monthlyRent.toInt()}", color = TextSecondaryColor, fontSize = 13.sp)
            Text("Reservation ref: ${reservation.referenceNumber}", color = TextSecondaryColor, fontSize = 13.sp)
            Text(
                "Reserved: ${
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(reservation.reservedAt))
                }",
                color = TextSecondaryColor,
                fontSize = 13.sp
            )
            Text("Status: ${reservation.status}", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onOpenChat, shape = RoundedCornerShape(10.dp)) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chat Landlord")
            }
        }
    }
}
