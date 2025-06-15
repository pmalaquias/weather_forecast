package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils

import com.pmalaquias.weatherforecast.R

/**
 * Returns the string resource ID for the weather condition label based on the provided code.
 *
 * @param code The weather condition code.
 * @param isDay Boolean indicating if the condition is for day or night (default is true).
 * @return The string resource ID for the weather condition label.
 */
fun getConditionLabel(code: Int, isDay: Boolean = true): Int {

    return when (code) {
        1000 -> if (isDay) R.string.cw_sunny_day_label else R.string.cw_clear_night_label
        1003 -> R.string.cw_partly_cloudy_label
        1006 -> R.string.cw_cloudy_label
        1009 -> R.string.cw_overcast_label
        1030 -> R.string.cw_mist_label
        1063 -> R.string.cw_patchy_rain_possible_label
        1066 -> R.string.cw_patchy_snow_possible_label
        1069 -> R.string.cw_patchy_sleet_possible_label
        1072 -> R.string.cw_patchy_freezing_drizzle_possible_label
        1087 -> R.string.cw_thundery_outbreaks_possible_label
        1114 -> R.string.cw_blowing_snow_label
        1117 -> R.string.cw_blizzard_label
        1135 -> R.string.cw_fog_label
        1147 -> R.string.cw_freezing_fog_label
        1150 -> R.string.cw_patchy_light_drizzle_label
        1153 -> R.string.cw_light_drizzle_label
        1168 -> R.string.cw_freezing_drizzle_label
        1171 -> R.string.cw_heavy_freezing_drizzle_label
        1180 -> R.string.cw_patchy_light_rain_label
        1183 -> R.string.cw_light_rain_label
        1186 -> R.string.cw_moderate_rain_at_times_label
        1189 -> R.string.cw_moderate_rain_label
        1192 -> R.string.cw_heavy_rain_at_times_label
        1195 -> R.string.cw_heavy_rain_label
        1198 -> R.string.cw_light_freezing_rain_label
        1201 -> R.string.cw_moderate_or_heavy_freezing_rain_label
        1204 -> R.string.cw_light_sleet_label
        1207 -> R.string.cw_moderate_or_heavy_sleet_label
        1210 -> R.string.cw_patchy_moderate_snow_label
        1213 -> R.string.cw_light_snow_label
        1216 -> R.string.cw_patchy_moderate_snow_label
        1219 -> R.string.cw_moderate_snow_label
        1222 -> R.string.cw_patchy_heavy_snow_label
        1225 -> R.string.cw_heavy_snow_label
        1237 -> R.string.cw_ice_pellets_label
        1240 -> R.string.cw_light_rain_shower_label
        1243 -> R.string.cw_moderate_or_heavy_rain_shower_label
        1246 -> R.string.cw_torrential_rain_shower_label
        1249 -> R.string.cw_light_sleet_showers_label
        1252 -> R.string.cw_moderate_or_heavy_sleet_showers_label
        1255 -> R.string.cw_light_snow_showers_label
        1258 -> R.string.cw_moderate_or_heavy_snow_showers_label
        1261 -> R.string.cw_light_showers_of_ice_pellets_label
        1264 -> R.string.cw_moderate_or_heavy_showers_of_ice_pellets_label
        1273 -> R.string.cw_patchy_light_rain_with_thunder_label
        1276 -> R.string.cw_patchy_light_rain_with_thunder_label
        1279 -> R.string.cw_patchy_light_snow_with_thunder_label
        1282 -> R.string.cw_moderate_or_heavy_snow_with_thunder_label
        else -> R.string.cw_unknown_condition
    }
}