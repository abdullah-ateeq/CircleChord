# CircleChord ◯

[![Android API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-purple.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**CircleChord** is an offline, high-precision Android calculator built with Jetpack Compose and Material 3. It calculates circle chord lengths, arc lengths, sector areas, diameters, and sagitta segment heights from radius and central angle inputs using a custom 50-digit high-precision trigonometric engine.

---

## 🌟 Key Features

- 📐 **Chord Length Calculation**: Instant calculation using $c = 2r \sin(\theta/2)$.
- 🎯 **Comprehensive Circle Geometry**:
  - **Diameter**: $d = 2r$
  - **Arc Length**: $s = r \cdot \theta_{\text{rad}}$
  - **Sector Area**: $A = \frac{1}{2} r^2 \theta_{\text{rad}}$
  - **Sagitta (Segment Height)**: $h = r(1 - \cos(\theta/2))$
- 📏 **Flexible Unit Conversion**:
  - **Metric Units**: Millimeters (`mm`), Centimeters (`cm`), Meters (`m`), Kilometers (`km`)
  - **Imperial Units**: Inches (`in`), Feet (`ft`), Yards (`yd`)
  - Mixed unit input and output support (e.g., input radius in `ft` and get chord in `m`).
- 🔢 **50-Digit High-Precision Math Engine**: Powered by `BigDecimal` with custom Taylor-series trigonometric computations ($sin$, $cos$, $rad$) for exact accuracy without floating-point rounding errors.
- 🎨 **Interactive Vector Diagram**: Visual representation of the circle, radius, chord, and central angle.
- 📝 **Step-by-Step Substitution**: Detailed calculation steps showing formula application and unit conversions.
- 📚 **Calculation History**: Save, restore, copy, and delete past calculations.
- ⚙️ **Customization Options**:
  - Precision control (0 to 12 decimal places).
  - Rounding modes (`HALF_UP`, `HALF_EVEN`, `DOWN`, `UP`).
  - Dark, Light, and System Default themes with Dynamic Color support.
  - Haptic feedback and visual toggles.
- 🔒 **100% Private & Offline**: Works completely offline without permissions, ads, or tracking.

---

## 📱 App Screens

1. **Home Screen**:
   - Interactive inputs for Radius and Central Angle ($0^\circ - 360^\circ$).
   - Unit selection bottom sheets.
   - Real-time calculation feedback and vector diagram visualization.
   - Expandable breakdown for Arc Length, Sector Area, Sagitta, and calculation steps.
2. **History Screen**:
   - Saved calculation records with date/time stamps.
   - Quick restore to calculator, copy formatted text, or individual record deletion.
3. **Settings Screen**:
   - Theme preferences (System, Light, Dark) and Dynamic Color toggle.
   - Numerical precision slider and rounding mode selection.
   - Display toggles for formula, diagram, and step-by-step breakdown.
4. **About Screen**:
   - Geometric definitions, privacy statement, and app feature summaries.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture domain layer
- **State Management**: `StateFlow` and `Kotlin Coroutines`
- **Navigation**: `Navigation Compose`
- **Math Engine**: Custom arbitrary-precision `BigDecimalMath` with Taylor-series expansion
- **SDK Target**: Android 16 (API Level 37) | Min SDK: Android 8.0 (API Level 26)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 11 or higher
- Android SDK 37

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/CircleChord.git
   cd CircleChord
   ```

2. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 📂 Project Structure

```
CircleChord/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/circlechord/
│   │   │   │   ├── data/           # Repositories & Preference storage
│   │   │   │   ├── domain/         # ChordCalculator, Formatters, Validators
│   │   │   │   ├── math/           # High-precision BigDecimal Math Engine
│   │   │   │   ├── ui/             # Jetpack Compose Screens, ViewModel, Theme
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/                # Drawables, mipmaps, strings
│   │   └── test/                   # Unit test suite for math, validation & storage
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 👨‍💻 Author

**Abdullah Ateeq**
- GitHub: [@abdullah-ateeq](https://github.com/abdullah-ateeq)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
