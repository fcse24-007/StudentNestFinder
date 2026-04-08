package com.example.studentnestfinder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentnestfinder.db.dao.ListingDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val listingDao: ListingDao) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadListings()
    }

    private fun loadListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Observe the database. Any changes (like a room being reserved)
            // will automatically update the UI.
            listingDao.getAllAvailable().collect { items ->
                _uiState.update { it.copy(listings = items, isLoading = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // Logic to trigger filter query in DAO can be added here
    }

    fun onUniversitySelected(uni: String) {
        _uiState.update { it.copy(selectedUniversity = uni) }
        // You can filter listings based on proximity to this campus
    }
}