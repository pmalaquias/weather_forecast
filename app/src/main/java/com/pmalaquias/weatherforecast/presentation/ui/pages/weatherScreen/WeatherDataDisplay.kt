package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.ForecastData
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
fun WeatherDataDisplay(
    weatherData: WeatherData,
    forecastData: ForecastData?
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val todayForecastData = forecastData?.dailyForecasts?.firstOrNull()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = weatherData.location.name) },
                /*navigationIcon = {
                    IconButton(onClick = { */
                /* Handle back navigation */
                /* }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                },*/
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
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(text = "Máxima: ${todayForecastData?.maxTempCelcius} ")
                    Text(text = "Mínima:${todayForecastData?.minTempCelcius}")
                }

                ForecastDisplayData(forecastData = forecastData)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Umidade: ${weatherData.current.humidity}%")
                Text(text = "Vento: ${weatherData.current.windKph} km/h")
                Text(text = "Direção do vento: ${weatherData.current.windDir}")
                Text(text = "UV: ${weatherData.current.uv}")
                Text(text = "Pressão: ${weatherData.current.pressureMb} mb")
                Text(text = "Precipitação: ${weatherData.current.precipitationMm} mm")

            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun WeatherDataDisplayPreview() {
    AppTheme {
        WeatherDataDisplay(
            weatherData = PreviewData.sampleWeatherData,
            forecastData = PreviewData.sampleForecastData
        )
    }
}
