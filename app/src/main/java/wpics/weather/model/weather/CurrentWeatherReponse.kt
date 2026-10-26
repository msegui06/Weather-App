// Margo Segui (msegui@wpi.edu)
package wpics.weather.model.weather

import com.squareup.moshi.Json

/**
 * Response of API call GET("weather")
 *
 * API Call: https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
 */
data class CurrentWeatherResponse(
    val weather: List<Weather>? = null,
    val main: Main? = null,
    val visibility: Int? = null,
    val wind: Wind? = null,
    val rain: Rain? = null,
    val clouds: Clouds? = null,     // cloudy %
    val dt: Long? = null,           // time of data calculations (unix, UTC)
    val sys: Sys? = null,
    val timezone: Int? = null,      // time shift in seconds from UTC
    val name: String? = null,       // city name
)

data class Weather(
    val id: Int? = null,
    val main: String? = null,           // e.g. "rain"
    val description: String? = null,    // e.g. "moderate rain"
    val icon: String? = null            // e.g. "10d" -> openweather weather icon id
)

data class Main(
    val temp: Double? = null,
    @param:Json(name = "feels_like") val feelsLike: Double? = null,
    @param:Json(name = "temp_min") val tempMin: Double? = null,
    @param:Json(name = "temp_max") val tempMax: Double? = null,
    val pressure: Int? = null,                                      // hPa
    val humidity: Int? = null                                       // %
)

data class Wind(
    val speed: Double? = null,
    val deg: Int? = null,
    val gust: Double? = null
)

data class Rain(
    @param:Json(name = "1h") val oneHour: Double? = null,      // precipitation mm/h
    @param:Json(name = "3h") val threeHour: Double? = null
)

data class Clouds(
    val all: Int? = null    // cloudy %
)

data class Sys(
    val country: String? = null,
    val sunrise: Long? = null,      // unix, UTC
    val sunset: Long? = null        // unix, UTC
)