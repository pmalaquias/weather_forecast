package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components


import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.domain.models.WindInfo
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.ui.theme.dayCloudyColorBrush
import com.pmalaquias.weatherforecast.presentation.ui.theme.daySunnyColorBrush
import com.pmalaquias.weatherforecast.presentation.ui.theme.nightSunnyColorBrush
import com.pmalaquias.weatherforecast.presentation.ui.theme.snowColorBrush

const val LIQUID_SHADER = """
    uniform shader composable;
    uniform float2 size;
    uniform float time;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / size;
        // Distorção senoidal para simular movimento de fluido
        float distortion = sin(uv.y * 12.0 + time) * cos(uv.x * 10.0 + time) * 0.005;
        float2 distortedCoord = fragCoord + (distortion * size.x);
        return composable.eval(distortedCoord);
    }
"""

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherDataDisplay(
    weatherData: WeatherData,
    forecastData: ForecastData?,
    isDay: Boolean = true,
    isSaved: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val todayForecastData: DailyForecast? = forecastData?.dailyForecasts?.firstOrNull()

    val isDayCalculated = weatherData.current.isDay == 1
    val isCurrentLocal = weatherData.isFromCurrentLocation
    val conditionCode = weatherData.current.condition.code


    val backgroundColor =
        remember(isDayCalculated) {
            if (isDayCalculated) {
                if (conditionCode == 1006 || conditionCode == 1009 || conditionCode == 1030) dayCloudyColorBrush
                else if (conditionCode == 1066 || conditionCode == 1114 || conditionCode == 1210 || conditionCode == 1213 || conditionCode == 1216 || conditionCode == 1219 || conditionCode == 1222 || conditionCode == 1225 || conditionCode == 1255 || conditionCode == 1258 || conditionCode == 1279 || conditionCode == 1282)
                    snowColorBrush
                else
                    daySunnyColorBrush
            } else nightSunnyColorBrush
        }


    val textColor: Color = remember(isDayCalculated) { if (isDayCalculated) Color.Black else Color.White }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = isDayCalculated
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false
        )
    }

    val toolbarTitleSize: TextUnit by remember {
        derivedStateOf {
            val collapsedFraction = scrollBehavior.state.collapsedFraction
            lerp(32.sp, 24.sp, collapsedFraction)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )

    val runtimeShader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        remember { RuntimeShader(LIQUID_SHADER) }
    } else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = backgroundColor))
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                MediumFlexibleTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(start = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && runtimeShader != null) {
                                            runtimeShader.setFloatUniform("time", time)
                                            runtimeShader.setFloatUniform(
                                                "size",
                                                size.width,
                                                size.height
                                            )
                                            val liquid =
                                                android.graphics.RenderEffect.createRuntimeShaderEffect(
                                                    runtimeShader,
                                                    "composable"
                                                )
                                            renderEffect = liquid.asComposeRenderEffect()
                                        }
                                        clip = true
                                        shape = CircleShape
                                    }
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .blur(radius = 16.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )

                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = textColor
                                )
                            }
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isCurrentLocal) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Current Location",
                                    modifier = Modifier.size(24.dp),
                                    tint = textColor
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = weatherData.location.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = textColor,
                                fontSize = toolbarTitleSize,
                            )
                        }
                    },
                    titleHorizontalAlignment = Alignment.CenterHorizontally,
                    actions = {
                        Box (contentAlignment = Alignment.Center, modifier = Modifier.padding(end = 8.dp)){
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && runtimeShader != null) {
                                            runtimeShader.setFloatUniform("time", time)
                                            runtimeShader.setFloatUniform("size", size.width, size.height)
                                            val liquid = android.graphics.RenderEffect.createRuntimeShaderEffect(runtimeShader, "composable")
                                            renderEffect = liquid.asComposeRenderEffect()
                                        }
                                        clip = true
                                        shape = CircleShape
                                    }
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .blur(radius = 16.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            IconButton(onClick = onSaveClick) {
                                val icon =
                                    if (isSaved) Icons.Filled.Favorite else Icons.Default.FavoriteBorder
                                Icon(
                                    imageVector = icon,
                                    contentDescription = if (isSaved) "Remove from favorites" else "Add to favorites",
                                    tint = textColor
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    windowInsets = WindowInsets.statusBars,

                    )
            },
            content = { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    CurrentTemperatureDisplay(
                        temperature = weatherData.current.tempCelcius,
                        code = weatherData.current.condition.code,
                        textColor = textColor,
                        icon = weatherData.current.condition.iconUrl,
                        iconDescription = weatherData.current.condition.text,
                        feelsLike = weatherData.current.feelslikeCelcius,
                        todayForecastData = todayForecastData,
                        scrollBehavior = scrollBehavior,
                        isDay = weatherData.current.isDay
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ForecastDisplayData(
                        forecastData = forecastData,
                        textColor = textColor,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HumidityDataDisplay(
                            humidityData = weatherData.current.humidity,
                            textColor = textColor,
                            modifier = Modifier.weight(1f)
                        )

                        PressureCard(
                            pressure = weatherData.current.pressureMb, 
                            textColor = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WindDataCard(
                            windInfo = WindInfo(
                                speed = weatherData.current.windKph,
                                direction = weatherData.current.windDir
                            ), 
                            textColor = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        PrecipitationCard(
                            rainfall = weatherData.current.precipitationMm, 
                            textColor = textColor,
                            //difier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        UVIndexDataDisplay(
                            uvIndex = weatherData.current.uv, 
                            textColor = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherDataDisplayPreview() {
    AppTheme {
        WeatherDataDisplay(
            weatherData = PreviewData.sampleWeatherData,
            forecastData = PreviewData.sampleForecastData,
        )
    }
}
