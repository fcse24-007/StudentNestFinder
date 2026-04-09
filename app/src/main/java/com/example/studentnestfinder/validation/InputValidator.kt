package com.example.studentnestfinder.validation

object InputValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")
    private val studentIdRegex = Regex("^[A-Za-z0-9]{4,20}$")

    fun sanitizeText(input: String, maxLength: Int = 255): String =
        input.trim().take(maxLength)

    fun validateLogin(studentId: String, password: String): String? {
        if (!studentIdRegex.matches(studentId.trim())) return "Enter a valid ID (4-20 letters/numbers)."
        if (password.length < 8) return "Password must be at least 8 characters."
        return null
    }

    fun validateRegistration(
        studentId: String,
        name: String,
        email: String,
        password: String,
        role: String,
        university: String
    ): String? {
        validateLogin(studentId, password)?.let { return it }
        if (sanitizeText(name, 100).length < 2) return "Please enter your full name."
        if (!emailRegex.matches(email.trim())) return "Please enter a valid email."
        if (role !in setOf("STUDENT", "PROVIDER")) return "Invalid role selected."
        if (role == "STUDENT" && university.isBlank()) return "Please select your institution."
        return null
    }

    fun validateListingInput(
        title: String,
        description: String,
        location: String,
        price: String,
        deposit: String,
        distance: String
    ): String? {
        if (sanitizeText(title, 120).length < 3) return "Title must be at least 3 characters."
        if (sanitizeText(description, 1000).length < 10) return "Description must be at least 10 characters."
        if (sanitizeText(location, 120).length < 2) return "Please enter a valid location."
        val parsedPrice = price.toFloatOrNull()
        if (parsedPrice == null || parsedPrice <= 0f) return "Price must be a positive number."
        val parsedDeposit = deposit.toIntOrNull()
        if (parsedDeposit == null || parsedDeposit < 0) return "Deposit must be 0 or greater."
        val parsedDistance = distance.toFloatOrNull()
        if (parsedDistance == null || parsedDistance < 0f) return "Distance must be 0 or greater."
        return null
    }

    fun validateCardPayment(cardholder: String, cardNumber: String, cardExpiry: String, cardCvv: String): String? {
        if (sanitizeText(cardholder, 120).length < 2) return "Enter cardholder name."
        if (!cardNumber.matches(Regex("^\\d{16}$"))) return "Card number must be 16 digits."
        if (!cardExpiry.matches(Regex("^(0[1-9]|1[0-2])/\\d{2}$"))) return "Expiry must be in MM/YY format."
        if (!cardCvv.matches(Regex("^\\d{3,4}$"))) return "CVV must be 3 or 4 digits."
        return null
    }

    fun validatePreferences(minPrice: String, maxPrice: String): String? {
        val min = minPrice.toFloatOrNull() ?: return "Minimum price is invalid."
        val max = maxPrice.toFloatOrNull() ?: return "Maximum price is invalid."
        if (min < 0f || max < 0f) return "Prices must be 0 or greater."
        if (max < min) return "Maximum price must be greater than or equal to minimum price."
        return null
    }

    fun validateChatMessage(message: String): String? {
        val sanitized = sanitizeText(message, 500)
        if (sanitized.isBlank()) return "Message cannot be empty."
        return null
    }
}
