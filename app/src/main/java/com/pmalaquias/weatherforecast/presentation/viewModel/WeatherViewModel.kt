package com.pmalaquias.weatherforecast.presentation.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.repositories.WeatherRepositoryImpl
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state of the weather forecast screen.
 *
 * @param application Application instance used to obtain the context.
 *
 * This ViewModel uses a LocationProvider to get the user's current location
 * and a WeatherRepositoryImpl to fetch weather forecast data.
 *
 * Properties:
 * - [_uiState]: Internal Flow that stores the current state of the weather forecast screen.
 * - [uiState]: Flow exposed for observing the screen state.
 *
 * Features:
 * - Automatically fetches weather forecast data when initialized.
 * - The [fetchWeather] function retrieves weather forecast data,
 *   updating the screen state according to the result (success or error).
 */
class WeatherViewModel(
    application: Application
): ViewModel() {

    private val locationProvider = LocationProvider(application.applicationContext)
    private val weatherRepository = WeatherRepositoryImpl(locationProvider)

    // Variable to hold the UI state of the weather forecast screen
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    // Function to fetch the current weather data
     fun fetchWeather() {

         _uiState.update { it.copy(isLoading = true, errorMessage = null) }

         viewModelScope.launch {
             val result = weatherRepository.getCurrentWeatherData()
             if (result != null) {
                 // Success: update the UI state with the weather data
                 _uiState.update {
                        it.copy(
                            isLoading = false,
                            weatherData = result,
                            errorMessage = null
                        )
                 }
             }else{
                 // Error: update the UI state with an error message
                    _uiState.update {
                            it.copy(
                                isLoading = false,
                                weatherData = null,
                                errorMessage = "Falha ao buscar dados do tempo. Verifique sua conexão ou tente novamente"
                            )
                    }
             }
         }
     }
}