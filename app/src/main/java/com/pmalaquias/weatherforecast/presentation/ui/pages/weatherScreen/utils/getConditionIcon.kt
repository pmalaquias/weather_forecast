package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils

import com.pmalaquias.weatherforecast.R

/**
 * Returns the appropriate weather condition icon based on the provided weather code.
 *
 * @param code The weather condition code.
 * @param isDay Indicates whether the icon should represent daytime conditions (default is true).
 * @return The resource ID of the corresponding weather condition icon.
 */
fun getConditionIcon(code: Int, isDay: Boolean = true): Int = when (code) {
    1000 -> if (isDay) R.drawable._113_sunny_icon else R.drawable._113_clear_icon
    1003 -> R.drawable._116_partly_cloudy_icon
    1006 -> R.drawable._119_cloudy_icon
    1009 -> R.drawable._122_overcast_icon
    1030 -> R.drawable._143_mist_icon
    1063 -> R.drawable._176_patchy_rain_possible_icon
    1066 -> R.drawable._179_patchy_snow_possible
    else -> R.drawable._113_sunny_icon
}
