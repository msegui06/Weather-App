// Margo Segui (msegui@wpi.edu)
package wpics.weather.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wpics.weather.model.weather.CurrentWeatherResponse
import wpics.weather.model.weather.UnitSystem

@Composable
fun CurrentWeatherHeader(
    currentWeather: CurrentWeatherResponse,
    unitSystem: UnitSystem
) {
    val weather = currentWeather.weather?.firstOrNull()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentWeather.name ?: "Unknown location",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIcon(weather?.icon, weather?.description, 96.dp)
            Text(
                text = formatTemp(currentWeather.main?.temp, unitSystem),
                style = MaterialTheme.typography.displayLarge
            )
        }

        Text(
            text = weather?.description?.replaceFirstChar { it.uppercase() } ?: "",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Feels like ${formatTemp(currentWeather.main?.feelsLike, unitSystem)}    H ${formatTemp(currentWeather.main?.tempMax, unitSystem)}   L ${formatTemp(currentWeather.main?.tempMin, unitSystem)}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}