package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.presentation.ui.utils.DateUtils
import java.util.Locale

@Composable
fun ForecastDisplayData(
    forecastData: ForecastData?,
) {

    Box {
        /*Row {

            forecastData?.dailyForecasts?.forEach { dailyForecast ->
                Column(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxHeight(),
                ) {
                    DailyForecastItem(dailyForecast = dailyForecast)
                }
            }

        }*/

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            forecastData?.dailyForecasts?.forEach { dailyForecast ->
                item {
                    DailyForecastItem(dailyForecast = dailyForecast)
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(dailyForecast: DailyForecast) {
    val displayDate =
        DateUtils.formatDisplayDate(dailyForecast.date, Locale("pt", "BR")) // Usando Locale pt-BR
    val dayOfWeekShort = DateUtils.getDayOfWeekAbbreviated(dailyForecast.date, Locale("pt", "BR"))

    Column {
        Text(text = displayDate) // Ex: "Hoje", "Amanhã", "3 de Junho"
        Text(text = dayOfWeekShort) // Ex: "TER"
        Text(text = "${dailyForecast.maxTempCelcius}°C")
        Text(text = "${dailyForecast.minTempCelcius}°C")
        AsyncImage(model = dailyForecast.condition.iconUrl, contentDescription = null)
    }
}


@Preview(showBackground = true)
@Composable
fun ForecastDisplayDataPreview() {
    ForecastDisplayData(
        forecastData = PreviewData.sampleForecastData
    )
}