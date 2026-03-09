package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme

const val LIQUID_SHADER_PRECIPITATION = """
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
fun PrecipitationCard(
    rainfall: Double,
    textColor: Color = Color.Black
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "time"
    )

    val runtimeShader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        remember { RuntimeShader(LIQUID_SHADER_PRECIPITATION) }
    } else null

    val shape = RoundedCornerShape(16.dp)

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(100.dp)
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && runtimeShader != null) {
                        runtimeShader.setFloatUniform("time", time)
                        runtimeShader.setFloatUniform("size", size.width, size.height)
                        val liquid = android.graphics.RenderEffect.createRuntimeShaderEffect(runtimeShader, "composable")
                        renderEffect = liquid.asComposeRenderEffect()
                    }
                    clip = true
                    this.shape = shape
                }
                .background(Color.White.copy(alpha = 0.2f), shape)
                .blur(radius = 16.dp)
                .border(1.dp, Color.White.copy(alpha = 0.3f), shape)
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(150.dp)
                .height(100.dp)
                .padding(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("☔", fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.precipitation_title),
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = textColor,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = " $rainfall  mm",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                )
            }
        }
    }
}

@Preview
@Composable
fun PrecipitationCardPreview() {
    AppTheme() {
        PrecipitationCard(rainfall = 10.0)
    }
}