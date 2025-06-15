package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.domain.models.WindInfo
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.ui.theme.daySunnyColorBrush
import com.pmalaquias.weatherforecast.presentation.ui.theme.nightSunnyColorBrush

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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherDataDisplay(
    weatherData: WeatherData, forecastData: ForecastData?
) {
    //val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val todayForecastData: DailyForecast? = forecastData?.dailyForecasts?.firstOrNull()

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

    val isDay = weatherData.current.isDay == 1

    val backgroundColor =
        remember(isDay) { if (isDay) daySunnyColorBrush else nightSunnyColorBrush }


    val textColor: Color = remember(isDay) { if (isDay) Color.Black else Color.White }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, darkIcons = useDarkIcons
        )
    }

    val toolbarTitleSize: TextUnit by remember {
        derivedStateOf {
            val collapsedFraction = scrollBehavior.state.collapsedFraction
            lerp(32.sp, 24.sp, collapsedFraction)
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
                    modifier = Modifier.fillMaxWidth(),//.blur(16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), // Blur effect can be adjusted or removed
                    title = {
                        Text(
                            text = weatherData.location.name,
                            style = MaterialTheme.typography.displaySmallEmphasized,
                            color = textColor,
                            fontSize = toolbarTitleSize
                        )
                    }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ), windowInsets = TopAppBarDefaults.windowInsets
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

                    CurrentTemperatureDisplay(
                        temperature = weatherData.current.tempCelcius,
                        code = weatherData.current.condition.code,
                        textColor = textColor,
                        icon = weatherData.current.condition.iconUrl,
                        iconDescription = weatherData.current.condition.text,
                        feelsLike = weatherData.current.feelslikeCelcius,
                        todayForecastData = todayForecastData,
                        scrollBehavior = scrollBehavior,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //Log.d(TAG_DISPLAY, "Calling ForecastDisplayDataComposable with forecastData: $forecastData")
                    Log.d(
                        TAG_DISPLAY,
                        "Calling ForecastDisplayDataComposable with forecastData: ${forecastData?.dailyForecasts?.size ?: "null"} items"
                    )
                    ForecastDisplayData(
                        forecastData = forecastData,
                        textColor = textColor,
                    )
                    Log.d(TAG_DISPLAY, "Returned from ForecastDisplayDataComposable")

                    Spacer(modifier = Modifier.height(24.dp)) // Mais espaço

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HumidityDataDisplay(
                            humidityData = weatherData.current.humidity,
                            textColor = textColor,
                        )

                        PressureCard(
                            pressure = weatherData.current.pressureMb, textColor = textColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WindDataCard(
                            WindInfo(
                                speed = weatherData.current.windKph,
                                direction = weatherData.current.windDir
                            ), textColor = textColor
                        )
                        PrecipitationCard(
                            rainfall = weatherData.current.precipitationMm, textColor = textColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        UVIndexDataDisplay(
                            uvIndex = weatherData.current.uv, textColor = textColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Log.d(TAG_DISPLAY, "Exiting Scaffold content lambda")
                }
            }
        ) // Fim do Scaffold
    }
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
