package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.domain.models.WindInfo
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import kotlin.math.roundToInt

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


val daySunnyColorBrush = listOf(
    Color(0xFF5698e2),
    Color(0xFF5e9cd9),
    Color(0xFF79add4)
)

val nightSunnyColorBrush = listOf(
    Color(0xFF1a1a2e),
    Color(0xFF16213e),
    Color(0xFF0f3460)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherDataDisplay(
    weatherData: WeatherData,
    forecastData: ForecastData?
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(

    )
    val todayForecastData: DailyForecast? =
        forecastData?.dailyForecasts?.firstOrNull() // Esta linha é segura

    val TAG_DISPLAY = "WeatherDataDisplayMain"
    Log.i(TAG_DISPLAY, "--- Rendering WeatherDataDisplay ---")
    Log.d(TAG_DISPLAY, "Location: ${weatherData.location.name}")
    //Log.d(TAG_DISPLAY, "ForecastData is null: ${forecastData == null}")
    if (forecastData != null) {
        Log.d(
            TAG_DISPLAY,
            "ForecastData.dailyForecasts is empty: ${forecastData.dailyForecasts.isEmpty()}"
        )
    }
    Log.d(TAG_DISPLAY, "TodayForecastData is null: ${todayForecastData == null}")

    /*Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Local: ${weatherData.location.name}, ${weatherData.location.country}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${weatherData.current.tempCelcius}°C", fontSize = 48.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = weatherData.current.condition.text, fontSize = 18.sp)

        // Para exibir o ícone (exemplo com Coil, adicione a dependência: implementation "io.coil-kt:coil-compose:2.6.0")
        // AsyncImage(
        //     model = weatherData.current.condition.iconUrl, // Lembre-se que o mapeamento já adiciona "https:"
        //     contentDescription = weatherData.current.condition.text,
        //     modifier = Modifier.size(64.dp)
        // )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Sensação: ${weatherData.current.feelslikeCelcius}°C")
        Text(text = "Umidade: ${weatherData.current.humidity}%")
        Text(text = "Vento: ${weatherData.current.windKph} km/h")
        Text(text= "Max: ${forecastData?.dailyForecasts?.maxByOrNull { it.maxTempCelcius ?: 0.0 }?.maxTempCelcius?.roundToInt() ?: "--°C"}, " +
                "Min: ${forecastData?.dailyForecasts?.minByOrNull { it.minTempCelcius ?: 0.0 }?.minTempCelcius?.roundToInt() ?: "--°C"}")
    }*/

    val temperature = remember { getTemperature(weatherData) }
    val tempWeight = remember(temperature) {
        when {
            temperature < 0 -> FontWeight.Thin
            temperature in 0..5 -> FontWeight.ExtraLight
            temperature in 6..10 -> FontWeight.Light
            temperature in 11..15 -> FontWeight.Normal
            temperature in 16..20 -> FontWeight.Medium
            temperature in 21..25 -> FontWeight.SemiBold
            temperature in 26..30 -> FontWeight.Bold
            temperature in 31..35 -> FontWeight.ExtraBold
            else -> FontWeight.Black
        }
    }

    val backgroundColor = remember() {
        if (weatherData.current.isDay == 1) {
            daySunnyColorBrush
        } else {
            nightSunnyColorBrush
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = backgroundColor)) // Aplica o gradiente ao Box
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                MediumFlexibleTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.blur(16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), // Blur effect can be adjusted or removed
                    title = { Text(text = weatherData.location.name) },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Transparente), // Cor secundária com leve transparência,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // Transparente quando rolado


                    )
                )
            },
            content = { innerPadding ->
                Log.d(TAG_DISPLAY, "Entering Scaffold content lambda. innerPadding: $innerPadding")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(innerPadding) // Aplica o padding do Scaffold
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // Permite rolagem de todo o conteúdo
                        .padding(horizontal = 16.dp) // Padding horizontal para o conteúdo da Column
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = weatherData.current.condition.text,
                        style = MaterialTheme.typography.headlineSmall, // Estilo maior
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Text(
                        text = "${weatherData.current.tempCelcius.roundToInt()}°",
                        style = MaterialTheme.typography.displayLargeEmphasized, // Estilo bem grande
                        fontWeight = tempWeight,
                    )
                    AsyncImage(
                        model = weatherData.current.condition.iconUrl,
                        contentDescription = weatherData.current.condition.text,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(vertical = 4.dp) // Ícone maior
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sensação: ${weatherData.current.feelslikeCelcius.roundToInt()}°C",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (todayForecastData != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            Text(
                                text = "Máx.: ${todayForecastData.maxTempCelcius?.roundToInt()}°C",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Mín.: ${todayForecastData.minTempCelcius?.roundToInt()}°C",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        Text(
                            "Previsão para hoje indisponível",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Log.d(TAG_DISPLAY, "Calling ForecastDisplayDataComposable with forecastData: $forecastData")
                    Log.d(
                        TAG_DISPLAY,
                        "Calling ForecastDisplayDataComposable with forecastData: ${forecastData?.dailyForecasts?.size ?: "null"} items"
                    )
                    ForecastDisplayData(forecastData = forecastData)
                    Log.d(TAG_DISPLAY, "Returned from ForecastDisplayDataComposable")

                    Spacer(modifier = Modifier.height(24.dp)) // Mais espaço

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        UVIndexDataDisplay(uvIndex = weatherData.current.uv)
                        //UVIndexDataDisplay(uvIndex = 12.0)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(IntrinsicSize.Min)
                        ) {
                            Text(text = "Pressão", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${weatherData.current.pressureMb} mb",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WindDataDisplay(
                            WindInfo(
                                speed = weatherData.current.windKph,
                                direction = weatherData.current.windDir
                            )
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Precipitação",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "${weatherData.current.precipitationMm} mm",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HumidityDataDisplay(humidityData = weatherData.current.humidity)

                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Log.d(TAG_DISPLAY, "Exiting Scaffold content lambda")
                }
            }
        )
    }
}

val getTemperature: (WeatherData) -> Int = { it.current.tempCelcius.roundToInt() }

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
