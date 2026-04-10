package com.example.studentnestfinder.ui.booking

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.theme.BorderLightColor
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor
import com.example.studentnestfinder.validation.InputValidator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPaymentScreen(
    viewModel: BookingViewModel,
    listingId: Int,
    studentId: Int,
    onPaymentComplete: (receiptNumber: String) -> Unit,
    onCancel: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardholder by remember { mutableStateOf("") }
    var paymentError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(listingId) {
        viewModel.loadListing(listingId)
    }

    if (state.isSuccess) {
        ReceiptScreen(receiptNumber = state.receiptNumber, onClose = { onPaymentComplete(state.receiptNumber) })
    } else if (state.listing != null) {
        val listing = state.listing!!
        Scaffold(
            containerColor = SecondaryColor,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SecondaryColor
                    ),
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = NeutralColor)
                        }
                    },
                    title = { Text("Complete Booking", color = NeutralColor) },
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
                    .padding(16.dp)
            ) {
                item {
                    if (state.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), color = PrimaryColor)
                    }
                    
                    if (state.error != null) {
                        Text(state.error!!, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    paymentError?.let {
                        Text(it, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Booking Summary",
                                color = NeutralColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            SummaryRow("Listing:", listing.title)
                            SummaryRow("Monthly Rent:", "P${listing.price.toInt()}")
                            SummaryRow("Deposit Required:", "P${listing.depositAmount}")
                            HorizontalDivider(color = BorderLightColor, modifier = Modifier.padding(vertical = 12.dp))
                            SummaryRow(
                                "Total to Pay:",
                                "P${listing.depositAmount}",
                                isTotal = true
                            )
                        }
                    }

                    Text(
                        "Payment Information",
                        color = NeutralColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PaymentTextField(
                        value = cardholder,
                        label = "Cardholder Name",
                        onValueChange = { cardholder = InputValidator.sanitizeText(it, 120) },
                        icon = Icons.Default.Person
                    )

                    PaymentTextField(
                        value = cardNumber,
                        label = "Card Number",
                        onValueChange = {
                            val sanitized = it.filter { c -> c.isDigit() }.take(16)
                            cardNumber = sanitized
                        },
                        icon = Icons.Default.CreditCard,
                        keyboardType = KeyboardType.Number,
                        placeholder = "1234 5678 9012 3456"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PaymentTextField(
                            value = cardExpiry,
                            label = "Expiry (MM/YY)",
                            onValueChange = { cardExpiry = it.take(5) },
                            modifier = Modifier.weight(1f),
                            placeholder = "12/25"
                        )

                        PaymentTextField(
                            value = cardCvv,
                            label = "CVV",
                            onValueChange = { cardCvv = it.filter { c -> c.isDigit() }.take(4) },
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            placeholder = "123"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = TextSecondaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "By confirming, you agree to the booking terms and conditions.",
                            color = TextSecondaryColor,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val validationError = InputValidator.validateCardPayment(
                                cardholder = cardholder,
                                cardNumber = cardNumber,
                                cardExpiry = cardExpiry,
                                cardCvv = cardCvv
                            )
                            if (validationError == null) {
                                paymentError = null
                                viewModel.completePayment(listingId, studentId, listing.depositAmount)
                            } else {
                                paymentError = validationError
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor
                        ),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Confirm Payment (P${listing.depositAmount})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BorderLightColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = NeutralColor)
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = TextSecondaryColor,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            color = if (isTotal) PrimaryColor else NeutralColor,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PaymentTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondaryColor) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = if (icon != null) {
            { Icon(icon, contentDescription = null, tint = PrimaryColor) }
        } else null,
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = TextSecondaryColor) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = BorderLightColor,
            focusedTextColor = NeutralColor,
            unfocusedTextColor = NeutralColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(receiptNumber: String, onClose: () -> Unit) {
    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecondaryColor
                ),
                title = { Text("Booking Confirmed", color = NeutralColor) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SecondaryColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(50),
                color = PrimaryColor
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Payment Successful",
                color = NeutralColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Your booking has been confirmed",
                color = TextSecondaryColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ReceiptRow("Receipt Number:", receiptNumber)
                    ReceiptRow("Date:", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
                    ReceiptRow("Status:", "CONFIRMED")
                    ReceiptRow("Next Steps:", "Check your chat with landlord")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue to Chat", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondaryColor, fontSize = 14.sp)
        Text(value, color = PrimaryColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun BookingPaymentScreenPreview() {
    // Mock DAO for Preview
    val mockListingDao = object : com.example.studentnestfinder.db.dao.ListingDao {
        override suspend fun insert(listing: com.example.studentnestfinder.db.entities.Listing): Long = 1
        override suspend fun insertAll(listings: List<com.example.studentnestfinder.db.entities.Listing>) {}
        override suspend fun update(listing: com.example.studentnestfinder.db.entities.Listing) {}
        override suspend fun deleteById(listingId: Int, providerId: Int) {}
        override fun getAllAvailable(): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Listing>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun filter(minPrice: Float?, maxPrice: Float?, location: String?, type: String?): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Listing>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun getById(id: Int): kotlinx.coroutines.flow.Flow<com.example.studentnestfinder.db.entities.Listing?> = kotlinx.coroutines.flow.flowOf(null)
        override fun getByProvider(providerId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Listing>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override suspend fun updateStatus(listingId: Int, status: String) {}
        override suspend fun findNewMatchingListings(
            sinceTimestamp: Long,
            minPrice: Float?,
            maxPrice: Float?,
            location: String?,
            type: String?
        ): List<com.example.studentnestfinder.db.entities.Listing> = emptyList()
        override suspend fun count(): Int = 0
    }
    val mockResDao = object : com.example.studentnestfinder.db.dao.ReservationDao {
        override suspend fun insert(reservation: com.example.studentnestfinder.db.entities.Reservation): Long = 1
        override suspend fun update(reservation: com.example.studentnestfinder.db.entities.Reservation) {}
        override fun getForStudent(studentId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Reservation>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun getActiveForStudent(studentId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Reservation>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun getForProvider(providerId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Reservation>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override suspend fun getByReference(ref: String): com.example.studentnestfinder.db.entities.Reservation? = null
        override suspend fun countActiveForListing(listingId: Int): Int = 0
        override suspend fun countActiveForStudent(studentId: Int): Int = 0
        override suspend fun countActiveForStudentAndListing(studentId: Int, listingId: Int): Int = 0
        override fun getStudentReservationDetails(studentId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.StudentReservationDetails>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun getProviderReservationDetails(providerId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.ProviderReservationDetails>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun getPendingStudentNotifications(studentId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Reservation>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override fun getPendingProviderNotifications(providerId: Int): kotlinx.coroutines.flow.Flow<List<com.example.studentnestfinder.db.entities.Reservation>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override suspend fun markStudentNotified(reservationId: Int) {}
        override suspend fun markProviderNotified(reservationId: Int) {}
        override suspend fun markStudentNotifiedBatch(reservationIds: List<Int>) {}
        override suspend fun markProviderNotifiedBatch(reservationIds: List<Int>) {}
    }
    
    val viewModel = BookingViewModel(mockListingDao, mockResDao)
    
    BookingPaymentScreen(
        viewModel = viewModel,
        listingId = 1,
        studentId = 1,
        onPaymentComplete = {},
        onCancel = {},
        onSettingsClick = {},
        onHelpClick = {},
        onFaqClick = {},
        onLogout = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ReceiptScreenPreview() {
    ReceiptScreen(receiptNumber = "RCP-20260408001", onClose = {})
}
