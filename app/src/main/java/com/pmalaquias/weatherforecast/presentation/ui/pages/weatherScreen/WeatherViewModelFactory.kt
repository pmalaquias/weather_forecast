package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}