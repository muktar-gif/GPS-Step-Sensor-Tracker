# GPS Sensor Step Tracker

An Android fitness app that tracks your daily steps, distance, and location in the background using a foreground service. View your activity on a live dashboard, browse historical data, and configure units and permissions in settings.

---

## Features

- **Step Tracking** — Uses the device's step detector sensor to count steps in real time via a foreground service
- **Distance Tracking** — Calculates distance traveled and displays it in either miles or kilometers
- **Location Tracking** — Records location data throughout the day using GPS
- **Dashboard** — Live view of today's steps, distance, and current location
- **History** — Browse past activity with steps, distance, and location data merged and listed by date
- **Settings** — Toggle location tracking on/off and switch between miles and kilometers
- **Persistent Storage** — Activity data is saved using SharedPreferences (JSON) and Room database so it persists across sessions

---

## Screenshots

| Dashboard | History | Settings |
|---|---|---|
| <img width="428" height="856" alt="5cf6e8d0-b540-485b-aaf9-41b896886813" src="https://github.com/user-attachments/assets/a0eb9865-3717-40d7-8343-efe2166a742a" /> | <img width="428" height="856" alt="b4f53fd6-b045-4f7f-890a-87b7cc384c1e" src="https://github.com/user-attachments/assets/a69ba8f6-d45a-444c-a58c-2edbe0e47106" /> | <img width="428" height="856" alt="534b2457-d4b5-4a4b-a828-e5332b7496db" src="https://github.com/user-attachments/assets/156acd90-bccf-4175-8873-4bc5c0665286" /> |

---

## Architecture

The app uses a single-Activity + Fragment architecture with a bottom navigation bar:

- **MainActivity** — Hosts the bottom navigation and manages fragment switching
- **DashboardFragment** — Displays today's steps, distance, and location data fed from the foreground service
- **HistoryFragment** — Parses and displays stored step/distance/location records grouped by date
- **SettingsFragment** — Stores user preferences (location enabled, unit preference) via SharedPreferences

A **Foreground Service** runs persistently to:
- Register and listen to the device's step detector sensor
- Attach location listeners for GPS tracking
- Pass live data to the Dashboard
- Persist daily step/distance and location data as JSON via SharedPreferences

If location is disabled in Settings, all location-related UI elements are grayed out and no location data is collected or displayed.

---

## Tech Stack

| Component | Detail |
|---|---|
| Language | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |
| Build Tool | Gradle (Kotlin DSL) |
| UI | Fragments + ViewBinding + Jetpack Compose |
| Navigation | Bottom Navigation Bar |
| Location | Google Play Services Location |
| Persistence | Room 2.6.1 + SharedPreferences + Gson 2.13.0 |
| Architecture | ViewModel + LiveData (Jetpack Lifecycle) |

---

## Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable version recommended)
- A physical Android device (API 24+) — step detection requires real hardware sensors

---

## Getting Started

**1. Clone the repository**

```bash
git clone https://github.com/muktar-gif/Assignment2.git
```

**2. Open in Android Studio**

File → Open → select the `Assignment2` folder.

**3. Connect your device**

Plug in a physical Android device via USB or enable wireless debugging. A physical device is required for step detection.

**4. Run the app**

Click **Run** or press `Shift + F10`.

**5. Grant permissions**

On first launch, accept the prompts for:
- **Health / Activity Recognition** — required for step detection
- **Location** — required for GPS tracking (can be disabled in Settings)

---

## Permissions

| Permission | Purpose |
|---|---|
| `ACTIVITY_RECOGNITION` | Access step detector sensor |
| `ACCESS_FINE_LOCATION` | GPS-based location tracking |
| `FOREGROUND_SERVICE` | Keep step/location tracking alive in background |
| `POST_NOTIFICATIONS` | Show foreground service notification (Android 13+) |
