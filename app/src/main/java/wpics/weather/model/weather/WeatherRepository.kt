// Margo Segui (msegui@wpi.edu)
package wpics.weather.model.weather

import com.google.android.gms.common.api.internal.ApiKey

open class WeatherRepository(
    private val apiKey: String,
    private val api: WeatherAPI = RetrofitProvider.api
) {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: UnitSystem
    ) = api.getCurrentWeather(lat, lon, apiKey, units.apiVal)

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: UnitSystem
    ) = api.getForecast(lat, lon, apiKey, units.apiVal)
}