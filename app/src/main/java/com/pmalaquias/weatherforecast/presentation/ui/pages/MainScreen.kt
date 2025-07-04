package com.pmalaquias.weatherforecast.presentation.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen.HomeScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherAppScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel

enum class WeatherScreen {
    HomeScreen,
    WeatherAppScreen
}

@Composable
fun MainScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()

    // Get the name of the current screen
    val currentScreen = WeatherScreen.valueOf(
        backStackEntry?.destination?.route ?: WeatherScreen.HomeScreen.name
    )

    val pageState: WeatherScreen by rememberSaveable {mutableStateOf(WeatherScreen.HomeScreen)}

    val uiState: WeatherUIState = viewModel.uiState.collectAsStateWithLifecycle().value

    val cities: List<WeatherData> = if (uiState.weatherData != null)  listOf(uiState.weatherData) else emptyList()

    val isDay = uiState.weatherData?.current?.isDay



    NavHost(navController = navController, startDestination = WeatherScreen.HomeScreen.name) {

            composable  (WeatherScreen.HomeScreen.name) {
                HomeScreen(
                    onBuscarCidade = { },
                    citiesSaves = cities,
                    onGoToWeatherPage = {navController.navigate(WeatherScreen.WeatherAppScreen.name)},
                    uiState = uiState,
                    onRefresh = { viewModel.fetchWeather() },
                    //viewModel = viewModel,
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
}