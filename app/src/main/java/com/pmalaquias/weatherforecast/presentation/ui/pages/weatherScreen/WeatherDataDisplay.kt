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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.WeatherData

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