# StudentNestFinder - Student Accommodation App

A modern Android application built with Kotlin and Jetpack Compose that helps tertiary students in Gaborone find affordable and safe accommodation based on price, location, and availability.

## Quick Start

### System Requirements
- Android Studio Arctic Fox or later
- JDK 17+
- Minimum SDK: 24
- Target SDK: 35
- Emulator: Nexus 5 (Android 6.0, API 23) or higher

### Building the Project

1. **Clone/Open Project**
   ```bash
   cd /home/daniel/AndroidStudioProjects/StudentNestFinder
   ```

2. **Build**
   ```bash
   ./gradlew clean build
   ```

3. **Run on Emulator**
   ```bash
   ./gradlew installDebug
   ```

4. **View Previews in Android Studio**
   - Open any Compose file (e.g., `HomeScreen.kt`)
   - Click "Preview" or press `Shift + Alt + P`
   - All `@Preview` functions will be rendered

## Features Overview

### 🏠 Home Screen
- Browse available accommodation listings
- Filter by university/institution
- Search functionality
- View quick details (price, location, distance to campus)

**Location:** `ui/home/HomeScreen.kt`

### 📋 Listing Details
- Full property information
- Amenities checklist
- Distance to campus
- Deposit amount
- Availability date
- "Reserve Now" button
- "Chat with Landlord" button

**Location:** `ui/listingdetail/ListingDetailScreen.kt`

### 💳 Booking & Payment
- Simulated payment form
- Card validation
- Booking summary with total amount
- Receipt generation with unique number
- Status update to "RESERVED"

**Location:** `ui/booking/BookingPaymentScreen.kt`

### 💬 Real-time Chat
- Direct messaging with landlords
- Message history
- Unread badges
- Timestamp display
- Message search (ready for implementation)

**Location:** `ui/chat/ChatScreen.kt`

### 👤 Authentication
- Student/Landlord role selection
- Institution dropdown (5 institutions)
- Registration & Login
- Account creation with validation

**Location:** `ui/auth/AuthScreen.kt`

## Architecture

### Technology Stack
```
Frontend
├── Jetpack Compose (Material 3)
├── Navigation Compose
└── Lifecycle/ViewModel

Backend/Storage
├── Room Database (Local)
├── Firebase Firestore (Real-time chat)
└── Firebase Auth (Optional)

Background
├── WorkManager (Notifications)
└── Coroutines (Async tasks)
```

### Data Flow
```
UI Layer (Composables)
    ↓
ViewModel Layer (StateFlow management)
    ↓
Repository/DAO Layer (Database access)
    ↓
Database/Firebase
```

## Project Structure

```
app/src/main/java/com/example/studentnestfinder/
├── MainActivity.kt                    # Compose entry point
├── db/
│   ├── AppDatabase.kt               # Room database config
│   ├── dao/                         # Data access objects
│   │   ├── ListingDao.kt
│   │   ├── UserDao.kt
│   │   ├── ChatMessageDao.kt
│   │   ├── ReservationDao.kt
│   │   ├── ReceiptDao.kt
│   │   └── UserPreferenceDao.kt
│   └── entities/                    # Data models
│       ├── Listing.kt
│       ├── User.kt
│       ├── ChatMessage.kt
│       ├── Reservation.kt
│       ├── Receipt.kt
│       ├── UserPreference.kt
│       └── ListingImage.kt
├── ui/
│   ├── auth/
│   │   ├── AuthScreen.kt            # Login/Register UI
│   │   ├── AuthViewModel.kt         # Auth logic
│   │   └── AuthUiState.kt           # State model
│   ├── home/
│   │   ├── HomeScreen.kt            # Listings feed
│   │   ├── HomeViewModel.kt         # Feed logic
│   │   └── HomeUiState.kt           # State model
│   ├── listingdetail/
│   │   └── ListingDetailScreen.kt   # Property details
│   ├── booking/
│   │   └── BookingPaymentScreen.kt  # Payment flow
│   ├── chat/
│   │   └── ChatScreen.kt            # Messaging
│   └── navigation/
│       └── Navigation.kt            # Route setup
└── data/
    └── MockDataGenerator.kt         # Test data
```

## Mock Data

The app comes pre-configured with comprehensive mock data:

### Students (50 records)
```
- Names: Kagiso, Nobantu, Tshotlo, Kesebone, Thabo, etc.
- IDs: STU00001 to STU00050
- Institutions: All 5 major institutions in Botswana
- Spread across different study years
```

### Providers (2 records)
```
- Mpho Ndlovu (PRV00001)
- Boitumelo Khubone (PRV00002)
```

### Listings (35 records)
```
- Prices: 1800 - 5500 BWP
- Types: EN_SUITE, SHARED, STUDIO, FLAT
- Locations: Gaborone, Tlokweng, Francistown
- Amenities: WiFi, AC, Kitchen, Parking, Pool, Security, etc.
- Deposits: 500 - 2000 BWP
```

**Generator Location:** `data/MockDataGenerator.kt`

## UI Previews

All screens have Compose preview functions. View them in Android Studio:

**Auth Previews:**
- `AuthScreenLoginPreview()`
- `AuthScreenSignUpPreview()`
- `AuthInputPreview()`
- `AuthTabSelectedPreview()`

**Home Previews:**
- `ListingCardPreview()`
- `ListingCardPreviewHighPrice()`
- `UniversityChipSelectedPreview()`
- `HomeTopBarPreview()`

**Detail Previews:**
- `ListingDetailScreenPreview()`

**Booking Previews:**
- `BookingPaymentScreenPreview()`
- `ReceiptScreenPreview()`

**Chat Previews:**
- `ChatScreenPreview()`
- `ChatListScreenPreview()`

## Firebase Integration (For Production)

### Setup
1. Create project at [firebase.google.com](https://console.firebase.google.com)
2. Add Android app: `com.example.studentnestfinder`
3. Download `google-services.json`
4. Place in `app/` folder
5. Uncomment in `build.gradle.kts`:
   ```kotlin
   alias(libs.plugins.google.services)
   ```

### Usage
- **Chat:** Messages sync to Firestore in real-time
- **Auth:** Optional Firebase authentication
- **Notifications:** Push messages when you get a chat

## Testing User Flows

### 1. Authentication Flow
```
Tap App
→ See Login screen
→ Enter credentials (any string accepted)
→ Tap Login/Register
→ Navigate to Home
```

### 2. Browse Listings
```
Home Screen
→ Scroll through listings
→ Tap university filter chips
→ Use search bar
→ Tap a listing
```

### 3. View Details & Reserve
```
Listing Detail Screen
→ Read all information
→ Scroll amenities
→ Tap "Reserve Now"
→ Enter payment details
→ Tap "Confirm Payment"
```

### 4. View Receipt
```
Receipt appears
→ Shows confirmation
→ Tap "Continue to Chat"
→ Navigate to chat screen
```

### 5. Send Messages
```
Chat Screen
→ See conversation history
→ Type message
→ Tap send
→ See message appear
```

## Color Scheme – Gaborone Edition

| Role | Name | Hex | Usage |
|------|------|-----|-------|
| Primary | Gaborone Clay | `#B84A2D` | Buttons, active states, key accents |
| Secondary | Mokolodi Green | `#2F6B4A` | FAB, success states, verification badge, chips |
| Tertiary | Savanna Gold | `#D49A2A` | Price tags, "new" badges, rating stars |
| Background | Soft Kalahari Sand | `#F9F6F0` | Main screen background |
| Surface | Ivory White | `#FFFFFF` | Cards, sheets, dialogs |
| On Primary | White | `#FFFFFF` | Text/icons on primary colour |
| On Background | Charcoal Dusk | `#2E2E2E` | Primary text |
| Subtext | Stone Gray | `#6B5E54` | Secondary text, captions, icons |
| Error | Acacia Thorn Red | `#C13C3C` | Alerts, error messages |
| Divider | Dusty Border | `#E2DCD5` | Borders, separators |

### Typography

The app uses a Material 3 `Typography` configuration built on the system **sans-serif** font stack
(Roboto on most Android devices).  The code is structured to accept **SF Pro** font files when
they are available under an appropriate licence:

1. Place the `.ttf` / `.otf` files in `app/src/main/res/font/`.
2. Declare a `FontFamily` in `AppTheme.kt` pointing to those files.
3. Replace `FontFamily.Default` in `AppFontFamily` with the new family.

### Switching the theme

All colours and typography are centralised in:

* `app/src/main/java/…/ui/theme/AppTheme.kt` – Compose colour scheme + typography
* `app/src/main/res/values/colors.xml` – XML colour resources (for View-based theming)
* `app/src/main/res/values/themes.xml` – light XML theme
* `app/src/main/res/values-night/themes.xml` – dark XML theme

## Navigation Routes

```
auth                        → Login/Register
home                        → Listings feed
listing/{listingId}         → Property details
booking/{listingId}         → Payment screen
chat_list                   → Conversations
chat/{conversationId}/{name}→ Chat screen
```

## Common Issues & Solutions

### Issue: App doesn't build
**Solution:** 
```bash
./gradlew clean
./gradlew build
```

### Issue: Previews don't show
**Solution:** 
- Click "Preview" in the code editor
- Or press `Shift + Alt + P`
- Ensure you're in a `@Preview` function
- Try invalidating caches: File → Invalidate Caches

### Issue: Firebase errors
**Solution:**
- Ensure `google-services.json` is in `app/` folder
- Firebase plugin must be uncommented in build.gradle
- Run `./gradlew clean build` again

## Performance Notes

- ✅ Uses LazyColumn for efficient list rendering
- ✅ LazyRow for horizontal scrolling
- ✅ StateFlow for reactive updates (no recomposition overhead)
- ✅ Room queries with Flow for real-time data
- ✅ Coil image loading configured (ready to use)

## Keyboard Shortcuts

| Action | Shortcut |
|--------|----------|
| Format Code | Ctrl + Alt + L |
| Rerun Preview | Ctrl + F5 |
| Build | Ctrl + F9 |
| Run App | Shift + F10 |
| Device Manager | Shift + Ctrl + Q |

## Future Enhancements

- [ ] Image uploads & gallery view
- [ ] Real Firebase Firestore chat sync
- [ ] Push notifications
- [ ] Location mapping
- [ ] User ratings & reviews
- [ ] Advanced filters (distance, price range)
- [ ] Payment gateway integration
- [ ] Video property tours
- [ ] Landlord dashboard
- [ ] Smart notifications for preference matches

## Contributing

This is a student project for CSE201. To add features:

1. Create a new branch: `git checkout -b feature/your-feature`
2. Follow the existing architecture pattern
3. Add Preview functions for all Composables
4. Test thoroughly on the Nexus 5 emulator
5. Commit with clear messages

## License

Educational project - Student Accommodation Finder for CSE201 Module

## Support

For issues or questions:
1. Check the `PROJECT_COMPLETION.md` for detailed feature docs
2. Review the code comments in each file
3. Check Android Studio's error messages
4. Clear cache and rebuild if needed

---

**Last Updated:** April 8, 2026  
**Status:** ✅ Ready for Production Testing

