package com.example.studentnestfinder.data

import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.db.entities.User
import java.util.Locale

@Suppress("unused")
object MockDataGenerator {

    fun generateMockStudents(): List<User> {
        val universities = listOf(
            "University of Botswana (UB)",
            "Botho University",
            "Botswana Accountancy College (BAC)",
            "Imperial School of Business and Science (ISBS)",
            "Boitekanelo College"
        )

        val firstNames = listOf(
            "Kagiso", "Nobantu", "Tshotlo", "Kesebone", "Thabo", "Palesa", "Lerato", "Nhlanhla",
            "Moepi", "Gorata", "Boitumelo", "Tshepiso", "Tumiso", "Bongiwe", "Thandeka", "Mpho"
        )

        val lastNames = listOf(
            "Makhele", "Kasane", "Ramone", "Molefe", "Ndaba", "Khubone", "Sengwayo", "Mkhize",
            "Dlamini", "Makgobo", "Lebelo", "Motlanthe", "Sekooe", "Maphalane", "Nthaga", "Radebe"
        )

        return (1..50).map { index ->
            val university = universities[index % universities.size]
            val firstName = firstNames[index % firstNames.size]
            val lastName = lastNames[(index + index / 5) % lastNames.size]

            User(
                id = index,
                name = "$firstName $lastName",
                email = "${firstName.lowercase()}.${lastName.lowercase()}@example.com",
                studentId = "fcse${String.format(Locale.US, "%02d", (index % 30) + 1)}-${String.format(Locale.US, "%03d", index)}",
                passwordHash = "hashed_password_123", // In real app, this would be hashed
                university = university,
                role = "STUDENT",
                createdAt = System.currentTimeMillis() - (index * 86400000)
            )
        }
    }

    fun generateMockProviders(): List<User> {
        return listOf(
            User(
                id = 51,
                name = "Mpho Ndlovu",
                email = "mpho.ndlovu@example.com",
                studentId = "PRV001",
                passwordHash = "hashed_password_123",
                university = "Gaborone",
                role = "PROVIDER",
                createdAt = System.currentTimeMillis() - (90 * 86400000)
            ),
            User(
                id = 52,
                name = "Boitumelo Khubone",
                email = "boitumelo.khubone@example.com",
                studentId = "PRV002",
                passwordHash = "hashed_password_123",
                university = "Gaborone",
                role = "PROVIDER",
                createdAt = System.currentTimeMillis() - (120 * 86400000)
            )
        )
    }

    fun generateMockListings(): List<Listing> {
        val locations = listOf(
            "Block 6, Gaborone",
            "Broadhurst, Gaborone",
            "Tlokweng",
            "Francistown",
            "Glen Marais, Gaborone",
            "Phakalane, Gaborone",
            "Botswana Accountancy College Area",
            "Notwane, Gaborone",
            "Old Naledi, Gaborone",
            "New Broadhurst"
        )

        val amenitiesList = listOf(
            "WiFi, Kitchen, Parking",
            "WiFi, AC, Kitchen, Parking, Pool",
            "WiFi, AC, Kitchen, Parking, Security Gate",
            "WiFi, Kitchen, Parking, Water Tank",
            "WiFi, AC, Kitchen, Parking, Garden",
            "WiFi, Kitchen, Parking, TV Cable",
            "WiFi, AC, Kitchen, Parking, Gym",
            "WiFi, Kitchen, Parking, Laundry Service",
            "WiFi, AC, Kitchen, Parking, 24/7 Security",
            "WiFi, Kitchen, Parking, Library Area"
        )

        val titles = listOf(
            "Cozy Studio Near Campus",
            "Luxury 2-Bedroom Apartment",
            "Modern Shared Flat",
            "Spacious En-Suite Room",
            "Studio with Garden",
            "2-Bed Apartment, Fully Furnished",
            "Comfortable Shared House",
            "Modern Studio Apartment",
            "3-Bedroom Family House",
            "Luxury Penthouse Studio"
        )

        val descriptions = listOf(
            "Beautiful studio apartment with excellent amenities. Newly renovated with modern furniture.",
            "Luxury apartment perfect for students. Close to all campuses and shopping centers.",
            "Shared accommodation with friendly housemates. Great for social students.",
            "Quiet en-suite room perfect for focused studying.",
            "Spacious studio with private garden area.",
            "Fully furnished 2-bedroom apartment ready for immediate occupation.",
            "Comfortable shared house with common areas and private rooms.",
            "Modern studio with contemporary design and all necessary amenities.",
            "Large family house suitable for groups of students.",
            "Premium studio apartment with luxury finishes."
        )

        val prices = listOf(1800f, 2200f, 2500f, 2800f, 3000f, 3500f, 4000f, 4500f, 5000f, 5500f)

        return (1..35).map { index ->
            val providerId = if (index % 2 == 0) 51 else 52
            val distanceToCampus = when (index % 5) {
                0 -> 0.2f
                1 -> 0.5f
                2 -> 1.0f
                3 -> 2.0f
                else -> 3.0f
            }

            Listing(
                id = index,
                providerId = providerId,
                title = "${titles[index % titles.size]} - Unit $index",
                description = descriptions[index % descriptions.size],
                price = prices[index % prices.size],
                location = locations[index % locations.size],
                type = listOf("EN_SUITE", "SHARED", "STUDIO", "FLAT")[index % 4],
                amenities = amenitiesList[index % amenitiesList.size],
                depositAmount = when (index % 5) {
                    0 -> 500
                    1 -> 750
                    2 -> 1000
                    3 -> 1500
                    else -> 2000
                },
                availabilityDate = "2026-${String.format(Locale.US, "%02d", (index % 11) + 5)}-${String.format(Locale.US, "%02d", (index % 28) + 1)}",
                status = if (index % 7 == 0) "RESERVED" else "AVAILABLE",
                distanceToCampusKm = distanceToCampus,
                createdAt = System.currentTimeMillis() - (index * 86400000)
            )
        }
    }
}
