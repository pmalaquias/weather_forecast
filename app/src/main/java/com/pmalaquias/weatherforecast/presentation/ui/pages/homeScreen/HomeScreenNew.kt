package com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen

//
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.pages.extensions.toBoolean
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.ErrorScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionIcon
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionLabel
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel
import com.pmalaquias.weatherforecast.domain.models.CurrentWeather
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherCondition
import io.mockk.mockk
import kotlinx.coroutines.launch

//
///**
// * HomeScreen Composable: Main screen of the weather application.
// *
// * Displays a search bar for finding cities, a list of saved cities with current weather,
// * and handles different UI states like loading, error, and content.
// *
// * @param uiState The current state of the UI, provided by [WeatherViewModel].
// * @param viewModel The ViewModel responsible for business logic and data fetching.
// * @param onGoToWeatherPage Lambda to be invoked when a city is selected, to navigate to its detailed weather page.
// *                          It passes the selected [WeatherData].
// * @param onRefresh Lambda to be invoked when a refresh action is triggered (e.g., on error retry).
// */
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//fun HomeScreenNew(
//    uiState: WeatherUIState,
//    viewModel: WeatherViewModel,
//    onGoToWeatherPage: (WeatherData) -> Unit,
//    onRefresh: () -> Unit,
//    // onBackClick: () -> Unit = { }, // Kept for potential future use with a different TopAppBar
//) {
//    // Search bar state management
//    val searchBarState = rememberSearchBarState()
//    val textFieldState = rememberTextFieldState()
//    val scope = rememberCoroutineScope()
//    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
//
//    // System UI controller for theming status/navigation bars
//    val systemUiController = rememberSystemUiController()
//    val useDarkIcons = !MaterialTheme.colorScheme.surface.isLight() // Basic check for dark icons
//
//    SideEffect {
//        systemUiController.setSystemBarsColor(
//            color = Color.Transparent, // Makes system bars transparent
//            darkIcons = useDarkIcons,
//            isNavigationBarContrastEnforced = false, // Recommended for edge-to-edge
//        )
//    }
//
//    // Updates the TextField when the searchQuery in ViewModel changes (e.g., after selecting a city)
//    LaunchedEffect(uiState.searchQuery) {
//        if (textFieldState.text.toString() != uiState.searchQuery) {
//            textFieldState.edit {
//                replace(0, length, uiState.searchQuery)
//            }
//        }
//    }
//
//    // Notifies the ViewModel when the search text in TextField changes
//    LaunchedEffect(textFieldState.text) {
//        // Log.d("HomeScreen", "Search text changed: ${textFieldState.text}") // For debugging
//        viewModel.onSearchQueryChanged(textFieldState.text.toString())
//    }
//
//    // State to control if the search input field is enabled.
//    // Useful for disabling search during critical errors or specific loading states.
//    var isSearchBarEnable by remember { mutableStateOf(true) }
//
//    // Composable lambda defining the input field for the search bar.
//    // This is passed to both TopSearchBar and ExpandedFullScreenSearchBar.
//    val inputField = @Composable {
//        SearchBarDefaults.InputField(
//            modifier = Modifier,
//            enabled = isSearchBarEnable,
//            searchBarState = searchBarState,
//            textFieldState = textFieldState,
//            onSearch = { query -> // query is the text content when search is submitted
//                scope.launch {
//                    // Trigger weather loading for the searched city
//                    viewModel.loadWeatherForCity(query) // Assumes loadWeatherForCity handles the API call
//                    searchBarState.animateToCollapsed() // Collapse search bar after search
//                }
//            },
//            placeholder = { Text(stringResource(R.string.search_city_placeholder)) },
//            leadingIcon = {
//                if (searchBarState.currentValue == SearchBarValue.Expanded) {
//                    IconButton(onClick = {
//                        scope.launch {
//                            textFieldState.clearText() // Clear text when navigating back from expanded search
//                            searchBarState.animateToCollapsed()
//                        }
//                    }) {
//                        Icon(
//                            Icons.AutoMirrored.Default.ArrowBack,
//                            contentDescription = stringResource(R.string.cd_navigate_back)
//                        )
//                    }
//                } else {
//                    Icon(
//                        Icons.Default.Search,
//                        contentDescription = stringResource(R.string.cd_search_icon)
//                    )
//                }
//            },
//            // trailingIcon = { /* Optional: e.g., Mic icon or Clear text icon */ },
//        )
//    }
//
//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            // Using TopSearchBar for a less intrusive search bar experience.
//            TopSearchBar(
//                state = searchBarState,
//                inputField = inputField,
//                scrollBehavior = scrollBehavior,
//                // Consider MaterialTheme colors for consistency
//                // colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
//                // shape = SearchBarDefaults.inputFieldShape, // Default shape
//            )
//            // ExpandedFullScreenSearchBar provides the content area when the search bar is active/expanded.
//            ExpandedFullScreenSearchBar(
//                state = searchBarState,
//                inputField = inputField,
//            ) {
//                // Content to display when the search bar is expanded (showing search results or loading state)
//                if (uiState.isSearching) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                } else {
//                    LazyColumn {
//                        items(uiState.searchResults, key = { it.name + it.country }) { cityLocationInfo -> // cityLocationInfo is LocationInfo
//                            ListItem(
//                                headlineContent = { Text("${cityLocationInfo.name}, ${cityLocationInfo.country}") },
//                                leadingContent = {
//                                    Icon(
//                                        Icons.Filled.History,
//                                        contentDescription = null // Decorative
//                                    )
//                                },
//                                modifier = Modifier.clickable {
//                                    viewModel.selectCityFromSearch(cityLocationInfo) // ViewModel handles selection
//                                    scope.launch { searchBarState.animateToCollapsed() }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    ) { paddingValues -> // Content of the Scaffold
//        // Main content area, reacts to different UI states
//        when {
//            // Loading state: Show a central loading indicator
//            uiState.isInitialLoading && uiState.savedCities.isEmpty() && uiState.errorMessage == null -> {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    LoadingIndicator()
//                    Text(stringResource(R.string.loading_initial_data), modifier = Modifier.padding(top = 8.dp))
//                }
//            }
//
//            // Error state: Show an error message and a retry button
//            uiState.errorMessage != null -> {
//                // isSearchBarEnable = false // Optionally disable search bar on critical errors
//                ErrorScreen(
//                    //modifier = Modifier.padding(paddingValues),
//                    errorMessage = uiState.errorMessage,
//                    onRetry = {
//                        // isSearchBarEnable = true // Re-enable on retry
//                        onRefresh()
//                    },
//                )
//            }
//
//            // Content state: Display list of saved cities if available
//            uiState.savedCities.isNotEmpty() -> {
//                LazyColumn(
//                    contentPadding = paddingValues,
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(uiState.savedCities, key = { it.location.name + it.location.region }) { cityData ->
//                        val conditionLabelResId: Int = getConditionLabel(
//                            cityData.current.condition.code,
//                            cityData.current.isDay.toBoolean()
//                        )
//                        val iconResId: Int = getConditionIcon(
//                            cityData.current.condition.code,
//                            cityData.current.isDay.toBoolean()
//                        )
//
//                        CityWeatherCard(
//                            weatherData = cityData,
//                            conditionLabel = stringResource(id = conditionLabelResId),
//                            conditionIconRes = iconResId,
//                            onClick = {
//                                viewModel.setCurrentWeather(cityData)
//                                onGoToWeatherPage(cityData)
//                            }
//                        )
//                    }
//                }
//            }
//
//            // Empty state (after initial load, no errors, no saved cities): Prompt user
//            else -> {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        stringResource(R.string.home_empty_state_prompt),
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(horizontal = 16.dp)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Button(onClick = onRefresh) { // Assuming onRefresh fetches current location
//                        Text(stringResource(R.string.home_get_current_location_button))
//                    }
//                    Text(
//                        stringResource(R.string.home_or_search_city),
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
///**
// * Composable to display a single city's weather information in a Card.
// *
// * @param weatherData The [WeatherData] for the city.
// * @param conditionLabel The human-readable weather condition.
// * @param conditionIconRes The drawable resource ID for the weather condition icon.
// * @param onClick Lambda to be invoked when the card is clicked.
// */
//@Composable
//fun CityWeatherCard(
//    weatherData: WeatherData,
//    conditionLabel: String,
//    conditionIconRes: Int,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp)
//            .height(72.dp), // Consider Dynamic Sizing or a fixed aspect ratio if content varies
//        onClick = onClick
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 12.dp, vertical = 8.dp), // Adjusted padding
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.weight(1f) // Allows this Row to take available space
//            ) {
//                Image(
//                    painter = painterResource(id = conditionIconRes),
//                    contentDescription = conditionLabel, // Important for accessibility
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(end = 12.dp),
//                )
//                Column(
//                    modifier = Modifier.padding(end = 8.dp) // Padding to separate from temperature
//                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Text(
//                            text = weatherData.location.name,
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold // Make city name stand out
//                        )
//                        // Display "(Current)" indicator if this is the user's current GPS location
//                        if (weatherData.isFromCurrentLocation) {
//                            Text(
//                                text = stringResource(R.string.current_location_indicator_short),
//                                modifier = Modifier.padding(start = 6.dp),
//                                style = MaterialTheme.typography.labelSmall,
//                                fontWeight = FontWeight.Bold,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                    Text(
//                        text = conditionLabel,
//                        style = MaterialTheme.typography.bodyMedium,
//                    )
//                }
//            }
//            // Display current temperature
//            Text(
//                text = stringResource(R.string.temperature_degrees, weatherData.current.tempCelcius.toInt()),
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//    }
//}
//
///**
// * Helper function to determine if the current color scheme is light.
// * Used for setting system bar icon colors.
// */
//@Composable
//private fun Color.isLight() = (red * 299 + green * 587 + blue * 114) / 1000 > 0.5
//
//
//// --- Preview Section ---
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview_Loading() {
//    MaterialTheme { // Wrap preview in your app's theme
//        HomeScreen(
//            uiState = WeatherUIState(isInitialLoading = true, savedCities = emptyList()),
//            viewModel = mockk(relaxed = true), // Using MockK for a relaxed mock ViewModel
//            onGoToWeatherPage = {},
//            onRefresh = {}
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview_Error() {
//    MaterialTheme {
//        HomeScreen(
//            uiState = WeatherUIState(
//                isInitialLoading = false,
//                errorMessage = R.string.error_fetching_data, // Example error
//                savedCities = emptyList()
//            ),
//            viewModel = mockk(relaxed = true),
//            onGoToWeatherPage = {},
//            onRefresh = {}
//        )
//    }
//}
//
///*@Preview(showBackground = true, widthDp = 360, heightDp = 640)
//@Composable
//fun HomeScreenPreview_WithData() {
//    val sampleWeatherData = WeatherData(
//        location = LocationInfo("Preview City", "Region", "Country", 0.0, 0.0, "America/New_York", "10:00 AM"),
//        current = WeatherData.CurrentWeather(25.0, WeatherData.Condition("Sunny", "//cdn.weatherapi.com/weather/64x64/day/113.png", 1000), 1, 10.0, 1000.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
//        isFromCurrentLocation = true
//    )
//    val sampleWeatherData2 = WeatherData(
//        location = LocationInfo("Another City", "State", "Country", 0.0, 0.0, "Europe/London", "03:00 PM"),
//        current = WeatherData.CurrentWeather(18.0, WeatherData.Condition("Partly cloudy", "//cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 0, 15.0, 1010.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
//        isFromCurrentLocation = false
//    )
//    MaterialTheme {
//        HomeScreen(
//            uiState = WeatherUIState(
//                isInitialLoading = false,
//                savedCities = listOf(sampleWeatherData, sampleWeatherData2)
//            ),
//            viewModel = mockk(relaxed = true),
//            onGoToWeatherPage = {},
//            onRefresh = {}
//        )
//    }
//}*/
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview_EmptyState() {
//    MaterialTheme {
//        HomeScreen(
//            uiState = WeatherUIState(isInitialLoading = false, savedCities = emptyList()),
//            viewModel = mockk(relaxed = true),
//            onGoToWeatherPage = {},
//            onRefresh = {}
//        )
//    }
//}
//
//// Note about HomeScreenNew:
//// The Composable 'HomeScreenNew' is also present in this file.
//// If it's an older version or not currently in use, consider removing it
//// or refactoring it separately if it serves a different purpose.
//// This refactoring focused on the primary 'HomeScreen' composable.
//


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class) // ExperimentalMaterial3ExpressiveApi pode não ser mais necessária aqui
@Composable
fun HomeScreenNew(
    uiState: WeatherUIState,
    viewModel: WeatherViewModel,
    onGoToWeatherPage: (WeatherData) -> Unit,
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
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                            tooltip = { PlainTooltip { Text("Back") } },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            )
        }

    Scaffold(
        // O SearchBar lida com seu próprio comportamento de scroll e preenchimento da tela quando ativo.
        // O nestedScroll pode não ser necessário da mesma forma.

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
                    Text(stringResource(R.string.loading_initial_data), modifier = Modifier.padding(
                        top = 8.dp
                    ))
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
                                onGoToWeatherPage(cityData)
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
        location = LocationInfo("London", "Region", "UK", "2023-10-27 10:00", "Europe/London", 0.0, 0.0),
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
        location = LocationInfo("New York", "NY", "USA", "2023-10-27 10:00", "America/New_York", 0.0, 0.0),
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
                text = stringResource(R.string.temperature_degrees, weatherData.current.tempCelcius.toInt()),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
