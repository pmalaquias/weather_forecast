package com.pmalaquias.weatherforecast.presentation.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.repositories.WeatherRepositoryImpl
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing and providing weather and forecast data to the UI.
 *
 * @param application The Application context used for location and repository initialization.
 *
 * This ViewModel:
 * - Initializes a [LocationProvider] and [WeatherRepositoryImpl] to fetch weather data.
 * - Exposes a [StateFlow] of [WeatherUIState] to represent the UI state, including loading, error, and data.
 * - Automatically fetches current weather and forecast data upon initialization.
 * - Provides [fetchWeather] to retrieve current weather and update the UI state accordingly.
 * - Provides [fetchForecastData] to retrieve weather forecast for a specified number of days (default is 7).
 * - Handles loading and error states, updating the UI state with appropriate messages.
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
        fetchForecastData()
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

    fun fetchForecastData(days: Int = 7){
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result: ForecastData? = weatherRepository.getForecastData(days)
            if (result != null) {
                // Success: update the UI state with the forecast data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        forecastData = result,
                        errorMessage = null
                    )
                }
            } else {
                // Error: update the UI state with an error message
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        forecastData = null,
                        errorMessage = "Falha ao buscar dados da previsão do tempo. Verifique sua conexão ou tente novamente"
                    )
                }
            }
        }
    }
}