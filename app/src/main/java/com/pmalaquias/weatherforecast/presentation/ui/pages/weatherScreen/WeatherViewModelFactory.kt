package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.repositories.WeatherRepositoryImpl
import com.pmalaquias.weatherforecast.domain.repository.WeatherRepository
import com.pmalaquias.weatherforecast.presentation.viewModel.WeatherViewModel

/**
 * Factory class for creating instances of [WeatherViewModel].
 *
 * @property application The application context used to instantiate the [WeatherViewModel].
 *
 * This factory is required to provide the [Application] context to the [WeatherViewModel]
 * when it is created by the [ViewModelProvider].
 *
 * @throws IllegalArgumentException if the requested ViewModel class is not [WeatherViewModel].
 */
class WeatherViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            val repository: WeatherRepository = WeatherRepositoryImpl(
                LocationProvider(application.applicationContext)
            )
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(
                repository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}