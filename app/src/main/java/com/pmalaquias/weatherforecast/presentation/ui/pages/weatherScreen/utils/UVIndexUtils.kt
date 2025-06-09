package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon
import kotlin.math.roundToInt

/**
 * Returns a [RoundedPolygon] shape corresponding to the given UV index value.
 *
 * The shape is selected based on the rounded integer value of the UV index:
 * - 0..2   : 4-sided polygon (Cookie4Sided)
 * - 3..5   : 6-sided polygon (Cookie6Sided)
 * - 6..7   : 7-sided polygon (Cookie7Sided)
 * - 8..10  : 9-sided polygon (Cookie9Sided)
 * - else   : 12-sided polygon (Cookie12Sided)
 *
 * @param uvIndex The UV index value as a [Double].
 * @return The corresponding [RoundedPolygon] shape.
 */


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun getUvIndexShape(uvIndex: Double): RoundedPolygon {
    return when (uvIndex.roundToInt()) {
        in 0..2 -> MaterialShapes.Cookie4Sided
        in 3..5 -> MaterialShapes.Cookie6Sided
        in 6..7 -> MaterialShapes.Cookie7Sided
        in 8..10 -> MaterialShapes.Cookie9Sided
        else -> MaterialShapes.Cookie12Sided
    }
}


/**
 * Returns a textual description for the given UV index value.
 *
 * The description is selected based on the rounded integer value of the UV index:
 * - 0..2   : "Baixo" (Low)
 * - 3..5   : "Moderado" (Moderate)
 * - 6..7   : "Alto" (High)
 * - 8..10  : "Muito Alto" (Very High)
 * - else   : "Extremo" (Extreme)
 *
 * @param uvIndex The UV index value as a [Double].
 * @return A [String] describing the UV index level.
 */
fun getUvIndexDescription(uvIndex: Double): String {
    return when (uvIndex.roundToInt()) {
        in 0..2 -> "Baixo"
        in 3..5 -> "Moderado"
        in 6..7 -> "Alto"
        in 8..10 -> "Muito Alto"
        else -> "Extremo"
    }
}