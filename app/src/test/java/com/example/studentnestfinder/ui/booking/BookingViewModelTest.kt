package com.example.studentnestfinder.ui.booking

import com.example.studentnestfinder.MainDispatcherRule
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.ReservationDao
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.db.entities.Reservation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BookingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun completePayment_success_setsSuccessState() = runTest {
        val listingDao = FakeListingDao()
        val reservationDao = FakeReservationDao(activeCount = 0)
        val viewModel = BookingViewModel(listingDao, reservationDao)

        viewModel.completePayment(listingId = 1, studentId = 10, amount = 1200)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSuccess)
        assertTrue(state.receiptNumber.startsWith("RES-"))
        assertEquals("RESERVED", listingDao.lastStatus)
    }

    @Test
    fun completePayment_alreadyReserved_setsError() = runTest {
        val viewModel = BookingViewModel(FakeListingDao(), FakeReservationDao(activeCount = 1))

        viewModel.completePayment(listingId = 1, studentId = 10, amount = 1200)
        advanceUntilIdle()

        assertEquals("This room has already been reserved.", viewModel.uiState.value.error)
    }
}

private class FakeReservationDao(private val activeCount: Int) : ReservationDao {
    override suspend fun insert(reservation: Reservation): Long = 1
    override suspend fun update(reservation: Reservation) {}
    override fun getForStudent(studentId: Int): Flow<List<Reservation>> = flowOf(emptyList())
    override fun getForProvider(providerId: Int): Flow<List<Reservation>> = flowOf(emptyList())
    override suspend fun getByReference(ref: String): Reservation? = null
    override suspend fun countActiveForListing(listingId: Int): Int = activeCount
}

private class FakeListingDao : ListingDao {
    var lastStatus: String? = null

    override suspend fun insert(listing: Listing): Long = 1
    override suspend fun insertAll(listings: List<Listing>) {}
    override suspend fun update(listing: Listing) {}
    override suspend fun deleteById(listingId: Int, providerId: Int) {}
    override fun getAllAvailable(): Flow<List<Listing>> = flowOf(emptyList())
    override fun filter(minPrice: Float?, maxPrice: Float?, location: String?, type: String?): Flow<List<Listing>> =
        flowOf(emptyList())
    override fun getById(id: Int): Flow<Listing?> = flowOf(
        Listing(
            id = 1,
            providerId = 1,
            title = "Sample",
            description = "Description",
            price = 1200f,
            location = "Block 6",
            type = "STUDIO",
            amenities = "WiFi",
            depositAmount = 1200,
            availabilityDate = "2026-05-01",
            status = "AVAILABLE",
            distanceToCampusKm = 1.2f
        )
    )
    override fun getByProvider(providerId: Int): Flow<List<Listing>> = flowOf(emptyList())
    override suspend fun updateStatus(listingId: Int, status: String) {
        lastStatus = status
    }
    override suspend fun findNewMatchingListings(
        sinceTimestamp: Long,
        minPrice: Float?,
        maxPrice: Float?,
        location: String?,
        type: String?
    ): List<Listing> = emptyList()
    override suspend fun count(): Int = 0
}
