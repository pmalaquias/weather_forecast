package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getUvIndexDescription
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getUvIndexShape
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.ui.theme.UvGradientColors
import kotlin.math.roundToInt

const val LIQUID_SHADER_UV = """
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UVIndexDataDisplay(
    uvIndex: Double,
    modifier: Modifier = Modifier,
    maxUvIndex: Double = 12.0,
    barHeight: Dp = 12.dp,
    indicatorDiameter: Dp = 32.dp,
    indicatorColor: Color = Color.White.copy(0.9f),
    indicatorBorderWidth: Dp = 1.dp,
    indicatorBorderColor: Color = Color.Gray,
    textColor: Color = Color.Black,
) {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationPlayed = true }

    val targetProgress = (uvIndex / maxUvIndex).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) targetProgress else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "uv_progress"
    )

    val rotation by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "uv_rotation"
    )

    val shape1 = remember(uvIndex) { getUvIndexShape(uvIndex) }
    val clip1 = remember(shape1) { RoundedPolygonShape(polygon = shape1) }
    val uvIndexDescription = getUvIndexDescription(uvIndex)
    val uvIndexInt = uvIndex.roundToInt()

    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val shaderTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )

    val runtimeShader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        remember { RuntimeShader(LIQUID_SHADER_UV) }
    } else null

    val cardShape = RoundedCornerShape(16.dp)

    val fontWeight: FontWeight = remember(uvIndex) {
        when (uvIndex.roundToInt()) {
            in 0..2 -> FontWeight.ExtraLight
            in 3..5 -> FontWeight.Light
            in 6..7 -> FontWeight.Medium
            in 8..10 -> FontWeight.Bold
            else -> FontWeight.ExtraBold
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Background Glass
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
                Text("☀️", fontSize = 16.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.uv_index_title).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }

            Box {
                Text(
                    text = stringResource(uvIndexDescription),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = fontWeight,

                    )
            }

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val density = LocalDensity.current
                val indicatorSizePx = with(density) { indicatorDiameter.toPx() }
                val fullWidthPx = with(density) { maxWidth.toPx() }
                val rangePx = fullWidthPx - indicatorSizePx
                val offsetXDp = with(density) { (animatedProgress * rangePx).toDp() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(indicatorDiameter),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Barra de fundo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight)
                            .clip(CircleShape)
                            .background(Brush.horizontalGradient(colors = UvGradientColors))
                    )
                    
                    // Indicador
                    Box(
                        modifier = Modifier
                            .offset(x = offsetXDp)
                            .size(indicatorDiameter)
                            .graphicsLayer { rotationZ = rotation },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(clip1)
                                .background(indicatorColor)
                                .border(
                                    indicatorBorderWidth,
                                    indicatorBorderColor.copy(alpha = 0.5f),
                                    clip1
                                )
                        )
                        Text(
                            text = uvIndexInt.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.graphicsLayer { rotationZ = -rotation }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun UVIndexDataDisplayPreview() {
    AppTheme {
        UVIndexDataDisplay(
            uvIndex = PreviewData.sampleWeatherData.current.uv,
            modifier = Modifier.padding(16.dp)
        )
    }
}
