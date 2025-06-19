package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils

import com.pmalaquias.weatherforecast.R

fun getCompassLabel(direction: String): Int {
    return when (direction.uppercase()) {
        "N" -> R.string.compass_north
        "NNE" -> R.string.compass_north_northeast
        "NE" -> R.string.compass_northeast
        "ENE" -> R.string.compass_east_northeast
        "E" -> R.string.compass_east
        "ESE" -> R.string.compass_east_southeast
        "SE" -> R.string.compass_southeast
        "SSE" -> R.string.compass_south_southeast
        "S" -> R.string.compass_south
        "SSW" -> R.string.compass_south_southwest
        "SW" -> R.string.compass_southwest
        "WSW" -> R.string.compass_west_southwest
        "W" -> R.string.compass_west
        "WNW" -> R.string.compass_west_northwest
        "NW" -> R.string.compass_northwest
        "NNW" -> R.string.compass_north_northwest
        else -> R.string.compass_unknown
    }
}