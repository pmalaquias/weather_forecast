package com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.pages.extensions.toBoolean
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.ErrorScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.PreviewData
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionIcon
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionLabel
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    cidadesSalvas: List<String> = emptyList(),
    onBuscarCidade: (String) -> Unit = {},
    onConfigClick: () -> Unit = { /* No-op */ },
    citiesSaves: List<WeatherData> = emptyList(),
    onGoToWeatherPage: () -> Unit = {},
    uiState: WeatherUIState = WeatherUIState(),
    onRefresh: () -> Unit = {},
    //viewModel: WeatherViewModel = null as WeatherViewModel,
) {
    var busca by remember { mutableStateOf("") }
    val cidadesFiltradas = cidadesSalvas.filter {
        it.contains(busca, ignoreCase = true)
    }

    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, darkIcons = useDarkIcons
        )
    }

    var isSearchBarEnable: Boolean by remember { mutableStateOf(true) }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                enabled = isSearchBarEnable,
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

                //trailingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                //shape = SearchBarDefaults.inputFieldShape,
            )
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopSearchBar(
                state = searchBarState,
                inputField = inputField,
                scrollBehavior = scrollBehavior,
                shape = SearchBarDefaults.inputFieldShape,


                )
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField,
            ) {

            }
        },
        content = { padding ->

            when {
                uiState.isInitialLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoadingIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    isSearchBarEnable = false
                    ErrorScreen(
                        errorMessage = uiState.errorMessage,
                        onRetry = { onRefresh },
                    )
                }

                uiState.weatherData != null -> {
                    LazyColumn(
                        contentPadding = padding,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val list = List(100) { "Text $it" }
                        items(count = citiesSaves.size) {
                            val condition: Int = getConditionLabel(
                                citiesSaves[it].current.condition.code,
                                citiesSaves[it].current.isDay.toBoolean()
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(64.dp),
                                onClick = { onGoToWeatherPage() }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                getConditionIcon(
                                                    citiesSaves[it].current.condition.code,
                                                    citiesSaves[it].current.isDay.toBoolean()
                                                )
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(vertical = 4.dp, horizontal = 4.dp),
                                            alignment = Alignment.CenterStart
                                        )

                                        Column {
                                            Text(
                                                text = citiesSaves[it].location.name,
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                style = MaterialTheme.typography.titleMediumEmphasized,
                                            )
                                            Text(
                                                text = stringResource(id = condition),
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                style = MaterialTheme.typography.titleSmallEmphasized,
                                            )
                                        }
                                    }

                                    Text(
                                        text = citiesSaves[it].current.tempCelcius.toString() + "°",
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        style = MaterialTheme.typography.headlineLargeEmphasized,
                                    )
                                }
                            }
                        }
                    }


                }

                else -> {
                    // Estado inicial ou inesperado, pode mostrar um texto ou carregar
                    Text("Buscando dados do tempo...")
                    // Ou chamar viewModel.fetchWeatherData() se o init não for suficiente
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        val cidadesSalvas =
            listOf("São Paulo", "Rio de Janeiro", "Belo Horizonte", "Curitiba", "Porto Alegre")
        HomeScreen(
            uiState = PreviewData.successState,
            cidadesSalvas = cidadesSalvas,
        )
        //citiesSaves = cities

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    AppTheme {
        val cidadesSalvas =
            listOf("São Paulo", "Rio de Janeiro", "Belo Horizonte", "Curitiba", "Porto Alegre")
        HomeScreen(
            uiState = PreviewData.errorState,
            cidadesSalvas = cidadesSalvas,
        )
        //citiesSaves = cities

    }
}