package com.pmalaquias.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.repositories.WeatherRepositoryImpl
import com.pmalaquias.weatherforecast.presentation.ui.pages.MainScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherAppScreen
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.WeatherViewModelFactory
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel


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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MainScreen(
                    viewModel = weatherViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            }
        }
    }
}
