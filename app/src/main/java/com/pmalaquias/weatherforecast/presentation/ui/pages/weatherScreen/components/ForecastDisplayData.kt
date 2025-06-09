package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import com.pmalaquias.weatherforecast.presentation.ui.utils.DateUtils
import java.util.Locale

@Composable
fun ForecastDisplayData(
    forecastData: ForecastData?,
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
            DailyForecastItem(dailyForecast = forecastData.dailyForecasts[index])
        }

    }
}


@Preview(showBackground = true)
@Composable
fun ForecastDisplayDataPreview() {
    ForecastDisplayData(
        forecastData = PreviewData.sampleForecastData
    )
}