// Margo Segui (msegui@wpi.edu)
package wpics.weather.model.location

/**
 * an interface that allows us to retrieve the last known or the current location if not known
 */
interface LocationRepository {
    /**
     * A special function that can be paused and resumed without
     * blocking the underlying thread when retrieving a location
     *
     * @return A [Coordinate] containing latitude and longitude if there is one, null otherwise.
     */
    suspend fun getLastKnownOrCurrentLocation(): Coordinate?

    /**
     * A special function that can be paused and resumed:
     * gets a city name from coordinates without blocking the thread
     *
     * @param coordinate A coordinate
     *
     * @return A city name if found, {@code null} otherwise.
     */
    suspend fun reverseGeocodeCity(coordinate: Coordinate): String?
}