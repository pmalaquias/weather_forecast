package com.pmalaquias.weatherforecast.data.repositories

import android.location.Location
import android.util.Log
import com.pmalaquias.weatherforecast.BuildConfig
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.remote.dto.*
import com.pmalaquias.weatherforecast.data.remote.dto.forecast.*
import com.pmalaquias.weatherforecast.domain.repository.WeatherRepository
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class WeatherRepositoryImplTest {

    // Dependências mockadas
    @RelaxedMockK
    private lateinit var locationProvider: LocationProvider

    @RelaxedMockK
    private lateinit var weatherApiService: WeatherApiService

    // A classe que estamos a testar
    private lateinit var weatherRepository: WeatherRepository

    private val dummyApiKey = "test_api_key"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        // Mockar Log para evitar chamar a implementação real do Android
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0

        // Mockar BuildConfig para a API Key
        mockkStatic(BuildConfig::class)

        // Instanciar o nosso repositório com as dependências mockadas
        weatherRepository = WeatherRepositoryImpl(locationProvider, weatherApiService)
    }

    @After
    fun tearDown() {
        unmockkAll() // Limpa todos os mocks após cada teste
    }

    /**
     * Helper para criar uma resposta mockada do Retrofit.
     */
    private fun <T> createMockResponse(
        body: T?,
        isSuccessful: Boolean,
        code: Int = 200
    ): Response<T> {
        return if (isSuccessful) {
            Response.success(body)
        } else {
            Response.error(code, "".toResponseBody(null))
        }
    }

    // --- Testes para getCurrentWeatherData ---

    @Test
    fun `getCurrentWeatherData - GIVEN location available and API success - THEN returns mapped WeatherData`() =
        runTest {
            // GIVEN: Uma localização válida é retornada pelo provider
            val mockLocation: Location = mockk()
            every { mockLocation.latitude } returns 1.0
            every { mockLocation.longitude } returns 1.0
            coEvery { locationProvider.getCurrentLocation() } returns mockLocation

            // GIVEN: A API retorna uma resposta de sucesso com dados válidos
            val mockCurrentWeatherDto = CurrentWeatherDto(
                tempCelcius = 25.0,
                condition = ConditionDto("Sunny", "//icon.url", 1000),
                windKph = 10.0,
                windDir = "N",
                uvIndex = 5.0,
                humidity = 50,
                feelslikeCelcius = 26.0,
                isDay = 1,
                pressureMb = 1012.0,
                precipitationMm = 0.0,
                tempFahrenheit = null,
                windMph = null,
                windDegree = null,
                pressureIn = null,
                precipitationIn = null,
                cloud = null,
                feelslikeFahrenheit = null,
                visKm = null,
                visMiles = null,
                gustMph = null,
                gustKph = null
            )
            val mockLocationDto = LocationDto(
                "Test City",
                "Test Region",
                "Test Country",
                1.0,
                1.0,
                "tz",
                123L,
                "2025-06-07 17:30"
            )
            val mockApiResponse =
                WeatherApiResponseDto(location = mockLocationDto, current = mockCurrentWeatherDto)
            val successResponse = createMockResponse(mockApiResponse, isSuccessful = true)
            coEvery {
                weatherApiService.getCurrentWeather(
                    any(),
                    any(),
                    any()
                )
            } returns successResponse

            // WHEN: Chamamos a função do repositório
            val result = weatherRepository.getCurrentWeatherData(dummyApiKey)

            // THEN: O resultado não deve ser nulo e deve conter os dados mapeados
            assertNotNull(result)
            assertEquals("Test City", result?.location?.name)
            assertEquals(25.0, result?.current?.tempCelcius)
            assertEquals("Sunny", result?.current?.condition?.text)
            assertEquals(
                "https://icon.url",
                result?.current?.condition?.iconUrl
            ) // Verifica se o "https:" foi adicionado
        }

    @Test
    fun `getCurrentWeatherData - GIVEN location unavailable - THEN returns null`() = runTest {
        // GIVEN: O LocationProvider retorna nulo
        coEvery { locationProvider.getCurrentLocation() } returns null

        // WHEN
        val result = weatherRepository.getCurrentWeatherData(dummyApiKey)

        // THEN: O resultado deve ser nulo
        assertNull(result)
        // E a API de tempo NÃO deve ser chamada
        coVerify(exactly = 0) { weatherApiService.getCurrentWeather(any(), any(), any()) }
    }

    @Test
    fun `getCurrentWeatherData - GIVEN API returns error - THEN returns null`() = runTest {
        // GIVEN: Uma localização válida
        val mockLocation: Location =
            mockk { every { latitude } returns 1.0; every { longitude } returns 1.0 }
        coEvery { locationProvider.getCurrentLocation() } returns mockLocation

        // GIVEN: A API retorna um erro (ex: 404 Not Found)
        val errorResponse =
            createMockResponse<WeatherApiResponseDto>(null, isSuccessful = false, code = 404)
        coEvery { weatherApiService.getCurrentWeather(any(), any(), any()) } returns errorResponse

        // WHEN
        val result = weatherRepository.getCurrentWeatherData(dummyApiKey)

        // THEN: O resultado deve ser nulo
        assertNull(result)
    }

    @Test
    fun `getCurrentWeatherData - GIVEN API throws exception - THEN returns null`() = runTest {
        // GIVEN: Uma localização válida
        val mockLocation: Location =
            mockk { every { latitude } returns 1.0; every { longitude } returns 1.0 }
        coEvery { locationProvider.getCurrentLocation() } returns mockLocation

        // GIVEN: A chamada à API lança uma exceção (ex: erro de rede)
        coEvery {
            weatherApiService.getCurrentWeather(
                any(),
                any(),
                any()
            )
        } throws Exception("Network error")

        // WHEN
        val result = weatherRepository.getCurrentWeatherData(dummyApiKey)

        // THEN: O resultado deve ser nulo
        assertNull(result)
    }


    // --- Testes para getForecastData (Exemplos) ---

    @Test
    fun `getForecastData - GIVEN location available and API success - THEN returns mapped ForecastData`() =
        runTest {
            // GIVEN: Uma localização válida
            val mockLocation: Location =
                mockk { every { latitude } returns 1.0; every { longitude } returns 1.0 }
            coEvery { locationProvider.getCurrentLocation() } returns mockLocation

            // GIVEN: A API de forecast retorna uma resposta de sucesso
            val mockDayDetails = DayDetailsDto(maxTempCelcius = 28.0, minTempCelcius = 18.0, avgTempCelcius = 23.0, maxWindKph = 15.0, totalPrecipMm = 0.5, avgHumidity = 60.0, dailyChanceOfRain = 20, condition = ConditionDto("Partly Cloudy", "//icon2.url", 1003), uvIndex = 6.0, dailyWillItRain = 1, dailyWillItSnow = 0, dailyChanceOfSnow = 0)

            val mockAstro = AstroDto(
                sunrise = "06:00 AM",
                sunset = "06:00 PM",
                moonrise = "moonrise",
                moonset = "moonset",
                moonPhase = "phase",
                moonIllumination = "illumination",
                isSunUp = 1,
                isMoonUp = 0
            )
            val mockForecastDay = ForecastDayDto(
                date = "2025-06-08",
                dateEpoch = 12345L,
                day = mockDayDetails,
                astro = mockAstro,
                hour = null
            )
            val mockForecastDto = ForecastDto(listOf(mockForecastDay))
            val mockLocationDto = LocationDto(
                "Test City",
                "Test Region",
                "Test Country",
                1.0,
                1.0,
                "tz",
                123L,
                "2025-06-07 17:30"
            )
            val mockApiResponse = ForecastApiResponseDto(
                location = mockLocationDto,
                current = null,
                forecast = mockForecastDto
            )
            val successResponse = createMockResponse(mockApiResponse, isSuccessful = true)
            coEvery {
                weatherApiService.getForecastWeather(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns successResponse

            // WHEN
            val result = weatherRepository.getForecastData(dummyApiKey,days = 1)

            // THEN
            assertNotNull(result)
            assertEquals(1, result?.dailyForecasts?.size)
            assertEquals("2025-06-08", result?.dailyForecasts?.get(0)?.date)
            assertEquals(28.0, result?.dailyForecasts?.get(0)?.maxTempCelcius)
        }

    @Test
    fun `getForecastData - GIVEN API returns empty forecast list - THEN returns ForecastData with empty list`() =
        runTest {
            // GIVEN: Uma localização válida
            val mockLocation: Location =
                mockk { every { latitude } returns 1.0; every { longitude } returns 1.0 }
            coEvery { locationProvider.getCurrentLocation() } returns mockLocation

            // GIVEN: A API retorna uma resposta de sucesso mas com a lista de dias vazia
            val mockForecastDto = ForecastDto(emptyList()) // Lista vazia
            val mockLocationDto = LocationDto(
                "Test City",
                "Test Region",
                "Test Country",
                1.0,
                1.0,
                "tz",
                123L,
                "2025-06-07 17:30"
            )
            val mockApiResponse = ForecastApiResponseDto(
                location = mockLocationDto,
                current = null,
                forecast = mockForecastDto
            )
            val successResponse = createMockResponse(mockApiResponse, isSuccessful = true)
            coEvery {
                weatherApiService.getForecastWeather(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns successResponse

            // WHEN
            val result = weatherRepository.getForecastData(dummyApiKey,days = 1)

            // THEN
            assertNotNull("O objeto ForecastData não deve ser nulo", result)
            assertTrue(
                "A lista dailyForecasts deve estar vazia",
                result?.dailyForecasts?.isEmpty() == true
            )
        }
}
