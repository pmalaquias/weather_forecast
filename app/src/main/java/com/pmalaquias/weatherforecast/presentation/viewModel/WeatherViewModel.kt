package com.pmalaquias.weatherforecast.presentation.viewModel

import android.util.Log
import androidx.compose.foundation.gestures.forEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmalaquias.weatherforecast.BuildConfig
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.local.db.SavedCityEntity
import com.pmalaquias.weatherforecast.data.repositories.WeatherRepositoryImpl
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
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    //private val locationProvider = LocationProvider(application.applicationContext)
    //private val weatherRepository = WeatherRepositoryImpl(locationProvider)

    // Variable to hold the UI state of the weather forecast screen
    val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        fetchWeather()
        fetchForecastData()
        observeSavedCities()
    }

    /**
     * Observa o Flow de cidades salvas do repositório e atualiza o uiState
     * sempre que a lista no banco de dados mudar.
     */
    /**
     * Observa o Flow de SavedCityEntity do repositório,
     * busca WeatherData atual para cada uma, e atualiza o uiState,
     * preservando a "localização atual" e marcando estas como isFromCurrentLocation = false.
     */
    private fun observeSavedCities() {
        weatherRepository.getSavedCities() // Isso retorna Flow<List<SavedCityEntity>>
            .onEach { savedCitiesEntities ->
                val apiKey = BuildConfig.WEATHER_API_KEY

                // Busca WeatherData para cada SavedCityEntity de forma concorrente
                val userSavedWeatherItemsDeferred = savedCitiesEntities.map { entity ->
                    viewModelScope.async { // Executa cada busca em uma corrotina filha
                        try {
                            // Usamos getWeatherDataByCity para buscar os dados completos
                            // Esta função no repositório já deve retornar WeatherData
                            weatherRepository.getWeatherDataByCity(apiKey, entity.cityName)
                                ?.copy(isFromCurrentLocation = false) // GARANTE que seja false
                        } catch (e: Exception) {
                            Log.e("WeatherViewModel", "Erro ao buscar dados para cidade salva: ${entity.cityName}", e)
                            null // Retorna null se houver erro para esta cidade específica
                        }
                    }
                }
                // Espera todas as buscas concorrentes e filtra os resultados nulos (falhas)
                val userSavedWeatherItems = userSavedWeatherItemsDeferred.awaitAll().filterNotNull()

                _uiState.update { currentState ->
                    // Pega a cidade da localização atual que já pode estar no uiState (de fetchWeather)
                    val currentLocationWeather = currentState.savedCities.find { it.isFromCurrentLocation }

                    val finalList = mutableListOf<WeatherData>()

                    // 1. Adiciona a cidade da localização atual primeiro, se existir
                    currentLocationWeather?.let {
                        finalList.add(it)
                    }

                    // 2. Adiciona as cidades salvas pelo usuário (userSavedWeatherItems)
                    // Evita adicionar uma cidade salva se ela for a mesma que a currentLocationWeather
                    // (para não ter duplicatas visuais, já que a currentLocationWeather tem prioridade com isFromCurrentLocation = true)
                    userSavedWeatherItems.forEach { savedCity ->
                        if (currentLocationWeather == null || savedCity.location.name != currentLocationWeather.location.name) {
                            finalList.add(savedCity)
                        }
                        // Se a cidade salva TEM o mesmo nome da localização atual, mas a currentLocationWeather
                        // AINDA não foi definida (ex: fetchWeather ainda não rodou ou falhou),
                        // podemos adicionar a versão do banco (com isFromCurrentLocation = false).
                        // No entanto, fetchWeather() ao ter sucesso vai sobrescrever/priorizar.
                        // A lógica acima já cobre isso: se currentLocationWeather é null, adiciona.
                    }
                    currentState.copy(savedCities = finalList)
                }
            }
            .catch { e ->
                // Este catch é para erros ao observar o Flow do banco de dados (ex: DB fechado)
                Log.e("WeatherViewModel", "Erro ao observar cidades salvas do DB", e)
                _uiState.update { it.copy(errorMessage = R.string.error_database_read) }
            }
            .launchIn(viewModelScope) // Lança a coleta do Flow no escopo do ViewModel
    }

    /**
     * Carrega os dados do tempo para a localização atual do dispositivo.
     */
    fun loadWeatherForCurrentLocation() {
        if (_uiState.value.isInitialLoading || _uiState.value.isRefreshing) return
        _uiState.update { it.copy(isInitialLoading = true) }

        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val weatherResult = weatherRepository.getCurrentWeatherData(apiKey)
            val forecastResult = weatherRepository.getForecastData(apiKey, 7)

            _uiState.update {
                it.copy(
                    isInitialLoading = false,
                    weatherData = weatherResult,
                    forecastData = forecastResult,
                    errorMessage = if (weatherResult == null) R.string.error_fetching_data else null
                )
            }
        }
    }

    // Dentro da classe WeatherViewModel

    fun fetchWeather() { // Renomeado de loadWeatherForCurrentLocation para ser o principal fetch
        if (_uiState.value.isInitialLoading || _uiState.value.isRefreshing) return // Evita múltiplas chamadas
        _uiState.update { it.copy(isInitialLoading = true, isRefreshing = true, errorMessage = null) }

        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val currentWeatherDataResult = weatherRepository.getCurrentWeatherData(apiKey)

            if (currentWeatherDataResult != null) {
                // MODIFICADO: Marca como sendo da localização atual
                val weatherDataForCurrentLocation = currentWeatherDataResult.copy(isFromCurrentLocation = true)

                // NOVO: Salvar automaticamente a cidade da localização atual no banco de dados
                // Primeiro, criamos a SavedCityEntity a partir dos dados obtidos
                val cityEntityToSave = SavedCityEntity(
                    cityName = weatherDataForCurrentLocation.location.name,
                    country = weatherDataForCurrentLocation.location.country,
                    latitude = weatherDataForCurrentLocation.location.lat,
                    longitude = weatherDataForCurrentLocation.location.lon
                    // O campo 'addedAt' na SavedCityEntity geralmente tem um valor padrão,
                    // então não precisamos especificá-lo aqui a menos que queiramos um valor diferente.
                )
                // Agora, chamamos o repositório para salvar esta entidade.
                // Se a cidade já existir (baseado na PrimaryKey cityName),
                // o Room (com OnConflictStrategy.REPLACE, que é comum) a substituirá.
                weatherRepository.saveCity(cityEntityToSave)
                // FIM DO NOVO BLOCO PARA SALVAR

                // A lógica existente para atualizar o uiState continua
                _uiState.update { currentState ->
                    // Remove qualquer outra cidade que possa estar marcada como "localização atual"
                    // e não seja a que acabamos de buscar (caso haja alguma inconsistência)
                    // E também remove a cidade atual da lista 'otherSavedCities' se ela já estava lá
                    // (vinda de observeSavedCities) para evitar duplicatas visuais antes que observeSavedCities reaja.
                    val otherSavedCities = currentState.savedCities.filter {
                        !it.isFromCurrentLocation && it.location.name != weatherDataForCurrentLocation.location.name
                    }

                    val updatedSavedCitiesList = mutableListOf(weatherDataForCurrentLocation) // Localização atual no topo
                    updatedSavedCitiesList.addAll(otherSavedCities)

                    currentState.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        weatherData = weatherDataForCurrentLocation, // Exibe detalhes da localização atual
                        savedCities = updatedSavedCitiesList, // Atualiza a lista para a HomeScreen
                        errorMessage = null
                    )
                }
                // Agora que temos a localização atual, buscamos o forecast para ela
                fetchForecastDataForLocation(weatherDataForCurrentLocation.location.name)
            } else {
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        weatherData = null, // Mantém nulo se falhar
                        errorMessage = R.string.error_message_without_conection // ou R.string.error_fetching_data
                        // Não limpamos savedCities aqui, pode ainda ter cidades salvas pelo usuário
                    )
                }
            }
        }
    }


    /**
     * Carrega os dados do tempo para uma cidade específica (selecionada da pesquisa ou da lista de salvas).
     */
    fun loadWeatherForCity(cityName: String) {
        _uiState.update { it.copy(isInitialLoading = true) }
        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val weatherResult = weatherRepository.getWeatherDataByCity(apiKey, cityName)
            val forecastResult = weatherRepository.getForecastData(apiKey, 7) // TODO: Ajustar para buscar previsão por cidade

            _uiState.update {
                it.copy(
                    isInitialLoading = false,
                    weatherData = weatherResult,
                    forecastData = forecastResult,
                    errorMessage = if (weatherResult == null) R.string.error_fetching_data else null
                )
            }
        }
    }

    /**
     * Salva a cidade atualmente exibida na lista de cidades favoritas.
     */
    fun saveCurrentCity() {
        viewModelScope.launch {
            _uiState.value.weatherData?.location?.let { location ->
                val cityEntity = SavedCityEntity(
                    cityName = location.name,
                    country = location.country,
                    latitude = location.lat,
                    longitude = location.lon,
                    addedAt = System.currentTimeMillis()
                )
                weatherRepository.saveCity(cityEntity)
            }
        }
    }

    /**
     * Remove uma cidade da lista de cidades salvas.
     */
    fun deleteCity(cityName: String) {
        viewModelScope.launch {
            weatherRepository.deleteCity(cityName)
        }
    }

   /* // Function to fetch the current weather data
    fun fetchWeather() {

        _uiState.update { it.copy(isInitialLoading = true, isRefreshing = false, errorMessage = null) }



        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val result = weatherRepository.getCurrentWeatherData(apiKey)
            if (result != null) {
                // Success: update the UI state with the weather data
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        weatherData = result,
                        errorMessage = null
                    )
                }
            } else {
                // Error: update the UI state with an error message
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        weatherData = null,
                        errorMessage = R.string.error_message_without_conection
                    )
                }
            }
        }
    }
*/
    fun fetchForecastData(days: Int = 7) {

        _uiState.update { it.copy(isInitialLoading = true, isRefreshing = false, errorMessage = null) }

        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val result: ForecastData? = weatherRepository.getForecastData(apiKey, days)
            if (result != null) {
                // Success: update the UI state with the forecast data
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        forecastData = result,
                        errorMessage = null
                    )
                }
            } else {
                // Error: update the UI state with an error message
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        forecastData = null,
                        errorMessage = R.string.error_message_without_conection
                    )
                }
            }
        }
    }

    /**
     * Busca dados de previsão para a localização/cidade especificada.
     * Se nenhum nome de cidade for passado, usa a cidade em uiState.weatherData (se disponível).
     *
     * @param cityName O nome da cidade para a qual buscar a previsão. Se nulo, tenta usar a cidade
     *                 atualmente em _uiState.value.weatherData.
     * @param days O número de dias para a previsão (padrão é 7).
     */
    private fun fetchForecastDataForLocation(cityName: String?, days: Int = 7) {
        val targetCityName = cityName ?: _uiState.value.weatherData?.location?.name

        // Se não há um nome de cidade alvo (nem passado como parâmetro, nem no uiState), não faz nada.
        if (targetCityName.isNullOrBlank()) {
            // Opcional: Log ou alguma indicação de que a previsão não pôde ser buscada.
            // _uiState.update { it.copy(forecastData = null) } // Limpa previsão se não há cidade
            return
        }

        // Não alteramos o estado de loading principal aqui, pois isso é geralmente
        // para a carga inicial do tempo. A previsão pode ser considerada uma carga secundária.
        // _uiState.update { it.copy(isInitialLoading = true) } // Evitar, a menos que seja um refresh explícito de forecast

        viewModelScope.launch {
            val apiKey = BuildConfig.WEATHER_API_KEY
            // IMPORTANTE: Você precisará de uma função no seu repositório que busque a previsão
            // por nome de cidade, ou adaptar sua getForecastData existente.
            // Exemplo: weatherRepository.getForecastDataByCity(apiKey, targetCityName, days)
            // Se sua getForecastData atual usa lat/lon, e você tem lat/lon para targetCityName
            // (talvez do uiState.weatherData ou você precise buscá-lo primeiro), você pode usá-lo.
            // Para este exemplo, vou assumir que você tem ou criará getForecastDataByCity.
            val forecastResult: ForecastData? = try {
                weatherRepository.getForecastDataByCity(apiKey, targetCityName, days)
            } catch (e: Exception) {
                // Lidar com exceções da chamada de rede, se necessário
                null // Trata exceção como resultado nulo
            }


            if (forecastResult != null) {
                _uiState.update {
                    it.copy(
                        forecastData = forecastResult,
                        errorMessage = null // Limpa erro anterior se a busca da previsão for bem-sucedida
                        // ou it.errorMessage se você quiser preservar um erro de tempo principal
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        forecastData = null, // Limpa dados de previsão se a busca falhar
                        // Mantém o errorMessage existente ou define um erro específico para a previsão
                        errorMessage = it.errorMessage ?: R.string.error_fetching_forecast
                    )
                }
            }
        }
    }


    /**
     * Chamado pela HomeScreen quando um card de cidade é clicado.
     * Atualiza o uiState.weatherData para exibir os detalhes da cidade selecionada.
     * Também busca o forecast para esta cidade.
     */
    fun setCurrentWeather(selectedWeatherData: WeatherData) {
        // Se a cidade selecionada é a mesma que já está em weatherData,
        // e já temos dados de previsão, podemos optar por não fazer nada para evitar recargas desnecessárias.
        // Remova o `&& _uiState.value.forecastData != null` se quiser sempre recarregar o forecast ao clicar.
        if (_uiState.value.weatherData?.location?.name == selectedWeatherData.location.name &&
            _uiState.value.weatherData?.isFromCurrentLocation == selectedWeatherData.isFromCurrentLocation &&
            _uiState.value.forecastData != null) { // Só pula se já tem forecast e é a mesma cidade
            // return // Descomente se quiser pular a atualização se for a mesma cidade e já tiver previsão
        }

        _uiState.update {
            it.copy(
                weatherData = selectedWeatherData, // Define a cidade selecionada para exibição detalhada
                errorMessage = null // Limpa erros anteriores ao selecionar uma nova cidade
            )
        }
        // Busca o forecast para a cidade recém-selecionada
        fetchForecastDataForLocation(selectedWeatherData.location.name)
    }

    fun onSearchQueryChanged(query: String) {
        Log.d("WeatherViewModel", "onSearchQueryChanged called with query: '$query'") // << LOG 1
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.length < 3) {
            Log.d("WeatherViewModel", "Query too short, clearing results.") // << LOG 2
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L)
            Log.d("WeatherViewModel", "Debounce time passed, initiating search for: '$query'") // << LOG 3
            _uiState.update { it.copy(isSearching = true) }
            val apiKey = BuildConfig.WEATHER_API_KEY
            try {
                Log.d("WeatherViewModel", "Calling repository.searchCities for: '$query'") // << LOG 4
                val results = weatherRepository.searchCities(apiKey, query)
                Log.d("WeatherViewModel", "Search results from repository: ${results?.size ?: "null"} items") // << LOG 5
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = results ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Search failed in ViewModel", e) // << LOG 6
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = emptyList(),
                        // errorMessage = R.string.error_search_failed // Opcional
                    )
                }
            }
        }
    }

    // Add this function to your WeatherViewModel
    fun selectCityFromSearch(city: LocationInfo) { // city type should match your searchResults item type
        // Clear search state
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList(), isSearching = false) }
        // Load weather for the selected city
        loadWeatherForCity(city.name) // Assuming city.name is the correct identifier
    }
}