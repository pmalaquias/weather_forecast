package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.presentation.ui.utils.DateUtils
import java.util.Locale

@Composable
fun DailyForecastItem(dailyForecast: DailyForecast) {
    val locale = Locale("pt", "BR")
    val displayDate = DateUtils.formatDisplayDate(dailyForecast.date, locale)
    val dayOfWeekShort = DateUtils.getDayOfWeekAbbreviated(dailyForecast.date, locale)

    val maxTempString =
        "${dailyForecast.maxTempCelcius}°C"
    val minTempString =
        "${dailyForecast.minTempCelcius}°C"

    // Example for accessibility: "Weather condition: Sunny"
    val imageContentDescription = "Weather condition: ${dailyForecast.condition.text}"

    Card {
        Column(
            modifier = Modifier.padding(16.dp), // Increased padding for better spacing within the card
            verticalArrangement = Arrangement.spacedBy(4.dp) // Adds space between text elements
        ) {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = dayOfWeekShort, style = MaterialTheme.typography.bodySmall)
            Text(text = "Max: $maxTempString", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Min: $minTempString", style = MaterialTheme.typography.bodyMedium)
            AsyncImage(
                model = dailyForecast.condition.iconUrl,
                contentDescription = imageContentDescription, // Important for accessibility
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
