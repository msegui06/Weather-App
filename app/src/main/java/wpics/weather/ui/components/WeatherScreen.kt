// Margo Segui (msegui@wpi.edu)
package wpics.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import wpics.weather.model.weather.CurrentWeatherResponse
import wpics.weather.model.weather.UnitSystem
import wpics.weather.ui.theme.WeatherButtonContent
import wpics.weather.ui.theme.WeatherContentColor
import wpics.weather.ui.theme.WeatherErrorBanner
import wpics.weather.viewmodel.WeatherUIState

@Composable
fun WeatherScreen(
    uiState: WeatherUIState,
    onUnitSelected: (UnitSystem) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentState = uiState.currentWeatherResponse
    val conditions = conditionFor(currentState?.weather?.firstOrNull()?.id)
    val isDay = currentState?.let { isDaytime(it) } ?: true
    val (top, bottom) = backgroundColors(conditions, isDay, isSystemInDarkTheme())

    CompositionLocalProvider(
        LocalContentColor provides WeatherContentColor
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(top, bottom)))
        ) {
            when {
                // when the current state isn't null, show weather content
                currentState != null -> WeatherContent(
                    uiState,
                    currentState,
                    onUnitSelected,
                    onRetry
                )

                uiState.errorMessage != null -> FullScreenError(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = WeatherContentColor
                )
            }
        }
    }
}
// -------------------------------------------Composables-------------------------------------------
@Composable
private fun WeatherContent(
    uiState: WeatherUIState,
    currentState: CurrentWeatherResponse,
    onUnitSelected: (UnitSystem) -> Unit,
    onRetry: () -> Unit
) {
    val units = uiState.unitSystem
    val timezone = currentState.timezone ?: uiState.forecastResponse?.city?.timezone
    val days = groupForecastByDay(uiState.forecastResponse)

    LazyColumn(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.isLoading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = WeatherContentColor,
                    trackColor = WeatherContentColor.copy(alpha = 0.3f)
                )
            }
        }

        uiState.errorMessage?.let {
            message -> item {
                ErrorBanner(message, onRetry)
            }
        }

        item { UnitSwitcher(units, onUnitSelected) }
        item { CurrentWeatherHeader(currentState, units) }
        item { DetailsCard(currentState, units, timezone) }
        item { HourlyCard(uiState.forecastResponse, units, timezone) }
        item { DailyForecastCard(days, units) }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WeatherErrorBanner, contentColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                message,
                Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = onRetry,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun FullScreenError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment =  Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = WeatherButtonContent)
        ) { Text("Retry") }
    }
}