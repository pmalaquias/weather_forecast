package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    if (forecastData == null || forecastData.dailyForecasts.isEmpty()) {
        Text("No forecast data available.")
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = forecastData.dailyForecasts.size,
            key = { index -> forecastData.dailyForecasts[index].date }) { index ->
            DailyForecastItem(
                dailyForecast = forecastData.dailyForecasts[index],
                textColor = textColor
            )
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