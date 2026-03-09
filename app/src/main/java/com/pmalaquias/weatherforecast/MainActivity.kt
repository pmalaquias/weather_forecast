package com.pmalaquias.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.MainScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherViewModelFactory
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(this, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Ativa o suporte a borda a borda no nível do sistema
        enableEdgeToEdge()

        setContent {
            AppTheme {
                // Removemos o Scaffold daqui para que as telas individuais controlem seus fundos
                MainScreen(
                    viewModel = weatherViewModel,
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()
                )
            }
        }
    }
}
