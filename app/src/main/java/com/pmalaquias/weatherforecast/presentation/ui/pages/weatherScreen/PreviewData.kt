package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.domain.models.CurrentWeather
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherCondition
import com.pmalaquias.weatherforecast.domain.models.WeatherData

object PreviewData {
    val sampleWeatherData = WeatherData(
        location = LocationInfo(
            name = "Ouro Preto",
            region = "Minas Gerais",
            country = "Brasil",
            localtime = "2025-05-26 20:00",
            timezoneId = "America/Sao_Paulo",
            lat = -20.3833,
            lon = -43.5033
        ),
        current = CurrentWeather(
            tempCelcius = 22.5,
            condition = WeatherCondition(
                text = "Parcialmente Nublado",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                code = 116
            ),
            windKph = 10.2,
            humidity = 65,
            feelslikeCelcius = 21.8,
            isDay = 1,
            windDir = "Noroeste",
            uv = 5.0,
            pressureMb = 1012.0,
            precipitationMm = 0.0
        )
    )

    // Novo: Dados mock para ForecastData
    val sampleForecastData = ForecastData(
        dailyForecasts = listOf(
            DailyForecast(
                date = "2025-06-02", // Amanhã (exemplo)
                maxTempCelcius = 25.0,
                minTempCelcius = 15.0,
                avgTempCelcius = 20.0,
                condition = WeatherCondition( // Usando o WeatherCondition do domain/models
                    text = "Ensolarado com Nuvens",
                    iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    code = 116 // Código de condição de ensolarado com nuvens
                ),
                sunriseTime = "06:30", // Formato simplificado
                sunsetTime = "17:45",  // Formato simplificado
                chanceOfRain = 10,     // %
                totalPrecipMm = 0.0,
                uvIndex = 5.0,           // Índice UV
                humidity = 60.0          // Umidade em %
            ),
            DailyForecast(
                date = "2025-06-03", // Depois de amanhã
                maxTempCelcius = 23.5,
                minTempCelcius = 14.0,
                avgTempCelcius = 18.5,
                condition = WeatherCondition(
                    text = "Chuva Leve",
                    iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/296.png",
                    code = 296 // Código de condição de chuva leve
                ),
                sunriseTime = "06:31",
                sunsetTime = "17:45",
                chanceOfRain = 60,
                totalPrecipMm = 2.5,
                uvIndex = 3.0,           // Índice UV
                humidity = 70.0          // Umidade em %
            ),
            DailyForecast(
                date = "2025-06-04",
                maxTempCelcius = 26.0,
                minTempCelcius = 16.5,
                avgTempCelcius = 21.0,
                condition = WeatherCondition(
                    text = "Ensolarado",
                    iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/113.png",
                    code = 113 // Código de condição de ensolarado
                ),
                sunriseTime = "06:31",
                sunsetTime = "17:46",
                chanceOfRain = 0,
                totalPrecipMm = 0.0,
                uvIndex = 6.0,           // Índice UV
                humidity = 55.0          // Umidade em %
            )
        )
    )


    val loadingState = WeatherUIState(isInitialLoading = true, errorMessage = null)

    val errorState = WeatherUIState(errorMessage = R.string.error_message_without_conection)

    val successState = WeatherUIState(
        isInitialLoading = false,
        isRefreshing = false,
        weatherData = sampleWeatherData,
        forecastData = sampleForecastData,
        errorMessage = null
    )
}
