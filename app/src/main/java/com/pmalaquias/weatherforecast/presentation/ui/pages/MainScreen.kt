package com.pmalaquias.weatherforecast.presentation.ui.pages

import android.Manifest // Adicione esta importação
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect // Certifique-se que esta é androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// Se androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect não for usada, remova-a.
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale // Adicione esta importação
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen.HomeScreen
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
        Manifest.permission.ACCESS_FINE_LOCATION // Usando Manifest importado
    ) { granted: Boolean ->
        if (granted) {
            viewModel.fetchWeather()
        } else {
            // Permissão negada. Você pode querer mostrar uma mensagem ou
            // guiar o usuário para as configurações do aplicativo.
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    // A variável currentScreen parece não estar sendo usada diretamente na lógica de navegação abaixo,
    // mas pode ser útil para outras coisas.
    // val currentScreen = WeatherScreen.valueOf(
    //     backStackEntry?.destination?.route ?: WeatherScreen.HomeScreen.name
    // )
    val uiState: WeatherUIState = viewModel.uiState.collectAsStateWithLifecycle().value
    // As variáveis cities e isDay parecem não estar sendo usadas diretamente aqui,
    // mas podem estar sendo usadas dentro de HomeScreenNew ou WeatherAppScreen através do uiState.
    // val cities: List<WeatherData> = if (uiState.weatherData != null)  listOf(uiState.weatherData) else emptyList()
    // val isDay = uiState.weatherData?.current?.isDay

    if (locationPermissionState.status.isGranted) {
        // Permissão concedida, mostrar a UI principal do aplicativo
        NavHost(
            navController = navController,
            startDestination = WeatherScreen.HomeScreen.name,
            modifier = modifier // Aplicando o modifier passado para MainScreen
        ) {
            composable(WeatherScreen.HomeScreen.name) {
                HomeScreenNew(
                    viewModel = viewModel,
                    onGoToWeatherPage = { navController.navigate(route = WeatherScreen.WeatherAppScreen.name){
                        popUpTo(WeatherScreen.HomeScreen.name) {
                            inclusive = true
                        }
                    } },
                    uiState = uiState,
                    onRefresh = { viewModel.fetchWeather() },
                    modifier = modifier
                )
            }
            composable(WeatherScreen.WeatherAppScreen.name) {
                WeatherAppScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onBackClick = { navController.navigate(WeatherScreen.HomeScreen.name) },
                )
            }
        }
    } else {
        // Permissão não concedida, mostrar UI para solicitar permissão
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
                "Este aplicativo precisa da permissão de localização para funcionar. Por favor, conceda a permissão quando solicitado."
            }
            Text(text = textToShow, modifier = Modifier.padding(bottom = 8.dp))
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("Conceder Permissão")
            }
        }

        // Se a justificativa não precisa ser mostrada (ex: primeira solicitação),
        // dispara a solicitação de permissão automaticamente.
        if (!locationPermissionState.status.shouldShowRationale) {
            SideEffect { // Certifique-se que esta é androidx.compose.runtime.SideEffect
                locationPermissionState.launchPermissionRequest()
            }
        }
    }
}