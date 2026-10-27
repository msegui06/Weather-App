// Margo Segui (msegui@wpi.edu)
package wpics.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wpics.weather.model.location.LocationRepository

/**
 * this class is the [ViewModel] class for the city explorer app that handles
 * the state for all composable
 */

class LocationViewModel(
    private val repository: LocationRepository
) : ViewModel() {
    /**
     * stores the UI state that will be accessed by the various composable functions
     */
    private val _uiState = MutableStateFlow(LocationUIState())

    val uiState: StateFlow<LocationUIState> = _uiState

    /**
     * callback function that handles results from location permission requests
     */
    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(permissionGranted = granted)
    }

    /**
     * callback function that initiates the process of fetching a location
     */
    fun onFetchLocation() {
        if (!_uiState.value.permissionGranted) return // permissions not granted
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val cord = repository.getLastKnownOrCurrentLocation()
            _uiState.value = if (cord == null) { // location not found
                _uiState.value.copy(isLoading = false)
            } else { // location found
                val city = repository.reverseGeocodeCity(cord)
                _uiState.value.copy(
                    isLoading = false,
                    coordinate = cord,
                    city = city
                )
            }
        }
    }
}