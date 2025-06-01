package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDataDisplay(weatherData: WeatherData) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = weatherData.location.name) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,

                )
        },
        content = { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                Box (
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = weatherData.current.condition.text,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                    )
                }
                /*Text(
                    text = "Local: ${weatherData.location.name}, ${weatherData.location.country}",
                    fontSize = 20.sp
                )*/
                // Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weatherData.current.tempCelcius}°C",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))


                AsyncImage(
                    model = weatherData.current.condition.iconUrl,
                    contentDescription = weatherData.current.condition.text,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Sensação: ${weatherData.current.feelslikeCelcius}°C")

                /*Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(text = "Máxima: ")
                    Text(text = "Mínima:")
                }*/
                Text(text = "Umidade: ${weatherData.current.humidity}%")
                Text(text = "Vento: ${weatherData.current.windKph} km/h")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun WeatherDataDisplayPreview() {
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
            uv = 5,
            pressureMb = 1012.0,
            precipitationMm = 0.0
        )
    )

    val loadingState = WeatherUIState(isLoading = true)

    val errorState = WeatherUIState(errorMessage = "Falha ao carregar dados (Preview)")

    val successState = WeatherUIState(weatherData = sampleWeatherData)
}