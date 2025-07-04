package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData

@Composable
fun ForecastDisplayData(
    forecastData: ForecastData?,
    textColor: Color = Color.Black,
) {

    val state = rememberScrollState()

    if (forecastData == null || forecastData.dailyForecasts.isEmpty()) {
        Text("No forecast data available.")
        return
    }

    val forecastDay = forecastData.dailyForecasts.size

    Column {
        Text(
            text = "$forecastDay-day Forecast",
            modifier = Modifier.padding(8.dp),
            color = textColor
        )
        Row(
            modifier = Modifier.horizontalScroll(state),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            forecastData.dailyForecasts.forEach { dailyForecast ->

                DailyForecastItem(
                    dailyForecast = dailyForecast,
                    textColor = textColor
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun ForecastDisplayDataPreview() {
    ForecastDisplayData(
        forecastData = PreviewData.sampleForecastData,
    )
}