package com.example.studentnestfinder.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.db.dao.ListingDao
import com.example.studentnestfinder.db.dao.UserPreferenceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listingDao: ListingDao,
    private val preferenceDao: UserPreferenceDao
) : ViewModel() {
    companion object {
        private const val INVALID_USER_ID = -1
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val searchQuery = MutableStateFlow("")
    private val debouncedSearchQuery = searchQuery.debounce(SEARCH_DEBOUNCE_MILLIS)
    private val selectedLocation = MutableStateFlow("All")

    init {
        loadListings()
    }

    private fun loadListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = UserSession.currentUser.value?.id ?: INVALID_USER_ID
            val preferenceFlow = if (userId > 0) preferenceDao.getForUser(userId) else flowOf(null)
            runCatching {
                combine(
                    listingDao.getAllAvailable(),
                    preferenceFlow,
                    debouncedSearchQuery,
                    selectedLocation
                ) { listings, preference, query, location ->
                    applyFilters(listings, preference, query, location)
                }.collect { items ->
                    _uiState.update { state -> state.copy(listings = items, isLoading = false, error = null) }
                }
            }.onFailure { error ->
                Log.e("HomeViewModel", "Failed to load listings", error)
                _uiState.update { it.copy(isLoading = false, error = "Unable to load listings.") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val trimmed = query.trim().take(100)
        _uiState.update { it.copy(searchQuery = trimmed) }
        searchQuery.value = trimmed
    }

    fun onLocationSelected(location: String) {
        _uiState.update { it.copy(selectedUniversity = location) }
        selectedLocation.value = location
    }

    private fun applyFilters(
        listings: List<com.example.studentnestfinder.db.entities.Listing>,
        preference: com.example.studentnestfinder.db.entities.UserPreference?,
        query: String,
        location: String
    ): List<com.example.studentnestfinder.db.entities.Listing> {
        val search = query.lowercase()
        return listings.filter { listing ->
            val matchesSearch = search.isBlank() ||
                listing.title.lowercase().contains(search) ||
                listing.location.lowercase().contains(search) ||
                listing.type.lowercase().contains(search)

            val matchesLocationChip = location == "All" ||
                listing.location.contains(location, ignoreCase = true)

            val matchesPreference = preference == null || (
                listing.price >= preference.minPrice &&
                    listing.price <= preference.maxPrice &&
                    (preference.preferredLocation.isBlank() ||
                        listing.location.contains(preference.preferredLocation, ignoreCase = true)) &&
                    (preference.preferredType.isBlank() || listing.type == preference.preferredType)
                )

            matchesSearch && matchesLocationChip && matchesPreference
        }
    }
}
