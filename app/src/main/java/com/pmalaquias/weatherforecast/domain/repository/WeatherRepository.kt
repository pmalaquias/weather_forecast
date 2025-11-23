package com.pmalaquias.weatherforecast.domain.repository

import com.pmalaquias.weatherforecast.data.local.db.SavedCityEntity
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import kotlinx.coroutines.flow.Flow

/**
 * Interface para aceder aos dados do tempo.
 */
interface WeatherRepository {
    /**
     * Busca os dados do tempo atuais.
     * @param apiKey A chave da API para autenticar a requisição.
     * @return WeatherData se bem-sucedido, ou null em caso de erro.
     */
    suspend fun getCurrentWeatherData(apiKey: String): WeatherData?

    /**
     * Busca a previsão do tempo para os próximos dias.
     * @param apiKey A chave da API para autenticar a requisição.
     * @param days O número de dias para a previsão.
     * @return ForecastData se bem-sucedido, ou null em caso de erro.
     */
    suspend fun getForecastData(apiKey: String, days: Int): ForecastData?

    suspend fun getWeatherDataByCity(apiKey: String, cityName: String): WeatherData?
    fun getSavedCities(): Flow<List<SavedCityEntity>>
    suspend fun saveCity(city: SavedCityEntity)
    suspend fun deleteCity(cityName: String)

    suspend fun searchCities(apiKey: String, query: String): List<LocationInfo>?

    suspend fun getForecastDataByCity(apiKey: String, cityName: String, days: Int): ForecastData?
}