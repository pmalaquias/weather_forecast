package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel

/**
 * Composable function that displays the main weather app screen.
 *
 * This function observes the [uiState] from the provided [WeatherViewModel] and updates the UI accordingly:
 * - Shows a loading indicator when data is being fetched.
 * - Displays an error message and a retry button if an error occurs.
 * - Shows the weather data when available.
 * - Displays a default message for initial or unexpected states.
 *
 * @param viewModel The [WeatherViewModel] that provides the UI state and handles data fetching.
 */
@Composable
fun WeatherAppScreen(viewModel: WeatherViewModel) {
    // Observes the uiState from the ViewModel
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> {
                   Column {
                       Text(
                           text = "Error: ${uiState.errorMessage}",
                           color = MaterialTheme.colorScheme.error,
                           modifier = Modifier.padding(16.dp)
                       )
                       // Button to retry
                       Button(onClick = { viewModel.fetchWeather() }, modifier = Modifier.padding(top = 8.dp)) {
                           Text("Try Again")
                       }
                   }
                }
                uiState.weatherData != null -> {
                    // Displays the weather data
                    WeatherDataDisplay(weatherData = uiState.weatherData)
                }
                else -> {
                    // Initial or unexpected state, can show a text or loading
                    Text("Fetching weather data...")
                    // Or call viewModel.fetchWeatherData() if init is not enough
                }
            }
        }
    }
}

