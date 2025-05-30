package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SearchBar
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
        refreshing = uiState.isLoading,
        onRefresh = { onRetry }
    )

    var expanded by rememberSaveable { mutableStateOf(false) }
    var searchResults: List<String> by rememberSaveable { mutableStateOf(emptyList()) }
    var onSearch: (String) -> Unit = { }
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text("Search...") },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            )
        }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

                    uiState.errorMessage != null && !uiState.isLoading -> { // Show error only if not reloading
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error: ${uiState.errorMessage}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Try Again")
                            }
                        }
                    }

                    uiState.weatherData != null -> {
                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()), // Makes the Column scrollable
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ){
                            SearchBar(
                                state = searchBarState,
                                inputField = inputField,
                            )
                            WeatherDataDisplay(weatherData = uiState.weatherData)
                        }
                    }

                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }

                    !uiState.isLoading && uiState.errorMessage == null -> {
                        // Empty or initial state without loading action
                        Text("Puxe para baixo para atualizar ou aguarde os dados do tempo.")
                    }
                }

        }


        // Pull-to-Refresh Indicator
        // It will be positioned at the top and center of the parent Box
        PullRefreshIndicator(
            refreshing = uiState.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherAppScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle().value

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        WeatherAppScreen(
            uiState = uiState,
            onRetry = { viewModel.fetchWeather() },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, name = "Weather App Screen - Loading")
@Composable
fun WeatherAppScreenLoadingPreview() {
    AppTheme {
        WeatherAppScreen(
            uiState = PreviewData.loadingState,
            onRetry = {}
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, name = "Weather App Screen - Success")
@Composable
fun WeatherAppScreenSuccessPreview() {
    AppTheme {
        WeatherAppScreen(
            uiState = PreviewData.successState,
            onRetry = {}
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, name = "Weather App Screen - Error")
@Composable
fun WeatherAppScreenErrorPreview() {
    AppTheme {
        WeatherAppScreen(
            uiState = PreviewData.errorState,
            onRetry = {}
        )
    }
}

