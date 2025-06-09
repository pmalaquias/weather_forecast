package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WindDirectionDisplay(
    windDirection: String,
    rotationDegrees: Float = 0f, // Parâmetro para os graus de rotação
    modifier: Modifier = Modifier // Parâmetro Modifier para flexibilidade
) {

    val clip = remember() {
        RoundedPolygonShape(polygon = MaterialShapes.Arrow)
    }

    Box (
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = modifier // Use o modifier passado
                .size(64.dp) // Tamanho do seu ícone/seta
                .graphicsLayer { // Aplica transformações gráficas
                    rotationZ =
                        rotationDegrees // Define a rotação em torno do eixo Z (profundidade)
                    // transformOrigin = TransformOrigin.Center // Garante que a rotação seja pelo centro (geralmente é o padrão)
                }
                .clip(clip)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)) // Cor de fundo do seu ícone/seta) // Cor do seu ícone/seta
        ) {

        }

    }
}

fun windDirectionToDegrees(direction: String): Float {
    return when (direction.uppercase()) {
        "N" -> 0f
        "NNE" -> 22.5f
        "NE" -> 45f
        "ENE" -> 67.5f
        "E" -> 90f
        "ESE" -> 112.5f
        "SE" -> 135f
        "SSE" -> 157.5f
        "S" -> 180f
        "SSW" -> 202.5f
        "SW" -> 225f
        "WSW" -> 247.5f
        "W" -> 270f
        "WNW" -> 292.5f
        "NW" -> 315f
        "NNW" -> 337.5f
        else -> 0f
    }
}

@Preview(showBackground = true, name = "Wind Arrow Display")
@Composable
fun WindDirectionDisplayPreview() {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW") // Add more directions
                directions.forEachIndexed { index, direction ->
                    WindDirectionDisplay(
                        windDirection = direction,
                        rotationDegrees = windDirectionToDegrees(direction)
                    )
                    if (index < directions.size - 1) { // Add spacer except for the last item
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Wind Arrow - Invalid Direction")
@Composable
fun WindDirectionDisplayInvalidPreview() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(16.dp)) {
            WindDirectionDisplay(
                windDirection = "XYZ", // Assuming this might be an invalid case
                rotationDegrees = windDirectionToDegrees("XYZ") // And how your function handles it
            )
        }
    }
}