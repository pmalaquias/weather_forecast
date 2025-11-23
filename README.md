# Weather Forecast App 🌦️

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue.svg?style=for-the-badge)


A modern Android application built with **Jetpack Compose** that provides real-time weather forecasts. This project demonstrates best practices in modern Android development, including Clean Architecture, MVVM, and [Material You Expressive](https://m3.material.io/).

## Table of Contents
* [Overview](#-overview)
* [Features](#-features)
* [Tech Stack](#-tech-stack)
* [Architecture](#-architecture)
* [Setup Instructions](#-setup-instructions)
* [Screenshots](#-screenshots)
* [Contributing](#-contributing)
* [License](#-license)

## 📱 Overview

The Weather Forecast App allows users to check the current weather and forecast for their location or any searched city. It leverages the [WeatherAPI](https://www.weatherapi.com/) to fetch accurate meteorological data.

## ✨ Features

*   **Current Weather:** Display real-time temperature, condition, humidity, wind speed, and more.
*   **Location-Based:** Automatically fetches weather for the user's current location.
*   **Search Functionality:** Search for weather conditions in any city worldwide.
*   **Modern UI:** Built entirely with Jetpack Compose and Material Design 3 for a sleek, expressive interface.
*   **Edge-to-Edge:** Immersive experience with transparent system bars.

## 🛠️ Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles
*   **Asynchronous Programming:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Network:** [Retrofit](https://square.github.io/retrofit/) with [Gson](https://github.com/google/gson)
*   **Dependency Injection:** Manual Dependency Injection (demonstrating core principles)
*   **Local Storage:** [Room Database](https://developer.android.com/training/data-storage/room) (configured for future caching)
*   **Location:** [Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary)
*   **Image Loading:** [Coil](https://coil-kt.github.io/coil/compose/)

## 🏗️ Architecture

The app follows the **Clean Architecture** guidelines, separating concerns into distinct layers:

*   **Presentation Layer:** Contains UI components (Composables) and ViewModels. Handles user interaction and data display.
*   **Domain Layer:** Contains business logic, Use Cases (implied), and Repository interfaces. Pure Kotlin code, independent of Android frameworks.
*   **Data Layer:** Implements Repositories, handles data sources (API, Database), and maps data to domain models.

## 🚀 Setup Instructions

To run this project locally, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/pmalaquias/weather_forecast.git
    ```
2.  **Open in Android Studio:**
    Open the project in the latest version of Android Studio (Koala or later recommended).
3.  **Get an API Key:**
    *   Sign up for a free account at [WeatherAPI.com](https://www.weatherapi.com/).
    *   Get your API Key.
4.  **Configure API Key:**
    *   Create a file named `local.properties` in the root directory of the project (if it doesn't exist).
    *   Add the following line to `local.properties`:
        ```properties
        WEATHER_API_KEY=your_api_key_here
        ```
5.  **Build and Run:**
    Sync the project with Gradle files and run the app on an emulator or physical device.

## 📸 Screenshots

| Home Screen | Search | Details |
|:-----------:|:------:|:-------:|
| *(Add Screenshot)* | *(Add Screenshot)* | *(Add Screenshot)* |

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
