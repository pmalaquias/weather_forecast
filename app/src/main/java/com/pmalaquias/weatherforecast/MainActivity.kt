package com.pmalaquias.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherAppScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherViewModelFactory
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel

/**
 * MainActivity is the entry point of the Weather Forecast application.
 * 
 * This activity initializes the [WeatherViewModel] using a [WeatherViewModelFactory] and sets up the UI
 * using Jetpack Compose. The UI is wrapped in an [AppTheme] and uses a [Scaffold] to provide basic
 * material design layout structure. The [WeatherAppScreen] composable is displayed as the main content,
 * receiving the [weatherViewModel] for data and logic handling.
 *
 * Lifecycle:
 * - On creation, edge-to-edge display is enabled and the Compose content is set.
 *
 * @see WeatherViewModel
 * @see WeatherViewModelFactory
 * @see WeatherAppScreen
 * @see AppTheme
 */
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherAppScreen(
                        viewModel = weatherViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

