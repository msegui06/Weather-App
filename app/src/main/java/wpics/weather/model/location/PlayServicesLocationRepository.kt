// Margo Segui (msegui@wpi.edu)
package wpics.weather.model.location

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * an implementation of [LocationRepository] where we retrieve the location
 * based on Google Play Location Services API
 */
class PlayServicesLocationRepository(
    private val context: Context
) : LocationRepository {
    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override suspend fun getLastKnownOrCurrentLocation(): Coordinate? {
        // location services
        val client = LocationServices.getFusedLocationProviderClient(context)

        // attempt to retrieve location
        try {
            // create a suspendable coroutine that fetches the last location if it is not available
            return suspendCancellableCoroutine { continuation ->
                // store the client's last location
                client.lastLocation.addOnSuccessListener { location ->
                    // we have a last location already
                    if (location != null) {
                        continuation.resume(Coordinate(location.latitude, location.longitude))
                    } else {
                        // hand case where GPS is off
                        val cts = CancellationTokenSource() // allows the request to be canceled if it takes too long
                        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                            // success!
                            .addOnSuccessListener { cur ->
                                continuation.resume(cur?.let {
                                    Coordinate(it.latitude, it.longitude)
                                })
                            }
                            // fail
                            .addOnFailureListener {
                                Log.e("WeatherApp", "Location is null. GPS may be disabled ")
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.e("WeatherApp", "Location fetch failed: ${e.message}")
                }
            }
        } catch (e: SecurityException) {
            Log.e("WeatherApp", "Permission error: ${e.message}")
            return null
        }
    }

    override suspend fun reverseGeocodeCity(coordinate: Coordinate): String? =
        withContext(Dispatchers.IO) {
            try {
                val g = Geocoder(context, Locale.getDefault())
                val r = g.getFromLocation(coordinate.lat, coordinate.lng, 1)

                // firstAddress, null, or fall-back to county-level name
                r?.firstOrNull()?.locality ?: r?.firstOrNull()?.subAdminArea
            } catch (e: Exception) {
                null
            }
        }
}