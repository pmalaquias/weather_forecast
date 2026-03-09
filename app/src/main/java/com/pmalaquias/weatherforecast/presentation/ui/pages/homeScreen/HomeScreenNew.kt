package com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Card
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.pmalaquias.weatherforecast.data.local.db.SavedCityEntity
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
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun HomeScreenNew(
    uiState: WeatherUIState,
    viewModel: WeatherViewModel,
    onGoToWeatherPage: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false,
        )
    }

    LaunchedEffect(uiState.searchQuery) {
        if (textFieldState.text != uiState.searchQuery) {
            textFieldState.edit { replace(0, length, uiState.searchQuery) }
        }
    }

    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChanged(textFieldState.text.toString())
    }

    val searchBarState = rememberSearchBarState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors = SearchBarDefaults.appBarWithSearchColors()
    
    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            placeholder = {
                Text(modifier = Modifier.clearAndSetSemantics {}, text = stringResource(R.string.search_placeholder))
            },
            leadingIcon = { Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.cd_search_icon)
            ) }
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
        when {
            uiState.isInitialLoading && uiState.savedCities.isEmpty() && uiState.errorMessage == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                    errorMessage = uiState.errorMessage!!,
                    onRetry = { onRefresh() }
                )
            }

            uiState.savedCities.isNotEmpty() -> {
                HorizontalDivider(modifier, thickness = 16.dp)
                LazyColumn(
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        uiState.savedCities,
                        key = { it.location.name + it.location.region }) { cityData ->
                        val conditionLabelResId = getConditionLabel(
                            cityData.current.condition.code,
                            cityData.current.isDay.toBoolean()
                        )
                        val iconResId = getConditionIcon(
                            cityData.current.condition.code,
                            cityData.current.isDay.toBoolean()
                        )

                        CityWeatherCard(
                            weatherData = cityData,
                            conditionLabel = stringResource(id = conditionLabelResId),
                            conditionIconRes = iconResId,
                            onClick = {
                                viewModel.setCurrentWeather(cityData)
                                onGoToWeatherPage()
                            },
                            modifier = Modifier
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.home_empty_state_prompt))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewPreview_Loading() {
    HomeScreenNew(
        uiState = WeatherUIState(
            isInitialLoading = true,
            savedCities = emptyList(),
            errorMessage = null
        ),
        viewModel = createDummyViewModel(),
        onGoToWeatherPage = {},
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewPreview_Error() {
    HomeScreenNew(
        uiState = WeatherUIState(
            isInitialLoading = false,
            savedCities = emptyList(),
            errorMessage = R.string.error_fetching_data
        ),
        viewModel = createDummyViewModel(),
        onGoToWeatherPage = {},
        onRefresh = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewPreview_Content() {
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
        viewModel = createDummyViewModel(),
        onGoToWeatherPage = {},
        onRefresh = {}
    )
}

private fun createDummyViewModel(): WeatherViewModel {
    val dummyRepository = object : com.pmalaquias.weatherforecast.domain.repository.WeatherRepository {
        override suspend fun getCurrentWeatherData(apiKey: String): WeatherData? = null
        override suspend fun getForecastData(apiKey: String, days: Int): com.pmalaquias.weatherforecast.domain.models.ForecastData? = null
        override suspend fun getWeatherDataByCity(apiKey: String, cityName: String): WeatherData? = null
        override fun getSavedCities(): kotlinx.coroutines.flow.Flow<List<SavedCityEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override suspend fun saveCity(city: SavedCityEntity) {}
        override suspend fun deleteCity(cityName: String) {}
        override suspend fun searchCities(apiKey: String, query: String): List<LocationInfo>? = null
        override suspend fun getForecastDataByCity(apiKey: String, cityName: String, days: Int): com.pmalaquias.weatherforecast.domain.models.ForecastData? = null
    }
    return WeatherViewModel(dummyRepository)
}

@Composable
fun CityWeatherCard(
    weatherData: WeatherData,
    conditionLabel: String,
    conditionIconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(72.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = conditionIconRes),
                    contentDescription = conditionLabel,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp),
                )
                Column(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = weatherData.location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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
