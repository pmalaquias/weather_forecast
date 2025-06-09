package com.pmalaquias.weatherforecast

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.repositories.WeatherRepositoryImpl
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherAppScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherViewModelFactory
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel
import kotlinx.coroutines.launch
import kotlin.getValue


/**
 * MainActivity is the entry point of the Weather Forecast application.
 *
 * This activity initializes the [WeatherViewModel] using a [WeatherViewModelFactory] and sets up the UI
 * using Jetpack Compose. The UI is wrapped in an [AppTheme] and uses a [Scaffold] to provide basic
 * material design layout structure. The [WeatherAppScreen] composable is displayed as the main content,
 * receiving the [weatherViewModel] for data and logic handling.
 *
 * Lifecycle:
 * - On creation, edge-to-edge display is enabled and the Compose content is set.
 *
 * @see WeatherViewModel
 * @see WeatherViewModelFactory
 * @see WeatherAppScreen
 * @see AppTheme
 */
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels {
        // Aqui nós construímos as dependências "reais" manualmente
        // 1. Crie a dependência de nível mais baixo: LocationProvider
        val locationProvider = LocationProvider(application)

        // 2. Crie a dependência que precisa da anterior: WeatherRepositoryImpl
        //    (Ele usa o RetrofitClient.instance por padrão, então só precisa do locationProvider)
        val weatherRepository = WeatherRepositoryImpl(locationProvider)

        // 3. Crie a Factory, passando as dependências necessárias
        //    (O ViewModel que você compartilhou não usa mais o 'application',
        //    apenas o repositório, mas sua Factory pode precisar)
        WeatherViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //MainScreen(modifier = Modifier.padding(innerPadding))

                    WeatherAppScreen(
                        viewModel = weatherViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationProvider =
        remember { LocationProvider(context.applicationContext) } // Crie uma instância
    var locationText by remember { mutableStateOf("Localização não obtida") }
    val coroutineScope = rememberCoroutineScope()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Permissão de localização fina concedida
                coroutineScope.launch {
                    val location: Location? = locationProvider.getCurrentLocation()
                    locationText = if (location != null) {
                        "Lat: ${location.latitude}, Lon: ${location.longitude}"
                    } else {
                        "Não foi possível obter a localização."
                    }
                    Log.d("MainActivity", "Location from provider: $locationText")
                }
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permissão de localização aproximada concedida
                coroutineScope.launch {
                    val location: Location? = locationProvider.getCurrentLocation()
                    locationText = if (location != null) {
                        "Lat: ${location.latitude}, Lon: ${location.longitude} (Aproximada)"
                    } else {
                        "Não foi possível obter a localização."
                    }
                    Log.d("MainActivity", "Location from provider (coarse): $locationText")
                }
            }

            else -> {
                // Nenhuma permissão de localização concedida
                locationText = "Permissão de localização negada."
                Log.w("MainActivity", "Location permission denied.")
            }
        }
    }

    val weatherRepository = remember { WeatherRepositoryImpl(locationProvider) }

    val apiKey = BuildConfig.WEATHER_API_KEY

    LaunchedEffect(key1 = Unit) { // key1 = Unit faz executar apenas uma vez na composição inicial
        // --- COLE A LÓGICA PRINCIPAL DO TESTE AQUI ---
        Log.d("APITest", "Tentando ler API Key...")
        if (BuildConfig.WEATHER_API_KEY.isNotEmpty() && BuildConfig.WEATHER_API_KEY != "null") {
            Log.i(
                "APITest",
                "API Key lida com sucesso (não mostrar em produção): ${
                    BuildConfig.WEATHER_API_KEY.take(4)
                }..."
            )
        } else {
            Log.e("APITest", "API Key NÃO encontrada ou vazia no BuildConfig!")
            return@LaunchedEffect
        }
        Log.d("APITest", "Iniciando teste de obtenção de previsão do tempo...")
        val weatherResponseDto = weatherRepository.getCurrentWeatherFromApi(apiKey)
        if (weatherResponseDto != null) {
            Log.i("APITest", "---------------------------------------")
            Log.i("APITest", "SUCESSO! Dados da API Recebidos:")
            Log.i(
                "APITest",
                "Localização: ${weatherResponseDto.location.name}, ${weatherResponseDto.location.country}"
            )
            Log.i("APITest", "Horário Local: ${weatherResponseDto.location.localtime}")
            Log.i(
                "APITest",
                "Temperatura: ${weatherResponseDto.current.tempCelcius}°C (Sensação: ${weatherResponseDto.current.feelslikeCelcius}°C)"
            )
            Log.i("APITest", "Condição: ${weatherResponseDto.current.condition.text}")
            Log.i("APITest", "Vento: ${weatherResponseDto.current.windKph} km/h")
            Log.i("APITest", "Umidade: ${weatherResponseDto.current.humidity}%")
            Log.i(
                "APITest",
                "Ícone URL (precisa de https:): ${weatherResponseDto.current.condition.iconUrl}"
            )
            Log.i("APITest", "É dia? (1=sim, 0=não): ${weatherResponseDto.current.isDay}")
            Log.i("APITest", "---------------------------------------")
        } else {
            Log.e("APITest", "---------------------------------------")
            Log.e("APITest", "FALHA ao obter dados da previsão do tempo.")
            Log.e(
                "APITest",
                "Verifique logs anteriores do LocationProvider ou WeatherRepository para mais detalhes sobre o erro (localização nula, erro de API, etc.)."
            )
            Log.e("APITest", "---------------------------------------")
        }
        // --- FIM DA LÓGICA PRINCIPAL DO TESTE ---
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)// Use padding para espaçamento
    ) {
        Text("Its ok, the app is running!")
        Button(onClick = {
            // Lança a solicitação de permissão
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }) {
            Text("Obter Localização e Ver Log")
        }
        Text(text = locationText)
    }
}

