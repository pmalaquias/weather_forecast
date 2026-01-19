package com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen

//

import android.graphics.drawable.Icon
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Card
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.domain.models.CurrentWeather
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherCondition
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.pages.extensions.toBoolean
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.ErrorScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionIcon
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionLabel
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel
import io.mockk.mockk
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
) // ExperimentalMaterial3ExpressiveApi pode não ser mais necessária aqui
@Composable
fun HomeScreenNew(
    uiState: WeatherUIState,
    viewModel: WeatherViewModel,
    onGoToWeatherPage: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState() // Para a query da SearchBar
    var active by remember { mutableStateOf(false) } // Controla o estado expandido/ativo da SearchBar
    val scope = rememberCoroutineScope()

    // System UI controller (mantenha o seu código existente para isso)
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true //!MaterialTheme.colorScheme.surface.isLight()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false,
        )
    }

    // LaunchedEffect para atualizar o textFieldState com base no uiState.searchQuery (mantenha)
    LaunchedEffect(uiState.searchQuery) {
        if (textFieldState.text != uiState.searchQuery) {
            textFieldState.edit { replace(0, length, uiState.searchQuery) }
        }
    }

    // LaunchedEffect para notificar o ViewModel sobre mudanças no textFieldState.text (mantenha)
    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChanged(textFieldState.text.toString())
    }

    var isSearchBarEnable by remember { mutableStateOf(true) } // Pode não ser mais necessário ou gerenciado pelo `active`

    val searchBarState = rememberSearchBarState()


    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(

        )
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = { Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_search_icon)
                ) },
                trailingIcon = {

                },
            )
        }

    Scaffold(

        topBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {},
                actions = {  },
            )
            ExpandedFullScreenContainedSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors,
            ) {
                LazyColumn {
                    items(items = uiState.searchResults) { city ->
                        val cityName = "${city.name}, ${city.region}, ${city.country}"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCityFromSearch(city)
                                    scope.launch { searchBarState.animateToCollapsed() }
                                    onGoToWeatherPage()
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = cityName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

        },

    ) { paddingValues ->
        // Conteúdo principal da sua tela (lista de cidades salvas, erros, loading inicial)
        // Este conteúdo será "empurrado para baixo" ou coberto pelo SearchBar quando ele estiver ativo.

        when {
            uiState.isInitialLoading && uiState.savedCities.isEmpty() && uiState.errorMessage == null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), // Aplica o padding do Scaffold
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingIndicator()
                    Text(
                        stringResource(R.string.loading_initial_data), modifier = Modifier.padding(
                            top = 8.dp
                        )
                    )
                }
            }

            uiState.errorMessage != null -> {
                ErrorScreen(
                    // modifier = Modifier.padding(paddingValues), // Aplica o padding do Scaffold
                    errorMessage = uiState.errorMessage,
                    onRetry = { onRefresh() }
                )
            }

            uiState.savedCities.isNotEmpty() -> {
                HorizontalDivider(modifier, thickness = 16.dp)
                LazyColumn(
                    contentPadding = paddingValues, // Aplica o padding do Scaffold
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        uiState.savedCities,
                        key = { it.location.name + it.location.region }) { cityData ->
                        // Seu CityWeatherCard (mantenha o código existente)
                        val conditionLabelResId: Int = getConditionLabel(
                            cityData.current.condition.code,
                            cityData.current.isDay.toBoolean()
                        )
                        val iconResId: Int = getConditionIcon(
                            cityData.current.condition.code,
                            cityData.current.isDay.toBoolean()
                        )

                        CityWeatherCard(
                            modifier = Modifier.padding(paddingValues),
                            weatherData = cityData,
                            conditionLabel = stringResource(id = conditionLabelResId),
                            conditionIconRes = iconResId,
                            onClick = {
                                viewModel.setCurrentWeather(cityData)
                                onGoToWeatherPage()
                            }
                        )
                    }
                }
            }
            // Adicione um estado para "Nenhuma cidade salva ainda" se necessário, após o carregamento inicial e sem erros.
            else -> { // Se não estiver carregando, sem erros, e sem cidades salvas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Pesquise e adicione sua primeira cidade!") // Exemplo
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewPreview_Loading() {
    val viewModel = mockk<WeatherViewModel>(relaxed = true)
    HomeScreenNew(
        uiState = WeatherUIState(
            isInitialLoading = true,
            savedCities = emptyList(),
            errorMessage = null
        ),
        viewModel = viewModel,
        onGoToWeatherPage = {},
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewPreview_Error() {
    val viewModel = mockk<WeatherViewModel>(relaxed = true)
    HomeScreenNew(
        uiState = WeatherUIState(
            isInitialLoading = false,
            savedCities = emptyList(),
            errorMessage = R.string.error_fetching_data
        ),
        viewModel = viewModel,
        onGoToWeatherPage = {},
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewPreview_Content() {
    val viewModel = mockk<WeatherViewModel>(relaxed = true)
    val dummyWeatherData = WeatherData(
        location = LocationInfo(
            "London",
            "Region",
            "UK",
            "2023-10-27 10:00",
            "Europe/London",
            0.0,
            0.0
        ),
        current = CurrentWeather(
            tempCelcius = 15.0,
            condition = WeatherCondition("Partly Cloudy", "", 1003),
            windKph = 10.0,
            windDir = "N",
            uv = 3.0,
            humidity = 60,
            feelslikeCelcius = 14.0,
            isDay = 1,
            pressureMb = 1012.0,
            precipitationMm = 0.0
        ),
        isFromCurrentLocation = false
    )

    HomeScreenNew(
        uiState = WeatherUIState(
            isInitialLoading = false,
            savedCities = listOf(dummyWeatherData),
            errorMessage = null
        ),
        viewModel = viewModel,
        onGoToWeatherPage = {},
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CityWeatherCardPreview() {
    val dummyWeatherData = WeatherData(
        location = LocationInfo(
            "New York",
            "NY",
            "USA",
            "2023-10-27 10:00",
            "America/New_York",
            0.0,
            0.0
        ),
        current = CurrentWeather(
            tempCelcius = 22.0,
            condition = WeatherCondition("Sunny", "", 1000),
            windKph = 15.0,
            windDir = "W",
            uv = 5.0,
            humidity = 45,
            feelslikeCelcius = 23.0,
            isDay = 1,
            pressureMb = 1015.0,
            precipitationMm = 0.0
        ),
        isFromCurrentLocation = true
    )
    CityWeatherCard(
        weatherData = dummyWeatherData,
        conditionLabel = "Sunny",
        conditionIconRes = R.drawable._113_sunny_icon,
        onClick = {},
        modifier = Modifier
    )
}


/**
 * Composable to display a single city's weather information in a Card.
 *
 * @param weatherData The [WeatherData] for the city.
 * @param conditionLabel The human-readable weather condition.
 * @param conditionIconRes The drawable resource ID for the weather condition icon.
 * @param onClick Lambda to be invoked when the card is clicked.
 */
@Composable
fun CityWeatherCard(
    weatherData: WeatherData,
    conditionLabel: String,
    conditionIconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(72.dp), // Considere o dimensionamento dinâmico ou uma proporção fixa se o conteúdo variaronClick = onClick
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp), // Padding ajustado
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Permite que esta Row ocupe o espaço disponível
            ) {
                Image(
                    painter = painterResource(id = conditionIconRes),
                    contentDescription = conditionLabel, // Importante para acessibilidade
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp),
                )
                Column(
                    modifier = Modifier.padding(end = 8.dp) // Padding para separar da temperatura
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = weatherData.location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold // Destaca o nome da cidade
                        )
                        // Exibe o indicador "(Atual)" se esta for a localização GPS atual do usuário
                        if (weatherData.isFromCurrentLocation) {
                            Text(
                                text = stringResource(R.string.current_location_indicator_short),
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = conditionLabel,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            // Exibe a temperatura atual
            Text(
                text = stringResource(
                    R.string.temperature_degrees,
                    weatherData.current.tempCelcius.toInt()
                ),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
