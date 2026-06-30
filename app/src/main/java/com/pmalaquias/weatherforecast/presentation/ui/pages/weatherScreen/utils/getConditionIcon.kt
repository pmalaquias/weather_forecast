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
    // --- Céu Limpo / Ensolarado ---
    1000 -> if (isDay) R.drawable._113_sunny_icon else R.drawable._113_clear_icon

    // --- Nublado / Parcialmente Nublado ---
    1003 -> R.drawable._116_partly_cloudy_icon // Considere criar a variação noturna se tiver
    1006, 1009 -> R.drawable._119_cloudy_icon // R.drawable._122_overcast_icon é praticamente igual

    // --- Neblina / Nevoeiro ---
    1030, 1135, 1147 -> R.drawable._143_mist_icon

    // --- Chuva Isolada (Patchy Rain) ---
    1063, 1150, 1180 -> if (isDay) R.drawable._176_patchy_rain_possible_icon else R.drawable.ic_patchy_rain_night

    // --- Chuva Leve / Chuvisco ---
    1153, 1183, 1186, 1189, 1240 -> R.drawable._296_light_rain_icon

    // --- Chuva Forte (Heavy Rain) ---
    1192, 1195, 1243, 1246 -> R.drawable.ic_heavy_rain

    // --- Tempestades com Raios (Thunder) ---
    1087, 1273, 1276, 1279, 1282 -> R.drawable.ic_thunderstorm

    // --- Chuva com Neve / Aguanieve (Sleet / Freezing Drizzle / Ice Pellets) ---
    1069, 1072, 1168, 1171, 1198, 1201, 1204, 1207, 1237, 1249, 1252, 1261, 1264 -> R.drawable.ic_sleet

    // --- Neve Isolada (Patchy Snow) ---
    1066, 1210, 1216 -> if (isDay) R.drawable._179_patchy_snow_possible else R.drawable.ic_patchy_snow_night

    // --- Neve Contínua / Forte (Snow / Blizzard) ---
    1117, 1213, 1219, 1222, 1225, 1255, 1258 -> R.drawable._326_light_snow_icon

    // --- Ventania / Blowing Snow ---
    1114 -> R.drawable.ic_windy

    // --- Fallback (Caso não encontre o código) ---
    else -> if (isDay) R.drawable._113_sunny_icon else R.drawable._113_clear_icon
}

