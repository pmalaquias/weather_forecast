package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.savedstate.SavedStateRegistryOwner
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.local.db.AppDatabase
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
class WeatherViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val application: Application,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {

            // Create the database instance
            val db: AppDatabase = Room.databaseBuilder(
                context = application.applicationContext,
                klass = AppDatabase::class.java,
                name = "weather-database"
            ).build()

            // Create the repository instance with the database and location provider
            val repository: WeatherRepository = WeatherRepositoryImpl(
                cityDao = db.cityDao(),
                locationProvider = LocationProvider(application.applicationContext)
            )
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(
                repository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}