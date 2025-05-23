package com.pmalaquias.weatherforecast.data.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

/**
 * Provides access to the device's current location.
 *
 * This class encapsulates the logic for fetching the current geographical location
 * using Google Play Services' FusedLocationProviderClient. It handles permission
 * checks and attempts to retrieve the location efficiently.
 *
 * @property appContext The application context, used to access system services.
 */
class LocationProvider(private val appContext: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
    private val TAG = "LocationProvider" // Tag for logging

    /**
     * Asynchronously retrieves the current device location.
     *
     * This function first checks for location permissions (ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION).
     * If permissions are granted, it attempts to get the last known location. If that's unavailable
     * or too old, it requests a fresh location update using high accuracy.
     *
     * The method is annotated with `@SuppressLint("MissingPermission")` because the permission
     * check is performed dynamically within the function. The caller is still responsible for
     * requesting permissions from the user at the UI layer before calling this method.
     *
     * @return The current [Location] if successfully retrieved and permissions are granted,
     * or `null` if permissions are denied or an error occurs.
     */
    @SuppressLint("MissingPermission") // Permission check is done within the function
    suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessFineLocationPermission && !hasAccessCoarseLocationPermission) {
            Log.w(TAG, "Location permission not granted.")
            return null
        }

        // Try to get the last known location quickly
        try {
            Log.d(TAG, "Attempting to get the last known location...")
            var location: Location? = fusedLocationClient.lastLocation.await()
            if (location != null) {
                Log.d(TAG, "Last known location retrieved: $location")
                return location
            }
            Log.d(TAG, "Last known location is null.")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting last known location: ${e.message}", e)
            // Continue to try fetching the current location
        }

        // If the last known location is not available, request the current location.
        Log.d(TAG, "Requesting current location with getCurrentLocation API...")
        val cancellationTokenSource = CancellationTokenSource()
        try {
            // Try with high accuracy first, it might be slower but more reliable for an initial fix
            val currentLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, // Try with high accuracy
                cancellationTokenSource.token
            ).await() // Using .await()

            if (currentLocation != null) {
                Log.d(TAG, "Current location retrieved (HIGH_ACCURACY): $currentLocation")
            } else {
                Log.w(TAG, "Current location returned null (HIGH_ACCURACY).")
                // Could try with PRIORITY_BALANCED_POWER_ACCURACY as a fallback here if desired
            }
            return currentLocation
        } catch (e: SecurityException) {
            Log.e(TAG, "Security error requesting current location: ${e.message}", e)
            // This can happen if the permission was revoked between the check and the call
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting current location: ${e.message}", e)
        }

        // Fallback: If getCurrentLocation fails or takes too long, we could try with requestLocationUpdates
        // This is a more robust implementation to get a fix if getCurrentLocation fails.
        // However, for a "get location now" scenario, getCurrentLocation is generally preferred.
        // If you continue to have issues, this alternative could be explored.
        // Log.d(TAG, "Fallback: Trying with requestLocationUpdates (not implemented in this simple example)")

        return null // Returns null if all attempts fail
    }
}
