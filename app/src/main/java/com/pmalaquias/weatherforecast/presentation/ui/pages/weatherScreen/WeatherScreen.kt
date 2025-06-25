package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components.WeatherDataDisplay
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel
import kotlinx.coroutines.launch


/**
 * Composable function that displays the main weather application screen.
 *
 * This screen handles different UI states such as loading, error, and displaying weather data.
 * It supports pull-to-refresh functionality and includes a search bar for user input.
 *
 * @param uiState The current UI state containing weather data, loading, and error information.
 * @param onRetry Callback invoked when the user requests to retry loading data (e.g., after an error or pull-to-refresh).
 *
 * UI States handled:
 * - Loading: Shows a loading indicator.
 * - Error: Displays an error message with a retry button.
 * - Weather Data: Shows the weather data and a search bar.
 * - Initial/Empty: Prompts the user to pull down to refresh or wait for data.
 *
 * Features:
 * - Pull-to-refresh using [rememberPullRefreshState].
 * - Search bar with animated collapse/expand and input field.
 * - Responsive layout with proper alignment and padding.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@ExperimentalMaterial3ExpressiveApi
@Composable
fun WeatherAppScreen(
    uiState: WeatherUIState,
    onRetry: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing, onRefresh = { onRetry })

    var expanded by rememberSaveable { mutableStateOf(false) }
    var searchResults: List<String> by rememberSaveable { mutableStateOf(emptyList()) }
    var onSearch: (String) -> Unit = { }
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            modifier = Modifier,
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            placeholder = { Text("Search...") },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
        )
    }

    Box(modifier = Modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState) // Makes the Column scrollable with pull-to-refresh
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            when {
                // Do not show the central CircularProgressIndicator if pull-to-refresh is already active
                // The pullRefreshState.refreshing (uiState.isLoading) already controls the indicator at the top.
                // Unless it is the initial loading and not a refresh.
                // You can refine this logic, for example, by having an `isInitialLoading`.

                uiState.isInitialLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = "Erro: ${uiState.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    // Botão para tentar novamente
                    Button(onClick = { onRetry }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Tentar Novamente")
                    }
                }

                uiState.weatherData != null -> {
                    // Exibe os dados do tempo
                    WeatherDataDisplay(
                        weatherData = uiState.weatherData,
                        forecastData = uiState.forecastData,
                        //onBackClick = onBackClick
                    )
                }

                else -> {
                    // Exibe uma mensagem de carregamento ou vazio
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text("Puxe para baixo para atualizar.")
                    }
                }
            }

        }


        // Pull-to-Refresh Indicator
        // It will be positioned at the top and center of the parent Box
        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherAppScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUIState,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    //val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val isDay: Boolean = uiState.weatherData?.current?.isDay == 1

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                uiState.isInitialLoading -> {
                    LoadingIndicator()
                }

                uiState.errorMessage != null -> {
                    ErrorScreen(
                        errorMessage = uiState.errorMessage,
                        onRetry = { viewModel.fetchWeather() }
                    )
                }

                uiState.weatherData != null && uiState.forecastData != null -> {
                    // Exibe os dados do tempo
                    WeatherDataDisplay(
                        weatherData = uiState.weatherData,
                        forecastData = uiState.forecastData,
                        onBackClick = onBackClick,
                        isDay = isDay,
                    )
                }

                else -> {
                    // Estado inicial ou inesperado, pode mostrar um texto ou carregar
                    Text("Buscando dados do tempo...")
                    // Ou chamar viewModel.fetchWeatherData() se o init não for suficiente
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, name = "Weather App Screen - Loading")
@Composable
fun WeatherAppScreenLoadingPreview() {
    AppTheme {
        WeatherAppScreen(
            uiState = PreviewData.loadingState, onRetry = {})
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, name = "Weather App Screen - Success")
@Composable
fun WeatherAppScreenSuccessPreview() {
    AppTheme {
        WeatherAppScreen(
            uiState = PreviewData.successState, onRetry = {})
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, name = "Weather App Screen - Error")
@Composable
fun WeatherAppScreenErrorPreview() {
    AppTheme {
        WeatherAppScreen(
            uiState = PreviewData.errorState, onRetry = {})
    }
}

//@Composable
//fun WeatherAppScreen(viewModel: WeatherViewModel) {
//    // Observa o uiState do ViewModel
//    // Use collectAsStateWithLifecycle para ser mais seguro em relação ao ciclo de vida
//    // Para isso, adicione implementation "androidx.lifecycle:lifecycle-runtime-compose:2.8.0"
//    val uiState by viewModel.uiState.collectAsState() // ou collectAsStateWithLifecycle()
//
//    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            when {
//                uiState.isLoading -> {
//                    CircularProgressIndicator()
//                }
//                uiState.errorMessage != null -> {
//                    Text(
//                        text = "Erro: ${uiState.errorMessage}",
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                    // Botão para tentar novamente
//                    Button(onClick = { viewModel.fetchWeather() }, modifier = Modifier.padding(top = 8.dp)) {
//                        Text("Tentar Novamente")
//                    }
//                }
//                uiState.weatherData != null -> {
//                    // Exibe os dados do tempo
//                    WeatherDataDisplay(weatherData = uiState.weatherData!!)
//                }
//                else -> {
//                    // Estado inicial ou inesperado, pode mostrar um texto ou carregar
//                    Text("Buscando dados do tempo...")
//                    // Ou chamar viewModel.fetchWeatherData() se o init não for suficiente
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun WeatherDataDisplay(weatherData: WeatherData) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = Modifier.padding(16.dp)
//    ) {
//        Text(text = "Local: ${weatherData.location.name}, ${weatherData.location.country}", fontSize = 20.sp)
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "${weatherData.current.tempCelcius}°C", fontSize = 48.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = weatherData.current.condition.text, fontSize = 18.sp)
//
//        // Para exibir o ícone (exemplo com Coil, adicione a dependência: implementation "io.coil-kt:coil-compose:2.6.0")
//        // AsyncImage(
//        //     model = weatherData.current.condition.iconUrl, // Lembre-se que o mapeamento já adiciona "https:"
//        //     contentDescription = weatherData.current.condition.text,
//        //     modifier = Modifier.size(64.dp)
//        // )
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "Sensação: ${weatherData.current.feelslikeCelcius}°C")
//        Text(text = "Umidade: ${weatherData.current.humidity}%")
//        Text(text = "Vento: ${weatherData.current.windKph} km/h")
//    }
//}

