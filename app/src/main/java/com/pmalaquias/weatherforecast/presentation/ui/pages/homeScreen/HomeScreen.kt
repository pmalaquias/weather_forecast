package com.pmalaquias.weatherforecast.presentation.ui.pages.homeScreen

import android.R.attr.navigationIcon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.data.local.db.SavedCityEntity
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.presentation.ui.pages.extensions.toBoolean
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.ErrorScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherAppScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherUIState
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionIcon
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getConditionLabel
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel
import io.mockk.mockk
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    cidadesSalvas: List<String> = emptyList(),
    onBuscarCidade: (String) -> Unit = {},
    onConfigClick: () -> Unit = { /* No-op */ },
    citiesSaves: List<WeatherData> = emptyList(),
    onGoToWeatherPage: (WeatherData) -> Unit = {},
    uiState: WeatherUIState = WeatherUIState(),
    onRefresh: () -> Unit = {},
    viewModel: WeatherViewModel,
    onBackClick: () -> Unit = { },
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

    // Atualiza textFieldState quando o searchQuery muda no ViewModel
    LaunchedEffect(uiState.searchQuery) {
        if (textFieldState.text != uiState.searchQuery) {
            textFieldState.edit {
                replace(0, length, uiState.searchQuery)
            }
        }
    }

    // Notifica o ViewModel quando o texto da busca muda
    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChanged(textFieldState.text as String)
    }


    var isSearchBarEnable: Boolean by remember { mutableStateOf(true) }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                enabled = isSearchBarEnable,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = {
                    scope.launch {
                        viewModel.loadWeatherForCity(textFieldState.text as String)
                        searchBarState.animateToCollapsed()
                    }
                },
                placeholder = { Text("Pesquise") },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    searchBarState.animateToCollapsed()
                                }
                            }
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
            SearchBarDefaults.colors()
            AppBarWithSearch(
                state = searchBarState,
                inputField = inputField,
                modifier = Modifier,
                //navigationIcon = navigationIcon,
                //actions = actions,
                //shape = SearchBarDefaults.inputFieldShape colors,
                //colors = SearchBarDefaults.TonalElevation,
                //tonalElevation = SearchBarDefaults.windowInsets,
                //shadowElevation = scrollBehavior
            )
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField,
            ) {
                if (uiState.isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(color = Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn {
                        items(uiState.searchResults) { city ->
                            print("City: ${city.name}, ${city.country}")
                            ListItem(
                                headlineContent = { Text("${city.name}, ${city.country}") },
                                leadingContent = {
                                    Icon(
                                        Icons.Filled.History,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable {
                                    viewModel.selectCityFromSearch(city)
                                    scope.launch { searchBarState.animateToCollapsed() }
                                }
                            )
                        }
                    }
                }
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

                uiState.savedCities.isNotEmpty() -> { // Melhor checar se a lista de cidades salvas tem conteúdo
                    LazyColumn(
                        contentPadding = padding,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Usar uiState.savedCities como a fonte dos dados
                        items(count = uiState.savedCities.size) { index ->
                            val cityData = uiState.savedCities[index] // Pega o WeatherData correto

                            val condition: Int = getConditionLabel(
                                cityData.current.condition.code,
                                cityData.current.isDay.toBoolean()
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp) // Ajuste no padding
                                    .height(72.dp), // Aumentei um pouco para o indicador
                                onClick = {
                                    viewModel.setCurrentWeather(cityData) // Informa ao ViewModel qual cidade foi selecionada
                                    onGoToWeatherPage(cityData) // Navega para a página do tempo)
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp), // Padding interno para o Row
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f) // Permite que esta Row ocupe o espaço disponível
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                getConditionIcon(
                                                    cityData.current.condition.code,
                                                    cityData.current.isDay.toBoolean()
                                                )
                                            ),
                                            contentDescription = stringResource(id = condition), // Adicionado contentDescription
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(end = 8.dp), // Espaço entre imagem e texto
                                            // alignment = Alignment.CenterStart // Pode remover se o Row já alinha
                                        )

                                        Column(
                                            modifier = Modifier.padding(end = 8.dp) // Evitar que o texto encoste na temperatura
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = cityData.location.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    // modifier = Modifier.padding(horizontal = 16.dp) // Removido padding individual
                                                )
                                                // ADICIONADO: Indicador para localização atual
                                                if (cityData.isFromCurrentLocation) {
                                                    Text(
                                                        text = "Atual",//stringResource(R.string.current_location_indicator), // Ex: "(Atual)"
                                                        modifier = Modifier.padding(start = 4.dp),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            Text(
                                                text = stringResource(id = condition),
                                                style = MaterialTheme.typography.bodyMedium, // Ajustado para bodyMedium
                                                // modifier = Modifier.padding(horizontal = 16.dp) // Removido padding individual
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${cityData.current.tempCelcius}°", // String template
                                        // modifier = Modifier.padding(horizontal = 16.dp), // Removido padding individual
                                        style = MaterialTheme.typography.headlineMedium, // Ajustado para headlineMedium
                                    )
                                }
                            }
                        }
                    }
                }
                // Se uiState.weatherData for nulo E uiState.savedCities estiver vazia,
                // pode mostrar o estado de "Buscando dados" ou "Nenhuma cidade salva"
                uiState.weatherData == null && uiState.savedCities.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Buscando dados ou adicione uma cidade..."/*stringResource(R.string.initial_weather_prompt)*/) // Ex: "Buscando dados ou adicione uma cidade..."
                        Spacer(modifier = Modifier.height(8.dp))
                        // Opcional: Um botão para forçar o refresh da localização atual
                        Button(onClick = onRefresh) {
                            Text("stringResource(R.string.refresh_current_location)")
                        }
                    }
                }

                else -> {

                    // Estado inicial ou inesperado, pode mostrar um texto ou carregar
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Buscando dados do tempo...")
                    }
                    // Ou chamar viewModel.fetchWeatherData() se o init não for suficiente
                }
            }
        }
    )
}



@Preview
@Composable
fun HomeScreenPreview() {
    val viewModel: WeatherViewModel = mockk(relaxed = true)
    HomeScreen(viewModel = viewModel)
}


@Preview
@Composable
fun SavedCitiesDrawerPreview() {
    val cities = listOf(
        SavedCityEntity(cityName = "Lisbon", country = "Portugal", latitude = 38.7223, longitude = -9.1393),
        SavedCityEntity(cityName = "Porto", country = "Portugal", latitude = 41.1579, longitude = -8.6291)
    )
    SavedCitiesDrawer(cities = cities, onCityClick = {}, onDeleteClick = {})
}

@Composable
fun SavedCitiesDrawer(
    cities: List<SavedCityEntity>,
    onCityClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.saved_locations), // "Locais Salvos"
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        }

        if (cities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_saved_locations), // "Nenhum local salvo"
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cities) { city ->
                    NavigationDrawerItem(
                        label = { Text("${city.cityName}, ${city.country}") },
                        selected = false,
                        onClick = { onCityClick(city.cityName) },
                        icon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        badge = {
                            IconButton(onClick = { onDeleteClick(city.cityName) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.delete_city), // "Apagar cidade"
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    }
}

