package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme

const val LIQUID_SHADER_PRESSURE = """
    uniform shader composable;
    uniform float2 size;
    uniform float time;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / size;
        float distortion = sin(uv.y * 1.0 + time) * cos(uv.x * 1.0 + time) * 0.005;
        float2 distortedCoord = fragCoord + (distortion * size.x);
        return composable.eval(distortedCoord);
    }
"""

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PressureCard(
    pressure: Double,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black
) {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val targetProgress = (pressure.toFloat().coerceIn(950f, 1050f) - 950f) / (1050f - 950f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) targetProgress else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "pressure_expansion"
    )

    val dynamicHeight = (4 + (8 * animatedProgress)).dp

    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val shaderTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )

    val runtimeShader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        remember { RuntimeShader(LIQUID_SHADER_PRESSURE) }
    } else null

    val cardShape = RoundedCornerShape(16.dp)

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && runtimeShader != null) {
                        runtimeShader.setFloatUniform("time", shaderTime)
                        runtimeShader.setFloatUniform("size", size.width, size.height)
                        val liquid = android.graphics.RenderEffect.createRuntimeShaderEffect(
                            runtimeShader,
                            "composable"
                        )
                        renderEffect = liquid.asComposeRenderEffect()
                    }
                    clip = true
                    this.shape = cardShape
                }
                .background(Color.White.copy(alpha = 0.2f), cardShape)
                .blur(radius = 16.dp)
                .border(1.dp, Color.White.copy(alpha = 0.3f), cardShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏲️", fontSize = 16.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.pressure_title).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "$pressure mb",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(dynamicHeight)
                        .fillMaxWidth()
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview
@Composable
fun PressureCardPreview() {
    AppTheme(
    ) {
        PressureCard(pressure = 1000.0)
    }
}