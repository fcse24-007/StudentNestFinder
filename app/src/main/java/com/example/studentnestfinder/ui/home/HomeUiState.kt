package com.example.studentnestfinder.ui.home

import com.example.studentnestfinder.db.entities.Listing

data class HomeUiState(
    val listings: List<Listing> = emptyList(),
    val searchQuery: String = "",
    val selectedUniversity: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)