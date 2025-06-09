package com.pmalaquias.weatherforecast.data.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationProviderTest {

    @RelaxedMockK
    private lateinit var contextMock: Context

    private lateinit var fusedLocationClientMock: FusedLocationProviderClient
    private lateinit var locationProvider: LocationProvider

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(ContextCompat::class, LocationServices::class, Log::class)

        // Configuração padrão do Log
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0

        // Mock da criação do FusedLocationProviderClient
        fusedLocationClientMock = mockk()
        every { LocationServices.getFusedLocationProviderClient(contextMock) } returns fusedLocationClientMock

        locationProvider = LocationProvider(contextMock)
    }

    @After
    fun tearDown() {
        unmockkAll() // Limpa todos os mocks (estáticos e de instância)
    }

    /**
     * Helper function to create a mock Task object for simulating Google Play Services API results.
     * This avoids using coEvery on the await() extension function.
     *
     * @param T The type of the task result.
     * @param result The successful result to be returned by the task.
     * @param exception The exception to be thrown by the task on failure.
     * @return A mocked [Task] configured with the specified result or exception.
     */
    private fun <T> mockTask(result: T? = null, exception: Exception? = null): Task<T> {
        val task: Task<T> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.isCanceled } returns false
        every { task.isSuccessful } returns (exception == null)
        every { task.result } returns result
        every { task.exception } returns exception

        // The real await() extension function will check these properties.
        return task
    }


    @Test
    fun `getCurrentLocation - GIVEN no location permission - THEN returns null`() = runTest {
        // GIVEN: Permissão negada
        every { ContextCompat.checkSelfPermission(contextMock, Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_DENIED
        every { ContextCompat.checkSelfPermission(contextMock, Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_DENIED

        // WHEN: A função é chamada
        val actualLocation = locationProvider.getCurrentLocation()

        // THEN: O resultado deve ser nulo
        assertNull("A localização deve ser nula quando a permissão é negada", actualLocation)
        verify { Log.w("LocationProvider", "Location permission not granted.") }
        verify(exactly = 0) { fusedLocationClientMock.lastLocation } // Nenhuma chamada de API deve ocorrer
    }

    @Test
    fun `getCurrentLocation - GIVEN permission and valid lastLocation - THEN returns lastLocation`() = runTest {
        // GIVEN: Permissão concedida
        every { ContextCompat.checkSelfPermission(contextMock, any<String>()) } returns PackageManager.PERMISSION_GRANTED

        // GIVEN: lastLocation retorna uma Task de sucesso com uma localização válida
        val expectedLocation: Location = mockk()
        val successTask = mockTask(result = expectedLocation)
        every { fusedLocationClientMock.lastLocation } returns successTask

        // WHEN
        val actualLocation = locationProvider.getCurrentLocation()

        // THEN: O resultado deve ser a localização esperada
        assertEquals("A localização retornada deve ser a de lastLocation", expectedLocation, actualLocation)
        // E getCurrentLocation (a chamada de API mais custosa) NÃO deve ser chamada
        verify(exactly = 0) { fusedLocationClientMock.getCurrentLocation(any<Int>(), any()) }
    }

    @Test
    fun `getCurrentLocation - GIVEN permission, null lastLocation, valid current API location - THEN returns current API location`() = runTest {
        // GIVEN: Permissão concedida
        every { ContextCompat.checkSelfPermission(contextMock, any<String>()) } returns PackageManager.PERMISSION_GRANTED

        // GIVEN: lastLocation retorna uma Task de sucesso, mas com resultado nulo
        val nullResultTask = mockTask<Location>(result = null)
        every { fusedLocationClientMock.lastLocation } returns nullResultTask

        // GIVEN: getCurrentLocation (da API) retorna uma Task de sucesso com uma localização válida
        val expectedLocation: Location = mockk()
        val successApiTask = mockTask(result = expectedLocation)
        every { fusedLocationClientMock.getCurrentLocation(any<Int>(), any()) } returns successApiTask

        // WHEN
        val actualLocation = locationProvider.getCurrentLocation()

        // THEN: O resultado deve ser a localização vinda da API
        assertEquals("A localização retornada deve ser a da API getCurrentLocation", expectedLocation, actualLocation)
        verify { Log.d("LocationProvider", "Last known location is null.") }
        verify { Log.d("LocationProvider", "Requesting current location with getCurrentLocation API...") }
    }

    @Test
    fun `getCurrentLocation - GIVEN permission but all sources return null - THEN returns null`() = runTest {
        // GIVEN: Permissão concedida
        every { ContextCompat.checkSelfPermission(contextMock, any()) } returns PackageManager.PERMISSION_GRANTED

        // GIVEN: Ambas as fontes de localização retornam Tasks com resultado nulo
        val nullResultTask = mockTask<Location>(result = null)
        every { fusedLocationClientMock.lastLocation } returns nullResultTask
        every { fusedLocationClientMock.getCurrentLocation(any<Int>(), any()) } returns nullResultTask

        // WHEN
        val actualLocation = locationProvider.getCurrentLocation()

        // THEN: O resultado final deve ser nulo
        assertNull("A localização deve ser nula se ambas as fontes falharem", actualLocation)
    }

    @Test
    fun `getCurrentLocation - GIVEN permission but lastLocation throws exception - THEN continues and returns location from API`() = runTest {
        // GIVEN: Permissão concedida
        every { ContextCompat.checkSelfPermission(contextMock, any()) } returns PackageManager.PERMISSION_GRANTED

        // GIVEN: lastLocation retorna uma Task com falha (exceção)
        val testException = SecurityException("Test exception")
        val failedTask = mockTask<Location>(exception = testException)
        every { fusedLocationClientMock.lastLocation } returns failedTask

        // GIVEN: A segunda chamada (getCurrentLocation) funciona e retorna uma localização
        val expectedLocation: Location = mockk()
        val successApiTask = mockTask(result = expectedLocation)
        every { fusedLocationClientMock.getCurrentLocation(any<Int>(), any()) } returns successApiTask

        // WHEN
        val actualLocation = locationProvider.getCurrentLocation()

        // THEN: O resultado deve ser a localização da segunda chamada, e o erro da primeira deve ser logado
        assertEquals("A localização da API deve ser retornada após exceção em lastLocation", expectedLocation, actualLocation)
        verify { Log.e("LocationProvider", "Error getting last known location: ${testException.message}", testException) }
    }
}
