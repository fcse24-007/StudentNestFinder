package com.example.studentnestfinder.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.db.entities.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class BookingViewModel(
    private val listingDao: ListingDao,
    private val reservationDao: ReservationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState = _uiState.asStateFlow()

    fun loadListing(listingId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val listing = listingDao.getById(listingId).first()
                if (listing != null) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        listing = listing
                    ) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Listing not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun completePayment(listingId: Int, studentId: Int, amount: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val reference = "RES-${UUID.randomUUID().toString().take(8).uppercase()}"
            
            val reservation = Reservation(
                listingId = listingId,
                studentId = studentId,
                referenceNumber = reference,
                status = "ACTIVE",
                moveInDate = System.currentTimeMillis() + 86400000 * 7, // Default 1 week from now
                moveOutDate = System.currentTimeMillis() + 86400000 * 30 * 6, // Default 6 months
                amountPaid = amount
            )

            try {
                // 1. Create the reservation
                reservationDao.insert(reservation)
                
                // 2. Update the listing status to RESERVED to lock it out
                listingDao.updateStatus(listingId, "RESERVED")
                
                _uiState.update { it.copy(
                    isLoading = false, 
                    isSuccess = true, 
                    receiptNumber = reference 
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

data class BookingUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val receiptNumber: String = "",
    val error: String? = null,
    val listing: Listing? = null
)
