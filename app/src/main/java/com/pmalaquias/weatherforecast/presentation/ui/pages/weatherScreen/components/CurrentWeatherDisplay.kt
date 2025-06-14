package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CurrentTemperatureDisplay(
    modifier: Modifier = Modifier,
    temperature: Double,
    textColor: Color = Color.Black,
    icon: String = "https://cdn.weatherapi.com/weather/64x64/day/113.png",
    iconDescription: String = "Weather icon", // Default value for icon description
    fellsLike: Double = 0.0,
    todayForecastData: DailyForecast? = null, // Optional parameter for today's forecast data
    condition: String = "Unknown Condition", // Default value for condition
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
) {

    val tempWeight = remember(temperature) {
        when (temperature.roundToInt()) {
            in Int.MIN_VALUE..0 -> FontWeight.Thin
            in 1..5 -> FontWeight.ExtraLight
            in 6..10 -> FontWeight.Light
            in 11..15 -> FontWeight.Normal
            in 16..20 -> FontWeight.Medium
            in 21..25 -> FontWeight.SemiBold
            in 26..30 -> FontWeight.Bold
            in 31..35 -> FontWeight.ExtraBold
            else -> FontWeight.Black

        }
    }

    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = condition,
            style = MaterialTheme.typography.headlineSmallEmphasized, // Estilo maior
            modifier = Modifier.padding(bottom = 8.dp),
            color = textColor
        )
        Row {
            Text(
                text = "${temperature.roundToInt()}°",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = tempWeight,
                fontSize = 64.sp,
                modifier = modifier
            )
            AsyncImage(
                model = icon,
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(100.dp)
                    .padding(vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Sensação: $fellsLike °C",
                style = MaterialTheme.typography.titleMediumEmphasized,
                color = textColor
            )

            if (todayForecastData != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = "Máx.: ${todayForecastData.maxTempCelcius?.roundToInt()}°",
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mín.: ${todayForecastData.minTempCelcius?.roundToInt()}°",
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        color = textColor
                    )
                }
            } else {
                Text(
                    "Previsão para hoje indisponível",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    color = textColor
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay thin Preview")
@Composable
fun CurrentWeatherDisplayPreview1() {
    CurrentTemperatureDisplay(
        temperature = -1.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay extra light Preview")
@Composable
fun CurrentWeatherDisplayPreview2() {
    CurrentTemperatureDisplay(
        temperature = 5.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay light Preview")
@Composable
fun CurrentWeatherDisplayPreview3() {
    CurrentTemperatureDisplay(
        temperature = 10.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay normal Preview")
@Composable
fun CurrentWeatherDisplayPreview4() {
    CurrentTemperatureDisplay(
        temperature = 15.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay medium Preview")
@Composable
fun CurrentWeatherDisplayPreview5() {
    CurrentTemperatureDisplay(
        temperature = 20.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay semi bold Preview")
@Composable
fun CurrentWeatherDisplayPreview6() {
    CurrentTemperatureDisplay(
        temperature = 25.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay bold Preview")
@Composable
fun CurrentWeatherDisplayPreview7() {
    CurrentTemperatureDisplay(
        temperature = 30.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay extra bold Preview")
@Composable
fun CurrentWeatherDisplayPreview8() {
    CurrentTemperatureDisplay(
        temperature = 35.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CurrentWeatherDisplay black Preview")
@Composable
fun CurrentWeatherDisplayPreview9() {
    CurrentTemperatureDisplay(
        temperature = 40.0,
        icon = PreviewData.sampleWeatherData.current.condition.iconUrl,
        fellsLike = PreviewData.sampleWeatherData.current.feelslikeCelcius,
        condition = PreviewData.sampleWeatherData.current.condition.text,
    )
}