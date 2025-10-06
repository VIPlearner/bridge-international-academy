# Implementation Overview

## Architecture

This Android application follows **Clean Architecture** with three layers:

- **Presentation** (`ui/`): Jetpack Compose screens with ViewModels
- **Domain** (`domain/`): Business entities and use cases  
- **Data** (`data/`): Repository pattern with Room (local) + Retrofit (network)

**Key Pattern**: Offline-first with automatic sync via WorkManager every 30 minutes.

## Requirements Status

✅ **View list of pupils** - Compose list with real-time Flow updates  
✅ **Add new pupil** - Form with validation and location services  
✅ **Offline support** - Room database + background sync when online  

## Project Structure

```
app/src/main/java/com/bridge/androidtechnicaltest/
├── App.kt                          # Application class with Hilt setup
├── config/                         # detekt config
├── data/
│   ├── datastore/                  # Preferences and sync state storage
│   ├── db/                         # Room database, DAOs, entities
│   ├── mapper/                     # mappers
│   ├── network/                    # Retrofit API interfaces
│   ├── repository/                 # Repository implementations
│   └── sync/                       # Synnchronization logic with WorkManager
├── domain/
│   ├── entity/                     # entities
│   ├── mapper/                     # Domain mappers
│   └── usecase/                    # use cases
├── ui/
│   ├── screens/                    # Compose screens (list, detail)
│   ├── navigation/                 # Navigation setup
│   ├── theme/                      # Material Design theme (materialKolor.com)
│   └── common/                     # Reusable UI components
└── utils/                          # Utility classes and extensions
```

## Configuration

Required in `local.properties`:
```properties
GEOCODING_API_KEY=your_geocoding_api_key
PUPIL_API_REQUEST_ID=your_pupil_api_request_id
```

**Build Requirements:**
- Min SDK: 23
- Target SDK: 34
- Kotlin with Jetpack Compose

**Key Dependencies:** Hilt (DI), Room (DB), Retrofit (Network), WorkManager (Sync), Coil (Images)
