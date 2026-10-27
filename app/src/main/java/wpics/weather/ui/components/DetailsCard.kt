// Margo Segui (msegui@wpi.edu)
package wpics.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import wpics.weather.model.weather.CurrentWeatherResponse
import wpics.weather.model.weather.ForecastResponse
import wpics.weather.model.weather.UnitSystem
import wpics.weather.ui.theme.WeatherContentColor

@Composable
fun DetailsCard(
    currentWeather: CurrentWeatherResponse,
    unitSystem: UnitSystem,
    timezone: Int?
) {
    val rain = currentWeather.rain?.oneHour ?: 0.0
    val details = listOf(
        "Humidity" to (currentWeather.main?.humidity?.let { "$it%" } ?: "--"),
        "Pressure" to (currentWeather.main?.pressure?.let { "$it hPa" } ?: "--"),
        "Precipitation (1h)" to "%.1f mm".format(rain),
        "Wind" to formatSpeed(currentWeather.wind?.speed, unitSystem),
        "Sunrise" to formatTime(currentWeather.sys?.sunrise, timezone),
        "Sunset" to formatTime(currentWeather.sys?.sunset, timezone)
    )

    ScrimCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            details.chunked(2).forEach { pair ->
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    pair.forEach { (label, value) ->
                        DetailItem(label, value, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = WeatherContentColor.copy(alpha = 0.85f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun HourlyCard(
    forecast: ForecastResponse?,
    unitSystem: UnitSystem,
    timezone: Int?
) {
    val hours = forecast?.list.orEmpty().take(8)
    if (hours.isEmpty()) return
    ScrimCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Next 24 hours",
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hours) { item ->
                    val weather = item.weather?.firstOrNull()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            formatTime(item.dt, timezone ?: forecast?.city?.timezone, "h a"),
                            style = MaterialTheme.typography.labelMedium
                        )
                        WeatherIcon(weather?.icon, weather?.description, 44.dp)
                        Text(
                            formatTemp(item.main?.temp, unitSystem),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastCard(
    days: List<DailyForecast>,
    unitSystem: UnitSystem
) {
    ScrimCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "5-day forecast",
                style = MaterialTheme.typography.titleMedium
            )

            if (days.isEmpty()) {
                Text(
                    "Forecast isn't available right now.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                days.forEach { day ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            day.label,
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        WeatherIcon(day.icon, day.description, 40.dp)
                        Text(
                            "L ${formatTemp(day.low, unitSystem)}",
                            Modifier.width(72.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WeatherContentColor.copy(alpha = 0.85f)
                        )
                        Text(
                            "H ${formatTemp(day.high, unitSystem)}",
                            Modifier.width(72.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (days.size < 5) {
                    Text(
                        "Only ${days.size} day${if (days.size == 1) "" else "s"} of forecast available",
                        style = MaterialTheme.typography.labelMedium,
                        color = WeatherContentColor.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}