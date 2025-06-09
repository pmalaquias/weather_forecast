package com.pmalaquias.weatherforecast.presentation.viewModel

import com.pmalaquias.weatherforecast.MainDispatcherRule
import com.pmalaquias.weatherforecast.domain.models.*
import com.pmalaquias.weatherforecast.domain.repository.WeatherRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var viewModel: WeatherViewModel

    private val dummyApiKey = "test_api_key"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        // O ViewModel é criado aqui, e o seu bloco 'init' será executado
        viewModel = WeatherViewModel(weatherRepository)
    }

    @Test
    fun `init - GIVEN repository success - THEN uiState is updated with data`() = runTest {
        // GIVEN: Criamos instâncias REAIS dos nossos modelos de dados para o teste
        val expectedWeatherCondition = WeatherCondition("Ensolarado", "https://icon.url", 0)
        val expectedLocation =
            LocationInfo("Cidade Teste", "Região Teste", "País Teste", "12:00", "UTC+0", 0.1, 0.1)
        val expectedCurrentWeather =
            CurrentWeather(25.0, expectedWeatherCondition, 10.0, "N", 5.0, 60, 26.0, 1, 1012.0, 0.0)
        val expectedWeatherData = WeatherData(expectedLocation, expectedCurrentWeather)

        @Test
        fun `init - GIVEN repository success - THEN uiState is updated with data`() = runTest {
            // GIVEN: Criamos instâncias REAIS dos nossos modelos de dados para o teste
            val expectedWeatherCondition = WeatherCondition("Ensolarado", "https://icon.url", 1000)
            val expectedLocation = LocationInfo(
                "Cidade Teste",
                "Região Teste",
                "País Teste",
                "12:00",
                "America/Sao_Paulo",
                -20.0,
                -43.0
            )
            val expectedCurrentWeather = CurrentWeather(
                25.0,
                expectedWeatherCondition,
                10.0,
                "N",
                5.0,
                60,
                26.0,
                1,
                1012.0,
                0.0
            )
            val expectedWeatherData = WeatherData(expectedLocation, expectedCurrentWeather)

            val expectedDailyForecast = DailyForecast(
                "2025-06-08",
                30.0,
                20.0,
                25.0,
                expectedWeatherCondition,
                "06:00",
                "18:00",
                10,
                0.0,
                7.0,
                55.0
            )
            val expectedForecastData = ForecastData(listOf(expectedDailyForecast))

            // GIVEN: O repositório mockado retornará estes dados reais
            coEvery { weatherRepository.getCurrentWeatherData(dummyApiKey) } returns expectedWeatherData // Removido 'any()'
            coEvery {
                weatherRepository.getForecastData(
                    dummyApiKey,
                    any()
                )
            } returns expectedForecastData // Removido 'any()'

            // WHEN: O ViewModel já foi inicializado no setUp e chamou o init.
            // Apenas precisamos de esperar que as coroutines terminem.
            advanceUntilIdle()

            // THEN: O estado final da UI deve corresponder exatamente aos dados que criámos
            val finalState = viewModel.uiState.value

            assertFalse("isInitialLoading deveria ser falso", finalState.isInitialLoading)
            assertNull("errorMessage deveria ser nulo", finalState.errorMessage)
            assertNotNull("weatherData não deveria ser nulo", finalState.weatherData)
            assertEquals(
                "Os objetos weatherData devem ser iguais",
                expectedWeatherData,
                finalState.weatherData
            )
            assertNotNull("forecastData não deveria ser nulo", finalState.forecastData)
            assertEquals(
                "Os objetos forecastData devem ser iguais",
                expectedForecastData,
                finalState.forecastData
            )
        }

        @Test
        fun `init - GIVEN repository failure - THEN uiState is updated with error`() = runTest {
            // GIVEN: O repositório retornará falha (nulo) explicitamente para ambas as chamadas
            coEvery { weatherRepository.getCurrentWeatherData(dummyApiKey) } returns null
            coEvery { weatherRepository.getForecastData(dummyApiKey, any()) } returns null

            // WHEN: Espera as chamadas no init terminarem
            advanceUntilIdle()

            // THEN: O estado da UI deve refletir o erro
            val finalState = viewModel.uiState.value

            assertFalse("isInitialLoading deveria ser falso", finalState.isInitialLoading)
            assertNull("weatherData deveria ser nulo", finalState.weatherData)
            assertNull("forecastData deveria ser nulo", finalState.forecastData)
            assertNotNull("errorMessage não deveria ser nulo", finalState.errorMessage)
        }

        @Test
        fun `init - GIVEN only forecast fails - THEN uiState has weatherData and error message`() =
            runTest {
                // GIVEN: A busca do tempo atual funciona, mas a do forecast falha
                val expectedWeatherCondition = WeatherCondition("Ensolarado", "https://icon.url", 1)
                val expectedLocation = LocationInfo(
                    "Cidade Teste",
                    "Região Teste",
                    "País Teste",
                    "12:00",
                    "UTC+0",
                    0.1,
                    0.1
                )
                val expectedCurrentWeather = CurrentWeather(
                    25.0,
                    expectedWeatherCondition,
                    10.0,
                    "N",
                    5.0,
                    60,
                    26.0,
                    1,
                    1012.0,
                    0.0
                )
                val expectedWeatherData = WeatherData(expectedLocation, expectedCurrentWeather)

                coEvery { weatherRepository.getCurrentWeatherData(dummyApiKey) } returns expectedWeatherData
                coEvery {
                    weatherRepository.getForecastData(
                        dummyApiKey,
                        any()
                    )
                } returns null // Forecast falha

                // WHEN
                advanceUntilIdle()

                // THEN
                val finalState = viewModel.uiState.value

                assertNotNull("weatherData não deveria ser nulo", finalState.weatherData)
                assertEquals(expectedWeatherData, finalState.weatherData)
                assertNull("forecastData deveria ser nulo", finalState.forecastData)
                assertNotNull(
                    "errorMessage deveria existir para a falha do forecast",
                    finalState.errorMessage
                )
            }
    }
}