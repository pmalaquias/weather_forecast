package com.example.weatherforecast

import android.Manifest
import android.R.attr.padding
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherforecast.data.local.LocationProvider
import com.example.weatherforecast.presentation.ui.theme.WeatherForecastTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherForecastTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationProvider = remember { LocationProvider(context.applicationContext) } // Crie uma instância
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
                    //val location: Location? = locationProvider.getCurrentLocationWithUpdatesFallback() // Nova linha para teste
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

    Scaffold (modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
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



}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherForecastTheme {
        Greeting("Android")
    }
}