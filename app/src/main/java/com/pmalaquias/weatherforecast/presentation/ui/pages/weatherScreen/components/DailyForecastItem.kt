package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.presentation.ui.utils.DateUtils
import java.util.Locale


@Composable
fun DailyForecastItem(dailyForecast: DailyForecast, textColor: Color) {


    val locale = Locale("pt", "BR")
    val displayDate = DateUtils.formatDisplayDate(dailyForecast.date, locale)
    val dayOfWeekShort = DateUtils.getDayOfWeekAbbreviated(dailyForecast.date, locale)

    val maxTempString = "${dailyForecast.maxTempCelcius}°C"
    val minTempString = "${dailyForecast.minTempCelcius}°C"

    val imageContentDescription = "Weather condition: ${dailyForecast.condition.text}"

    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.4f),
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
            Text(
                text = dayOfWeekShort,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
            Text(
                text = "Max: $maxTempString",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = "Min: $minTempString",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            AsyncImage(
                model = dailyForecast.condition.iconUrl,
                contentDescription = imageContentDescription,
                modifier = Modifier.size(48.dp)
            )
        }
    }


}

