package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getUvIndexDescription
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getUvIndexShape
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.ui.theme.UvGradientColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UVIndexDataDisplay(
    uvIndex: Double,
    maxUvIndex: Double = 12.0,
    barHeight: Dp = 30.dp,
    indicatorDiameter: Dp = 30.dp,
    indicatorColor: Color = Color.White.copy(0.8f),
    indicatorBorderWidth: Dp = 1.dp,
    indicatorBorderColor: Color = Color.Gray,
) {
    val progress = remember(uvIndex, maxUvIndex) {
        (uvIndex / maxUvIndex).toFloat().coerceIn(0f, 1f)
    }

    val shape = remember(uvIndex) { // Depends only on uvIndex
        getUvIndexShape(uvIndex)
    }

    val clip = remember(shape) {
        RoundedPolygonShape(polygon = shape)
    }

    val fontWeight = remember(uvIndex) {
        when (uvIndex.roundToInt()) {
            in 0..2 -> FontWeight.ExtraLight
            in 3..5 -> FontWeight.Light
            in 6..7 -> FontWeight.Medium
            in 8..10 -> FontWeight.Bold
            else -> FontWeight.ExtraBold
        }
    }

    val uvIndexDescription = remember(uvIndex) {
        getUvIndexDescription(uvIndex)
    }

    val uvIndexInt = remember(uvIndex) {
        uvIndex.roundToInt()
    }

    Card(modifier = Modifier) {
        Column(
            modifier = Modifier
                .padding(UVIndexDimens.DefaultPadding)
                .width(UVIndexDimens.CardWidth) // Consider making these configurable or derived
                .height(UVIndexDimens.CardHeight),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                // Removed redundant Arrangement.Center as Column handles horizontal alignment
            ) {
                Text("☀️", fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text("Índice UV", style = MaterialTheme.typography.titleSmallEmphasized)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uvIndexDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold, // Consider if this should also be dynamic like the indicator text
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(8.dp))

                BoxWithConstraints(
                    modifier = Modifier
                        .height(indicatorDiameter)
                        .fillMaxWidth()
                ) {
                    val density = LocalDensity.current
                    val indicatorDiameterPx = remember(indicatorDiameter) { with(density) { indicatorDiameter.toPx() } }
                    val fullBarWidthPx = remember(maxWidth) { with(density) { maxWidth.toPx() } }

                    val effectiveBarWidthForIndicatorCenter =
                        remember(fullBarWidthPx, indicatorDiameterPx) {
                            fullBarWidthPx - indicatorDiameterPx
                        }
                    val indicatorCenterXOffsetPx =
                        remember(progress, effectiveBarWidthForIndicatorCenter) {
                            progress * effectiveBarWidthForIndicatorCenter
                        }
                    val indicatorCenterXOffsetDp =
                        remember(indicatorCenterXOffsetPx) { with(density) { indicatorCenterXOffsetPx.toDp() } }


                    Box(
                        modifier = Modifier
                            .height(barHeight)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(barHeight / 2))
                            .background(Brush.horizontalGradient(colors = UvGradientColors))
                            .align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = indicatorCenterXOffsetDp)
                                .size(indicatorDiameter)
                                .clip(clip)
                                .background(indicatorColor)
                                .border(indicatorBorderWidth, indicatorBorderColor, clip)
                                .align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = uvIndexInt.toString(),
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.bodyLargeEmphasized,
                                fontWeight = fontWeight,
                            )
                        }
                    }
                }
            }
        }
    }
}

private object UVIndexDimens {
    val CardWidth = 150.dp
    val CardHeight = 100.dp
    val DefaultPadding = 8.dp
    val SmallSpacer = 4.dp
    val MediumSpacer = 8.dp
    val LargeSpacer = 16.dp
    val TitleIconSize = 20.sp
}



@Preview(name = "Low UV", showBackground = true)
@Composable
fun UVIndexDataDisplayPreviewLow() {
    AppTheme {
        UVIndexDataDisplay(uvIndex = 1.0)
    }
}

@Preview(name = "Moderate UV", showBackground = true)
@Composable
fun UVIndexDataDisplayPreviewModerate() {
    AppTheme {
        UVIndexDataDisplay(uvIndex = 5.0)
    }
}

@Preview(name = "Extreme UV", showBackground = true)
@Composable
fun UVIndexDataDisplayPreviewExtreme() {
    AppTheme {
        UVIndexDataDisplay(uvIndex = 11.5)
    }
}