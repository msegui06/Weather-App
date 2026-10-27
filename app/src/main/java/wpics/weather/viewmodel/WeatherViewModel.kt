// Margo Segui (msegui@wpi.edu)
package wpics.weather.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import wpics.weather.model.location.Coordinate
import wpics.weather.model.weather.CurrentWeatherResponse
import wpics.weather.model.weather.UnitSystem
import wpics.weather.model.weather.WeatherRepository

class WeatherViewModel(
    private val repository: WeatherRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    private var lastCoordinate: Coordinate? = null
    private var fetchJob: Job? = null

    /**
     * launches a coroutine in viewModelScope
     * fetches the current weather and forecast
     * updates the state
     */
    fun fetchData(coordinate: Coordinate) {
        lastCoordinate = coordinate
        fetchJob?.cancel()  // stop any old request that is still getting fetched

        fetchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val units = _uiState.value.unitSystem
            try {
                val currentWeather = repository.getCurrentWeather(coordinate.lat, coordinate.lng, units)
                val forecast = repository.getForecast(coordinate.lat, coordinate.lng, units)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentWeatherResponse = currentWeather,
                    forecastResponse = forecast
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Failed to fetch weather", e)
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMessage(e))
            }
        }
    }

    /**
     * stores the selected UnitSystem and fetches a new set of data in the new units
     * default UnitSystem is Metric
     */
    fun updateUnitSystem(unitSystem: UnitSystem) {
        _uiState.value = _uiState.value.copy(unitSystem = unitSystem)
        // re-fetch new set of data in new unit system
        lastCoordinate?.let { fetchData(it) }
    }

    fun errorMessage(e: Exception): String = when (e) {
        is IOException -> "No internet connection. Check your network and try again"
        else -> "Couldn't load weather: ${e.message ?: "unknown error"}"
    }
}