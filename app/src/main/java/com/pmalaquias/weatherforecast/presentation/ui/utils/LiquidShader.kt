package com.pmalaquias.weatherforecast.presentation.ui.utils

const val LIQUID_SHADER: String = """
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

const val LIQUID_SHADER1 = """
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
