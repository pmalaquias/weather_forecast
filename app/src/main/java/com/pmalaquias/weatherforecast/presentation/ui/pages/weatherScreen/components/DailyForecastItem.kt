package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionIcon
import com.pmalaquias.weatherforecast.presentation.ui.utils.DateUtils
import com.pmalaquias.weatherforecast.presentation.ui.utils.getLocale

@Composable
fun DailyForecastItem(dailyForecast: DailyForecast, textColor: Color) {


    val locale = getLocale()
    val context = LocalContext.current

    val displayDate = DateUtils.formatDisplayDate(context, dailyForecast.date, locale)
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
                text = stringResource(R.string.max_daily_forecast_item_body, maxTempString),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = stringResource(R.string.min_daily_forecast_item_body, minTempString),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Image(
                painter = painterResource(
                    id = getConditionIcon(
                        code = dailyForecast.condition.code,
                    )
                ),
                contentDescription = imageContentDescription,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}