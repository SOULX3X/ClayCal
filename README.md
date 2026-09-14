# 🗓️ Claycal — Tactile Claymorphism Calendar for Android

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%2B-informational.svg?style=flat)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue.svg?style=flat)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg?style=flat)](LICENSE)

**Claycal** is an offline-first calendar and daily schedule application designed with a **tactile claymorphism** design language. Crafted with modern Jetpack Compose and Material 3, Claycal reimagines digital scheduling as warm, organic clay blocks with realistic depth, soft bevels, and interactive tactile feedback.

---

## ✨ Features

### 📅 Four Intuitive View Modes
- **Month View**: Complete monthly grid with multi-event category dots, selected date agenda, and quick event creation.
- **Week View**: Horizontal week strip with event density badges and an hourly breakdown of the selected day.
- **Day View**: Focused hourly timeline (7:00 AM – 10:00 PM) with current-hour indicators and tap-to-mold event time slots.
- **Schedule (Agenda) View**: Chronological upcoming event feed grouped by day (Today, Tomorrow, and upcoming dates) with category filter chips.

### 📱 Adaptive & Responsive UI
- **Portrait Mode**: Optimized single-column layout with smooth scrolling and accessible touch targets (48dp+).
- **Landscape & Tablet Two-Pane Layout**: Automatically activates when width reaches 600dp or screen is rotated:
  - **Month View**: Left pane displays the month grid card; right pane displays selected date events and actions.
  - **Week View**: Left pane shows week selector and daily counters; right pane shows the day's timeline.
  - **Day View**: Left pane displays day metrics and category distributions; right pane shows the hourly schedule.
  - **Schedule View**: Left pane houses the category filter sidebar; right pane hosts the agenda list.
- **Edge-to-Edge**: Full support for Android 15+ edge-to-edge drawing (`WindowInsets.safeDrawing`).

### 🎨 Tactile Claymorphism Aesthetic
- **Custom Clay Components**: Custom-built `ClayCard`, `ClayButton`, `ClayPill`, and `ClayIconButton` with dual-layer directional drop shadows, gradient top-highlights, and soft inner bevels.
- **Organic Color Palette**: Warm marshmallow backgrounds, soft clay surfaces, terracotta accents, peach highlights, sage greens, and coral alerts.
- **Theme Modes**: Supports Light, Dark, and System Default themes.
- **Clay Accent Picker**: Personalize your calendar with Terracotta, Clay Peach, Mint Sage, Sky Slate, or Lilac Clay.

### 📝 Rich Event Management
- **Full Event Attributes**: Title, detailed notes, category, date, start time, end time, location, and priority level (High, Medium, Low).
- **Event Categories**: Pre-configured categories with distinct color codes and emojis (Work 💼, Personal 🏠, Health 🏃, Study 📚, Social 🎉, Finance 💰).
- **Task Completion**: Check off events and tasks with interactive completion checkmarks and strikethrough visual states.
- **Real-Time Search**: Search through event titles, locations, and descriptions instantly from the header.

### 🔒 Privacy & Offline-First
- **Local Persistence**: Powered by SQLite via **Android Room**. All data stays 100% private and on-device.
- **Zero Cloud Dependence**: Operates entirely offline without requiring user accounts or third-party tracking.

---

## 🏗️ Architecture & Tech Stack

Claycal follows modern Android development best practices and MVVM (Model-View-ViewModel) architecture:

```
app/src/main/java/com/example/
├── data/
│   ├── AppDatabase.kt          # Room SQLite Database instance & migrations
│   ├── CalendarDao.kt          # DAO for CRUD and reactive queries
│   ├── CalendarEventEntity.kt  # Room database entity
│   ├── CalendarRepository.kt   # Repository bridging DAO and ViewModel
│   └── ThemePreferences.kt     # DataStore/SharedPreferences for UI themes
├── model/
│   ├── CalendarEvent.kt        # Domain model for calendar events & priority
│   ├── Category.kt             # Event categories, emojis, and clay color mappings
│   ├── SimpleDate.kt           # Immutable calendar date helper with calculation utilities
│   └── SimpleTime.kt           # Time model (12-hour AM/PM formatting and math)
├── ui/
│   ├── calendar/
│   │   ├── CalendarHeader.kt   # Adaptive top app bar, search, and navigation
│   │   ├── CalendarStatsBanner.kt # Month overview statistics banner
│   │   ├── CalendarUiState.kt  # State holder for calendar screen state
│   │   ├── CalendarViewModel.kt# ViewModel managing reactive flows & business logic
│   │   ├── DayView.kt          # Hourly day timeline composable
│   │   ├── EventDetailDialog.kt# Tactile dialog for viewing event details
│   │   ├── EventDialog.kt      # Create / edit event form dialog
│   │   ├── EventItemCard.kt    # Reusable clay card for event items
│   │   ├── MonthView.kt        # Interactive month calendar grid & agenda
│   │   ├── ScheduleView.kt     # Filterable upcoming agenda list
│   │   ├── SettingsDialog.kt   # Theme & accent selection dialog
│   │   └── WeekView.kt         # Weekly calendar strip and schedule
│   ├── clay/
│   │   ├── ClayColors.kt       # Tactile color palettes, gradients, and shadows
│   │   └── ClayComponents.kt   # ClayCard, ClayButton, ClayPill, ClayTextField
│   └── theme/
│       ├── Color.kt            # Material 3 color definitions
│       ├── Theme.kt            # Compose Material 3 theme wrapper
│       └── Type.kt             # Typography scale
└── MainActivity.kt             # Entry point with edge-to-edge and responsive layout
```

---

## 🛠️ Requirements & Building

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer recommended
- **JDK**: Java 17 or Java 21
- **Android SDK**:
  - `compileSdk`: 36
  - `minSdk`: 26 (Android 8.0 Oreo or higher)
  - `targetSdk`: 36

### Build via Command Line
```bash
# Clone the repository
git clone https://github.com/your-username/claycal.git
cd claycal

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

### Install on Device / Emulator
```bash
./gradlew installDebug
```

### In-Place APK Updates
Claycal is configured for seamless in-place APK updates (`android:installLocation="auto"`, persistent Room migrations, and consistent application signing).
- **Updating**: When a new version APK is generated, simply tap on the downloaded `.apk` file on your device and select **"Update"** to update in-place without losing your calendar events or settings.
- **Requirements**: Ensure the `versionCode` in `build.gradle.kts` is bumped for each update (e.g. `versionCode = 6` for v1.0.5), and that both builds share the same signing certificate (e.g., debug build).

---

## 📐 Design Specifications

| Aspect | Implementation |
|---|---|
| **Design Style** | Tactile Claymorphism (Soft 3D, dual directional shadows, inner bevel) |
| **UI Toolkit** | Jetpack Compose (Kotlin DSL) |
| **Design System** | Material Design 3 (`androidx.compose.material3`) |
| **Minimum Touch Target** | 48dp x 48dp (WCAG & Material 3 compliant) |
| **Responsive Breakpoints** | `< 600dp` (Mobile portrait single pane), `≥ 600dp` (Tablet/Landscape two pane) |
| **Database** | Room 2.7.0 with Kotlin Symbol Processing (KSP) |
| **Async & Concurrency** | Kotlin Coroutines & `StateFlow` (`collectAsStateWithLifecycle`) |

---

## 📄 License

```
Copyright 2026 Claycal Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
