// Margo Segui (msegui@wpi.edu)
package wpics.weather.model.weather

/**
 * Response of the API call GET("forecast")
 *
 * API Call: api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}
 */
data class ForecastResponse(
    val list: List<ForecastList>? = null,
    val city: City? = null
)

data class ForecastList(
    val dt: Long? = null,
    val main: Main? = null,
    val weather: List<Weather>? = null,
    val clouds: Clouds? = null,
    val wind: Wind? = null,
    val pop: Double? = null,                // probability of precipitation, 0..1
    val rain: Rain? = null
)

data class City(
    val name: String? = null,
    val country: String? = null,
    val timezone: Int? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)