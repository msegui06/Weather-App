// Margo Segui (msegui@wpi.edu)
package wpics.weather.viewmodel

import wpics.weather.model.location.Coordinate

/**
 * current UI state for Weather app
 */
data class LocationUIState(
    val permissionGranted: Boolean = false,

    val isLoading: Boolean = false,

    val coordinate: Coordinate? = null,

    val city: String? = null,
)