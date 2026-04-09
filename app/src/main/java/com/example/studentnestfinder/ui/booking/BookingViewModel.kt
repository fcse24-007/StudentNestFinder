package com.example.studentnestfinder.ui.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.db.entities.Reservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val listingDao: ListingDao,
    private val reservationDao: ReservationDao
) : ViewModel() {
    companion object {
        private const val MIN_VALID_ID = 1
        private const val MIN_VALID_AMOUNT = 1
    }

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
                Log.e("BookingViewModel", "Failed to load listing", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load listing.") }
            }
        }
    }

    fun completePayment(listingId: Int, studentId: Int, amount: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (studentId < MIN_VALID_ID || amount < MIN_VALID_AMOUNT) {
                _uiState.update { it.copy(isLoading = false, error = "Invalid booking information.") }
                return@launch
            }
            
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
                if (reservationDao.countActiveForListing(listingId) > 0) {
                    _uiState.update { it.copy(isLoading = false, error = "This room has already been reserved.") }
                    return@launch
                }

                reservationDao.insert(reservation)
                listingDao.updateStatus(listingId, "RESERVED")
                
                _uiState.update { it.copy(
                    isLoading = false, 
                    isSuccess = true, 
                    receiptNumber = reference 
                ) }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Failed to complete booking payment", e)
                _uiState.update { it.copy(isLoading = false, error = "Payment failed. Please try again.") }
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
