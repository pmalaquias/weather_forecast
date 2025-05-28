package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.CurrentWeather
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherCondition
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme

/**
 * Displays weather data in a vertically arranged column.
 *
 * @param weatherData The weather data to display, including location, temperature, condition, and other details.
 *
 * This composable shows:
 * - The location name and country.
 * - The current temperature in Celsius.
 * - The weather condition description and icon.
 * - The "feels like" temperature.
 * - The humidity percentage.
 * - The wind speed in km/h.
 */
@Composable
fun WeatherDataDisplay(weatherData: WeatherData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Local: ${weatherData.location.name}, ${weatherData.location.country}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${weatherData.current.tempCelcius}°C", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = weatherData.current.condition.text, fontSize = 18.sp)


        AsyncImage(
            model = weatherData.current.condition.iconUrl,
            contentDescription = weatherData.current.condition.text,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Sensação: ${weatherData.current.feelslikeCelcius}°C")
        Text(text = "Umidade: ${weatherData.current.humidity}%")
        Text(text = "Vento: ${weatherData.current.windKph} km/h")
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherDataDisplayPreview(){
    AppTheme {
        WeatherDataDisplay(
            weatherData = PreviewData.sampleWeatherData
        )
    }
}

object PreviewData {
    val sampleWeatherData = WeatherData(
        location = LocationInfo(
            name = "Ouro Preto",
            region = "Minas Gerais",
            country = "Brasil",
            localtime = "2025-05-26 20:00"
        ),
        current = CurrentWeather(
            tempCelcius = 22.5,
            condition = WeatherCondition(
                text = "Parcialmente Nublado",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
            ),
            windKph = 10.2,
            humidity = 65,
            feelslikeCelcius = 21.8,
            isDay = 1
        )
    )

    val loadingState = WeatherUIState(isLoading = true)

    val errorState = WeatherUIState(errorMessage = "Falha ao carregar dados (Preview)")

    val successState = WeatherUIState(weatherData = sampleWeatherData)
}