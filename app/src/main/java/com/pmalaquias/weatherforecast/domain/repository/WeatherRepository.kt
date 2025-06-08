package com.pmalaquias.weatherforecast.domain.repository

import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.WeatherData

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
}