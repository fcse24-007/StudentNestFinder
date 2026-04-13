package com.example.studentnestfinder.ui.common

fun normalizeAmenitiesInput(amenitiesInput: String): List<String> =
    amenitiesInput
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

fun parseStoredAmenities(rawAmenities: String): List<String> {
    return try {
        if (rawAmenities.startsWith("[")) {
            normalizeAmenitiesInput(
                rawAmenities
                    .removeSurrounding("[", "]")
                    .replace("\"", "")
            )
        } else {
            normalizeAmenitiesInput(rawAmenities)
        }
    } catch (_: Exception) {
        normalizeAmenitiesInput(rawAmenities)
    }
}
