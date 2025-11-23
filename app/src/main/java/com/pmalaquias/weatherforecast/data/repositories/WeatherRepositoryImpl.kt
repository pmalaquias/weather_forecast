package com.pmalaquias.weatherforecast.data.repositories

import android.util.Log
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.local.db.CityDao
import com.pmalaquias.weatherforecast.data.local.db.SavedCityEntity
import com.pmalaquias.weatherforecast.data.remote.RetrofitClient
import com.pmalaquias.weatherforecast.data.remote.dto.LocationDto
import com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiResponseDto
import com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiService
import com.pmalaquias.weatherforecast.data.remote.dto.forecast.ForecastApiResponseDto
import com.pmalaquias.weatherforecast.domain.models.CurrentWeather
import com.pmalaquias.weatherforecast.domain.models.DailyForecast
import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherCondition
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class WeatherRepositoryImpl(
    private val cityDao: CityDao,
    private val locationProvider: LocationProvider,
    private val weatherApiService: WeatherApiService = RetrofitClient.instance,
) : WeatherRepository {

    private val TAG = "WeatherRepositoryImpl"

    override suspend fun getCurrentWeatherData(apiKey: String): WeatherData? {
        try {
            val deviceLocation = locationProvider.getCurrentLocation()

            if (deviceLocation == null) {
                Log.e(TAG, "Device location not available for current weather.")
                return null
            }
            val latLonQuery = "${deviceLocation.latitude},${deviceLocation.longitude}"
            Log.d(TAG, "Fetching current weather for: $latLonQuery")

            val response = weatherApiService.getCurrentWeather(
                apiKey = apiKey,
                locationQuery = latLonQuery,
                lang = "en" // Use your locale language here, e.g., "pt" for Portuguese
            )

            if (response.isSuccessful) {
                val weatherDto: WeatherApiResponseDto? = response.body()
                if (weatherDto != null) {
                    Log.i(TAG, "Current Weather API OK: ${weatherDto.location.name}")
                    return withContext(Dispatchers.Default) { mapDtoToDomain(weatherDto) }
                } else {
                    Log.e(TAG, "Current Weather API Response body is null.")
                    return null
                }
            } else {
                Log.e(TAG, "Current Weather API Error: ${response.code()} - ${response.errorBody()?.string()}")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching current weather: ${e.message}", e)
            return null
        }
    }

    suspend fun getCurrentWeatherFromApi(apikey: String): WeatherData? { // Mude para seu Result<WeatherData>
        try {
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                val latLonQuery = "${location.latitude},${location.longitude}"
                Log.d("WeatherRepository", "Fetching weather for: $latLonQuery")

                val response = weatherApiService.getCurrentWeather(
                    apiKey = apikey, // Do seu BuildConfig
                    locationQuery = latLonQuery,
                    lang = "en" // Use your locale language here, e.g., "pt" for Portuguese
                )

                if (response.isSuccessful) {
                    val weatherDataDto: WeatherApiResponseDto? = response.body()
                    Log.d("WeatherRepository", "API Response: $weatherDataDto")
                    // Aqui você mapearia weatherDataDto para o seu modelo de domínio WeatherData
                    // return Result.Success(mappedWeatherData)
                    if (weatherDataDto != null) {
                        return withContext(Dispatchers.Default) { mapDtoToDomain(weatherDataDto) }
                    } else{
                        Log.e("WeatherRepository", "API Response body is null")
                        // return Result.Error("API Response body is null")
                        return null
                    }// Retornando DTO diretamente para este exemplo simples
                } else {
                    Log.e("WeatherRepository", "API Error: ${response.code()} - ${response.message()}")
                    // return Result.Error("API Error: ${response.code()}")
                    return null
                }
            } else {
                Log.e("WeatherRepository", "Location not available")
                // return Result.Error("Location not available")
                return null
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Network Exception: ${e.message}", e)
            // return Result.Error("Network Exception: ${e.message}")
            return null
        }
    }

    override suspend fun getForecastData(apiKey: String, days: Int): ForecastData? {
        try {
            val deviceLocation = locationProvider.getCurrentLocation()
            if (deviceLocation == null) {
                Log.e(TAG, "Device location not available for forecast.")
                return null
            }
            val latLonQuery = "${deviceLocation.latitude},${deviceLocation.longitude}"
            Log.d(TAG, "Fetching forecast for: $latLonQuery, days: $days")

            // CORRIGIDO: Chamar o método getForecast da WeatherApiService
            val response = weatherApiService.getForecastWeather(
                apiKey = apiKey,
                locationQuery = latLonQuery,
                days = days,
                lang = "en"
            )

            if (response.isSuccessful) {
                val forecastDto = response.body()
                if (forecastDto != null) {
                    Log.i(TAG, "Forecast API OK for: ${forecastDto.location.name}")
                    return withContext(Dispatchers.Default) { mapForecastDtoToDomain(forecastDto) }
                } else {
                    Log.e(TAG, "Forecast API Response body is null.")
                    return null
                }
            } else {
                Log.e(TAG, "Forecast API Error: ${response.code()} - ${response.errorBody()?.string()}")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching forecast data: ${e.message}", e)
            return null
        }
    }

    override suspend fun getWeatherDataByCity(
        apiKey: String,
        cityName: String
    ): WeatherData? {
        return getWeatherDataByQuery(apiKey, cityName)
    }

    override fun getSavedCities(): Flow<List<SavedCityEntity>> {
        return cityDao.getAllCities()
    }

    override suspend fun saveCity(city: SavedCityEntity) {
        withContext(Dispatchers.IO) { // Operações de banco de dados devem ser feitas na IO thread
            cityDao.insertCity(city)
        }
    }

    override suspend fun deleteCity(cityName: String) {
        cityDao.deleteCity(cityName)
    }

    override suspend fun searchCities(
        apiKey: String,
        query: String
    ): List<LocationInfo>? {
        Log.d("WeatherRepository", "searchCities called with query: '$query'") // << LOG 7
        return try {
            val response = weatherApiService.searchCity(apiKey = apiKey, query = query) // ou searchCities
            Log.d("WeatherRepository", "API search response successful: ${response.isSuccessful}, code: ${response.code()}") // << LOG 8
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("WeatherRepository", "API search response body: $responseBody") // << LOG 9
                // Se responseBody for List<LocationDto>, você mapeia para List<LocationInfo> aqui.
                // Exemplo se for DTO:
                val mappedResults = responseBody?.map { dto -> mapLocationDtoToDomain(dto) }
                Log.d("WeatherRepository", "Mapped results: ${mappedResults?.size ?: "null"} items, Data: $mappedResults") // << NOVO LOG
                return mappedResults
            } else {
                Log.e("WeatherRepository", "Search API error body: ${response.errorBody()?.string()}") // << LOG 10
                null
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Search network/exception in repository", e) // << LOG 11
            null
        }
    }

    /**
     * Função privada que centraliza a lógica de chamada à API para obter o tempo atual,
     * seja por coordenadas ou por nome de cidade.
     */
    private suspend fun getWeatherDataByQuery(apiKey: String, query: String): WeatherData? {
        return try {
            val response = weatherApiService.getCurrentWeather(
                apiKey = apiKey,
                locationQuery = query,
                lang = "pt"
            )

            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Log.i(TAG, "API call successful for query '$query': ${dto.location.name}")
                    withContext(Dispatchers.Default) { mapDtoToDomain(dto) }
                } ?: run {
                    Log.e(TAG, "API Response body is null for query '$query'.")
                    null
                }
            } else {
                Log.e(TAG, "API Error for query '$query': ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching weather for query '$query': ${e.message}", e)
            null
        }
    }

    /**
     * Função privada que centraliza a lógica de chamada à API para obter a previsão.
     */
    private suspend fun getForecastDataByQuery(apiKey: String, query: String, days: Int): ForecastData? {
        return try {
            // Assumindo que o nome do método no seu WeatherApiService é getForecastWeather
            val response = weatherApiService.getForecastWeather(
                apiKey = apiKey,
                locationQuery = query,
                days = days,
                lang = "pt"
            )

            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Log.i(TAG, "Forecast API call successful for query '$query': ${dto.location.name}")
                    withContext(Dispatchers.Default) { mapForecastDtoToDomain(dto) }
                } ?: run {
                    Log.e(TAG, "Forecast API Response body is null for query '$query'.")
                    null
                }
            } else {
                Log.e(TAG, "Forecast API Error for query '$query': ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching forecast for query '$query': ${e.message}", e)
            null
        }
    }
    override suspend fun getForecastDataByCity(
        apiKey: String,
        cityName: String,
        days: Int
    ): ForecastData? {
        return getForecastDataByQuery(apiKey, cityName, days)
    }

    private fun mapDtoToDomain(dto: WeatherApiResponseDto): WeatherData {
        val locationInfo = LocationInfo(
            name = dto.location.name,
            region = dto.location.region,
            country = dto.location.country,
            localtime = dto.location.localtime,
            timezoneId = dto.location.tzId,
            lat = dto.location.lat,
            lon = dto.location.lon
        )

        val weatherCondition = WeatherCondition(
            text = dto.current.condition.text,
            iconUrl = "https:${dto.current.condition.iconUrl}",
            code = dto.current.condition.code // Se WeatherCondition tiver 'code'
        )

        val currentWeather = CurrentWeather(
            tempCelcius = dto.current.tempCelcius,
            condition = weatherCondition,
            windKph = dto.current.windKph,
            windDir = dto.current.windDir ?: "N/A",
            uv = dto.current.uvIndex,
            humidity = dto.current.humidity,
            feelslikeCelcius = dto.current.feelslikeCelcius,
            isDay = dto.current.isDay,
            pressureMb = dto.current.pressureMb ?: 0.0, // Fallback
            precipitationMm = dto.current.precipitationMm ?: 0.0 // Fallback
        )

        return WeatherData(
            location = locationInfo,
            current = currentWeather
        )
    }

    private fun mapForecastDtoToDomain(dto: ForecastApiResponseDto): ForecastData {
        val dailyForecasts = dto.forecast.forecastDay.map { forecastDayDto ->
            DailyForecast(
                date = forecastDayDto.date,
                maxTempCelcius = forecastDayDto.day.maxTempCelcius,
                minTempCelcius = forecastDayDto.day.minTempCelcius,
                avgTempCelcius = forecastDayDto.day.avgTempCelcius,
                condition = WeatherCondition(
                    text = forecastDayDto.day.condition.text,
                    iconUrl = "https:${forecastDayDto.day.condition.iconUrl}",
                    code = forecastDayDto.day.condition.code
                ),
                sunriseTime = forecastDayDto.astro.sunrise,
                sunsetTime = forecastDayDto.astro.sunset,
                chanceOfRain = forecastDayDto.day.dailyChanceOfRain,
                totalPrecipMm = forecastDayDto.day.totalPrecipMm,
                uvIndex = forecastDayDto.day.uvIndex,
                humidity = forecastDayDto.day.avgHumidity
            )
        }
        Log.d(TAG, "Mapped ${dailyForecasts.size} daily forecasts from DTO.")
        return ForecastData(dailyForecasts = dailyForecasts)
    }

    private fun mapLocationDtoToDomain(dto: LocationDto): LocationInfo {
        return LocationInfo(
            name = dto.name,
            region = dto.region,
            country = dto.country,
            lat = dto.lat,
            lon = dto.lon,
            timezoneId = dto.tzId,
            localtime = dto.localtime
        )
    }
}

