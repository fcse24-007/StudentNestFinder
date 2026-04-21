# StudentNestFinder Migration Plan (Compose/Firebase -> XML/Room)

## Goal
- Migrate UI from Jetpack Compose to XML (View system).
- Remove Firebase completely.
- Keep Kotlin and Room as the only persistence layer (including chat).
- Keep app runnable at each stage.

## Staged Delivery Plan

### Stage 0 (this PR): Baseline safety + diagnostics
- Capture current feature/screen inventory and core user flows.
- Record build/test verification commands and baseline status.
- Add a reusable verification checklist template for all migration PRs.

### Stage 1: Room as single source of truth (Compose kept temporarily)
- Refactor repositories so UI reads from Room-only flows.
- Add/complete Room entities + DAOs for any Firebase-backed data.
- Ensure chat messages are fully represented in Room.
- Add/confirm local seeding strategy for local-only behavior.

### Stage 2: Remove Firebase completely
- Remove Firebase SDK usage, initialization, and Gradle config.
- Replace real-time chat with Room-backed local threads/messages.
- Keep observable chat streams via Flow from Room.

### Stage 3: UI migration Compose -> XML (incremental)
- Introduce Activities/Fragments and XML layouts.
- Migrate navigation incrementally while keeping app runnable.
- Migrate screens in small batches.

### Stage 4: Cleanup
- Remove remaining Compose dependencies and dead code.
- Final docs + verification pass.

## Current App Inventory (Stage 0 Baseline)

### Entry + Navigation
- Entry point: `MainActivity` (Compose `setContent`) with `AppNavigation`.
- Navigation host: `ui/navigation/Navigation.kt`.
- Routes:
  - `auth`
  - `home`
  - `listing/{listingId}`
  - `booking/{listingId}`
  - `student_reservations`
  - `chat_list`
  - `chat/{conversationId}/{recipientName}`
  - `preferences`
  - `provider_dashboard`
  - `provider/listing/add`
  - `provider/listing/{listingId}/edit`
  - `help`
  - `faq`

### Main Compose Screens
- Auth, Home, Listing Detail
- Booking Payment, Student Reservations
- Chat List, Chat
- Preferences
- Provider Dashboard, Add/Edit Listing
- Help, FAQ

### Data/Persistence Baseline
- Room database: `AppDatabase` with seeded local users/listings/images.
- Chat repository currently mixes:
  - Firestore as upstream real-time source.
  - Room as local cache/observable source for UI.

### Firebase Usage Baseline
- `MainActivity` initializes Firebase (`FirebaseApp.initializeApp`).
- `ChatRepository` uses Firestore listeners/writes for chat sync + read receipts.
- Gradle includes Firebase plugins/dependencies in `app/build.gradle.kts`.

## Existing Test/Verification Surface
- Unit tests:
  - `AuthViewModelTest`
  - `BookingViewModelTest`
  - `ExampleUnitTest`
- Android tests:
  - `ExampleInstrumentedTest`
  - `UserDaoTest`

## Verification Checklist Template (use in every migration PR)
- [ ] `./gradlew clean build`
- [ ] `./gradlew test`
- [ ] `./gradlew connectedAndroidTest` (when emulator/device is available)
- [ ] Manual smoke flow:
  - [ ] Auth/login
  - [ ] Home list and listing detail
  - [ ] Booking flow
  - [ ] Chat list and chat send/receive
  - [ ] Preferences
  - [ ] Provider dashboard + add/edit listing

## Stage 0 Verification Notes
- Baseline command attempted in this environment:
  - `./gradlew clean build`
- Result: failed during plugin resolution (`com.android.application` `8.2.2` not resolved from configured repositories in sandbox run).
- No runtime/app code behavior changes were made in Stage 0; this PR focuses on migration diagnostics/documentation.

## Stage 1 Implementation Notes
- `ChatRepository` now uses Room as the single source of truth:
  - Message sends persist directly to Room.
  - Conversation observation reads directly from Room Flow.
  - Read receipts update Room only.
- Added local chat seed data in `AppDatabase` creation callback so chat screens have initial local conversations without Firebase.
- Added focused unit tests for room-first chat behavior (`ChatRepositoryTest`).

## Stage 1 Verification Notes
- Commands attempted in this environment:
  - `./gradlew clean build`
  - `./gradlew test`
- Result in sandbox: build/test could not proceed due plugin resolution failure for `com.android.application` `8.2.2` before compilation.
