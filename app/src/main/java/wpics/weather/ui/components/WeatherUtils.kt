// Margo Segui (msegui@wpi.edu)
package wpics.weather.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import wpics.weather.model.weather.CurrentWeatherResponse
import wpics.weather.model.weather.ForecastList
import wpics.weather.model.weather.ForecastResponse
import wpics.weather.model.weather.UnitSystem
import wpics.weather.ui.theme.ClearDayBottom
import wpics.weather.ui.theme.ClearDayTop
import wpics.weather.ui.theme.ClearNightBottom
import wpics.weather.ui.theme.ClearNightTop
import wpics.weather.ui.theme.CloudsDayBottom
import wpics.weather.ui.theme.CloudsDayTop
import wpics.weather.ui.theme.CloudsNightBottom
import wpics.weather.ui.theme.CloudsNightTop
import wpics.weather.ui.theme.MistDayBottom
import wpics.weather.ui.theme.MistDayTop
import wpics.weather.ui.theme.MistNightBottom
import wpics.weather.ui.theme.MistNightTop
import wpics.weather.ui.theme.RainDayBottom
import wpics.weather.ui.theme.RainDayTop
import wpics.weather.ui.theme.RainNightBottom
import wpics.weather.ui.theme.RainNightTop
import wpics.weather.ui.theme.SnowDayBottom
import wpics.weather.ui.theme.SnowDayTop
import wpics.weather.ui.theme.SnowNightBottom
import wpics.weather.ui.theme.SnowNightTop
import wpics.weather.ui.theme.StormDayBottom
import wpics.weather.ui.theme.StormDayTop
import wpics.weather.ui.theme.StormNightBottom
import wpics.weather.ui.theme.StormNightTop
import wpics.weather.ui.theme.WeatherButtonContent
import wpics.weather.ui.theme.WeatherCardScrim
import java.time.ZoneOffset
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * misc. composables and functions
 */

@Composable
fun ScrimCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardScrim,
            contentColor = Color.White
        )
    ) {
        Column(
            Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun WeatherIcon(
    icon: String?,
    description: String?,
    size: Dp
) {
    if (icon == null) {
        Spacer(Modifier.size(size))
    } else {
        AsyncImage(
            model = iconUrl(icon),
            contentDescription = description,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun UnitSwitcher(
    selected: UnitSystem,
    onSelect: (UnitSystem) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UnitSystem.values().forEach { unit ->
            val modifier = Modifier.weight(1f)
            if (unit == selected) {
                Button(
                    onClick = { onSelect(unit) },
                    modifier = modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = WeatherButtonContent
                    )
                ) {
                    Text(unit.name, maxLines = 1)
                }
            } else {
                OutlinedButton(
                    onClick = { onSelect(unit) },
                    modifier = modifier,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(unit.name, maxLines = 1)
                }
            }
        }
    }
}

enum class WeatherCondition {
    Clear,
    Clouds,
    Rain,
    Thunderstorm,
    Snow,
    Mist
}

fun conditionFor(id: Int?): WeatherCondition = when (id) {
    in 200..232 -> WeatherCondition.Thunderstorm
    in 300..531 -> WeatherCondition.Rain
    in 600..622 -> WeatherCondition.Snow
    in 700..781 -> WeatherCondition.Mist
    800 -> WeatherCondition.Clear
    in 801..804 -> WeatherCondition.Clouds
    else -> WeatherCondition.Clear
}

fun isDaytime(
    currentWeather: CurrentWeatherResponse
): Boolean {
    val now = currentWeather.dt
    val sunrise = currentWeather.sys?.sunrise
    val sunset = currentWeather.sys?.sunset

    if (now != null && sunrise != null && sunset != null) return now in sunrise until sunset

    return currentWeather.weather?.firstOrNull()?.icon?.endsWith("d") ?: true
}

fun backgroundColors(
    condition: WeatherCondition,
    isDay: Boolean,
    darkTheme: Boolean
): Pair<Color, Color> {
    val colors: Pair<Color, Color> = when (condition) {
        WeatherCondition.Clear -> if (isDay) ClearDayTop to ClearDayBottom else ClearNightTop to ClearNightBottom
        WeatherCondition.Clouds -> if (isDay) CloudsDayTop to CloudsDayBottom else CloudsNightTop to CloudsNightBottom
        WeatherCondition.Rain -> if (isDay) RainDayTop to RainDayBottom else RainNightTop to RainNightBottom
        WeatherCondition.Thunderstorm -> if (isDay) StormDayTop to StormDayBottom else StormNightTop to StormNightBottom
        WeatherCondition.Snow -> if (isDay) SnowDayTop to SnowDayBottom else SnowNightTop to SnowNightBottom
        WeatherCondition.Mist -> if (isDay) MistDayTop to MistDayBottom else MistNightTop to MistNightBottom
    }
    val (top, bottom) = colors

    return if (darkTheme && isDay) {
        lerp(top, Color.Black, 0.3f) to lerp(bottom, Color.Black, 0.3f)
    } else top to bottom
}

fun formatTemp(
    temp: Double?,
    unitSystem: UnitSystem
): String = if (temp == null) "--" else "${temp.roundToInt()}${unitSystem.tempSymbol}"

fun formatSpeed(
    speed: Double?,
    unitSystem: UnitSystem
): String = if (speed == null) "--" else "%.1f %s".format(speed, unitSystem.speedUnit)

fun formatTime(
    epochSeconds: Long?,
    timezoneSeconds: Int?,
    pattern: String = "h:mm a"
): String {
    if (epochSeconds == null) return "--"

    val offset = ZoneOffset.ofTotalSeconds((timezoneSeconds ?: 0))
    return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).format((Instant.ofEpochSecond(epochSeconds).atOffset(offset)))
}

fun iconUrl(icon: String): String = "https://openweathermap.org/img/wn/${icon}@2x.png"

data class DailyForecast(
    val label: String,
    val low: Double?,
    val high: Double?,
    val icon: String?,
    val description: String?
)

fun groupForecastByDay(
    forecast: ForecastResponse?,
    maxDays: Int = 5
): List<DailyForecast> {
    val offset = ZoneOffset.ofTotalSeconds(forecast?.city?.timezone ?: 0)
    val today: LocalDate = OffsetDateTime.now(offset).toLocalDate()

    val snapshots = forecast?.list.orEmpty().mapNotNull { item: ForecastList ->
        val dt = item.dt ?: return@mapNotNull null
        Instant.ofEpochSecond(dt).atOffset(offset) to item
    }

    return snapshots
        .groupBy({ it.first.toLocalDate() }, { it })
        .entries
        .take(maxDays)
        .map { (date, snaps) ->
            val lows = snaps.mapNotNull { it.second.main?.tempMin ?: it.second.main?.temp }
            val highs = snaps.mapNotNull { it.second.main?.tempMax ?: it.second.main?.temp }
            val midday = snaps.minByOrNull { abs(it.first.hour - 12) }?.second
            val weather = midday?.weather?.firstOrNull()
            DailyForecast(
                label = if (date == today) "Today" else date.dayOfWeek.getDisplayName(TextStyle.SHORT,
                    Locale.getDefault()),
                low = lows.minOrNull(),
                high = highs.maxOrNull(),
                icon = weather?.icon?.let { if (it.endsWith("n")) it.dropLast(1) + "d" else it },
                description = weather?.description
            )
        }
}