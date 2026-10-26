// Margo Segui (msegui@wpi.edu)
package wpics.weather.model.weather

enum class UnitSystem(
    val apiVal: String,
    val displayName: String,
    val tempSymbol: String,
    val speedUnit: String
) {
    Metric("metric", "Metric (°C, m/s)", "°C", "m/s"),
    Imperial("imperial", "Imperial (°F, mph)", "°F", "mph"),
    Standard("standard", "Standard (K, m/s)", "K", "m/s")
}