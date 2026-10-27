// Margo Segui (msegui@wpi.edu)
package wpics.weather.viewmodel

import wpics.weather.model.weather.CurrentWeatherResponse
import wpics.weather.model.weather.ForecastResponse
import wpics.weather.model.weather.UnitSystem

data class WeatherUIState(
    val isLoading: Boolean = false,
    val currentWeatherResponse: CurrentWeatherResponse? = null,
    val forecastResponse: ForecastResponse? = null,
    val errorMessage: String? = null,
    val unitSystem: UnitSystem = UnitSystem.Metric  // default is Metric
)