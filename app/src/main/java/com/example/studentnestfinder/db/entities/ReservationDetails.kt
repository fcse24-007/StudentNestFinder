package com.example.studentnestfinder.db.entities

data class StudentReservationDetails(
    val reservationId: Int,
    val referenceNumber: String,
    val status: String,
    val reservedAt: Long,
    val listingId: Int,
    val listingTitle: String,
    val location: String,
    val monthlyRent: Float,
    val providerId: Int,
    val providerName: String
)

data class ProviderReservationDetails(
    val reservationId: Int,
    val referenceNumber: String,
    val status: String,
    val reservedAt: Long,
    val listingId: Int,
    val listingTitle: String,
    val studentInternalId: Int,
    val studentIdentifier: String,
    val studentName: String
)
