package com.pmalaquias.weatherforecast.presentation.ui.pages


import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen.HomeScreenNew
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherAppScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel

enum class WeatherScreen {
    HomeScreen,
    WeatherAppScreen
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { granted: Boolean ->
        if (granted) {
            viewModel.fetchWeather()
        }
    }

    val navController = rememberNavController()
    val uiState: WeatherUIState by viewModel.uiState.collectAsStateWithLifecycle()

    // Lançar o pedido de permissão apenas uma vez na inicialização se não concedida
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    if (locationPermissionState.status.isGranted) {
        NavHost(
            navController = navController,
            startDestination = WeatherScreen.HomeScreen.name,
            modifier = modifier
        ) {
            composable(WeatherScreen.HomeScreen.name) {
                HomeScreenNew(
                    viewModel = viewModel,
                    onGoToWeatherPage = { 
                        navController.navigate(route = WeatherScreen.WeatherAppScreen.name)
                    },
                    uiState = uiState,
                    onRefresh = { viewModel.fetchWeather() }
                )
            }
            composable(WeatherScreen.WeatherAppScreen.name) {
                WeatherAppScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val textToShow = if (locationPermissionState.status.shouldShowRationale) {
                "A permissão de localização é essencial para mostrar o clima. Por favor, conceda a permissão."
            } else {
                "Este aplicativo precisa da permissão de localização para funcionar."
            }
            Text(text = textToShow, modifier = Modifier.padding(bottom = 16.dp))
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("Conceder Permissão")
            }
        }
    }
}
