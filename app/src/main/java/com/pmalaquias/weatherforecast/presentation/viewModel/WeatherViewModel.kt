package com.pmalaquias.weatherforecast.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmalaquias.weatherforecast.BuildConfig
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.data.local.db.SavedCityEntity
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.domain.repository.WeatherRepository
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing and providing weather and forecast data to the UI.
 */
class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Observe saved cities from DB
        observeSavedCities()
        // fetchWeather() is now triggered by UI once permissions are confirmed
    }

    private fun observeSavedCities() {
        weatherRepository.getSavedCities()
            .onEach { savedCitiesEntities ->
                val apiKey = BuildConfig.WEATHER_API_KEY

                val userSavedWeatherItemsDeferred = savedCitiesEntities.map { entity ->
                    viewModelScope.async {
                        try {
                            weatherRepository.getWeatherDataByCity(apiKey, entity.cityName)
                                ?.copy(isFromCurrentLocation = false)
                        } catch (e: Exception) {
                            Log.e("WeatherViewModel", "Erro ao buscar dados para cidade salva: ${entity.cityName}", e)
                            null
                        }
                    }
                }
                val userSavedWeatherItems = userSavedWeatherItemsDeferred.awaitAll().filterNotNull()

                _uiState.update { currentState ->
                    val currentLocationWeather = currentState.savedCities.find { it.isFromCurrentLocation }
                    val finalList = mutableListOf<WeatherData>()

                    currentLocationWeather?.let { finalList.add(it) }

                    userSavedWeatherItems.forEach { savedCity ->
                        if (currentLocationWeather == null || savedCity.location.name != currentLocationWeather.location.name) {
                            finalList.add(savedCity)
                        }
                    }
                    currentState.copy(savedCities = finalList)
                }
            }
            .catch { e ->
                Log.e("WeatherViewModel", "Erro ao observar cidades salvas do DB", e)
                _uiState.update { it.copy(errorMessage = R.string.error_database_read) }
            }
            .launchIn(viewModelScope)
    }

    fun fetchWeather() {
        if (_uiState.value.isInitialLoading || _uiState.value.isRefreshing) return
        _uiState.update { it.copy(isInitialLoading = true, isRefreshing = true, errorMessage = null) }

        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            try {
                val currentWeatherDataResult = weatherRepository.getCurrentWeatherData(apiKey)

                if (currentWeatherDataResult != null) {
                    val weatherDataForCurrentLocation = currentWeatherDataResult.copy(isFromCurrentLocation = true)

                    val cityEntityToSave = SavedCityEntity(
                        cityName = weatherDataForCurrentLocation.location.name,
                        country = weatherDataForCurrentLocation.location.country,
                        latitude = weatherDataForCurrentLocation.location.lat,
                        longitude = weatherDataForCurrentLocation.location.lon
                    )
                    weatherRepository.saveCity(cityEntityToSave)

                    _uiState.update { currentState ->
                        val otherSavedCities = currentState.savedCities.filter {
                            !it.isFromCurrentLocation && it.location.name != weatherDataForCurrentLocation.location.name
                        }

                        val updatedSavedCitiesList = mutableListOf(weatherDataForCurrentLocation)
                        updatedSavedCitiesList.addAll(otherSavedCities)

                        currentState.copy(
                            isInitialLoading = false,
                            isRefreshing = false,
                            weatherData = weatherDataForCurrentLocation,
                            savedCities = updatedSavedCitiesList,
                            errorMessage = null
                        )
                    }
                    fetchForecastDataForLocation(weatherDataForCurrentLocation.location.name)
                } else {
                    _uiState.update {
                        it.copy(
                            isInitialLoading = false,
                            isRefreshing = false,
                            errorMessage = R.string.error_message_without_conection
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        errorMessage = R.string.error_fetching_data
                    )
                }
            }
        }
    }

    fun loadWeatherForCity(cityName: String) {
        _uiState.update { it.copy(isInitialLoading = true) }
        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val weatherResult = weatherRepository.getWeatherDataByCity(apiKey, cityName)
            
            if (weatherResult != null) {
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        weatherData = weatherResult,
                        errorMessage = null
                    )
                }
                fetchForecastDataForLocation(cityName)
            } else {
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        errorMessage = R.string.error_fetching_data
                    )
                }
            }
        }
    }

    fun saveCurrentCity() {
        viewModelScope.launch {
            _uiState.value.weatherData?.location?.let { location ->
                val cityEntity = SavedCityEntity(
                    cityName = location.name,
                    country = location.country,
                    latitude = location.lat,
                    longitude = location.lon
                )
                weatherRepository.saveCity(cityEntity)
            }
        }
    }

    fun deleteCity(cityName: String) {
        viewModelScope.launch {
            weatherRepository.deleteCity(cityName)
        }
    }

    private fun fetchForecastDataForLocation(cityName: String?, days: Int = 7) {
        val targetCityName = cityName ?: _uiState.value.weatherData?.location?.name
        if (targetCityName.isNullOrBlank()) return

        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            try {
                val forecastResult = weatherRepository.getForecastDataByCity(apiKey, targetCityName, days)
                if (forecastResult != null) {
                    _uiState.update {
                        it.copy(
                            forecastData = forecastResult,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            forecastData = null,
                            errorMessage = it.errorMessage ?: R.string.error_fetching_forecast
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching forecast", e)
            }
        }
    }

    fun setCurrentWeather(selectedWeatherData: WeatherData) {
        _uiState.update {
            it.copy(
                weatherData = selectedWeatherData,
                errorMessage = null
            )
        }
        fetchForecastDataForLocation(selectedWeatherData.location.name)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.length < 3) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L)
            _uiState.update { it.copy(isSearching = true) }
            val apiKey = BuildConfig.WEATHER_API_KEY
            try {
                val results = weatherRepository.searchCities(apiKey, query)
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = results ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Search failed", e)
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = emptyList()
                    )
                }
            }
        }
    }

    fun selectCityFromSearch(city: LocationInfo) {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList(), isSearching = false) }
        loadWeatherForCity(city.name)
    }
}
