package wpics.weather.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import wpics.weather.model.weather.UnitSystem
import wpics.weather.viewmodel.LocationUIState
import wpics.weather.viewmodel.LocationViewModel
import wpics.weather.viewmodel.WeatherUIState
import wpics.weather.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHome(
    modifier: Modifier = Modifier,
    locViewModel: LocationViewModel,
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current
    val locationState by locViewModel.uiState.collectAsState()
    val weatherState by weatherViewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.values.any { it }
        locViewModel.onPermissionResult(granted)
        if (granted) locViewModel.onFetchLocation()
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // double check permissions
    val requestPerms = {
        if (hasLocationPermission(context)) {
            locViewModel.onPermissionResult(true)
            locViewModel.onFetchLocation()
        } else {
            launcher.launch(permissions)
        }
    }

    // first launch: use curr perms if available, otherwise ask
    LaunchedEffect(Unit) { requestPerms() }

    // load weather for every new coord
    LaunchedEffect(locationState.coordinate) {
        locationState.coordinate?.let { weatherViewModel.fetchData(it) }
    }

    WeatherHomeContent(
        locationState = locationState,
        weatherState = weatherState,
        onRequestPermission = requestPerms,
        onFetchLocation = locViewModel::onFetchLocation,
        onUnitSelected = weatherViewModel::updateUnitSystem,
        onRetyWeather = {locationState.coordinate?.let { weatherViewModel.fetchData(it) }},
        modifier = modifier
    )
}
// -----------------------------------------Helper functions-----------------------------------------
/**
 * checks if fine and course location access has been granted from the user
 */
private fun hasLocationPermission(context: Context): Boolean =
    listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

// -------------------------------------------Composables-------------------------------------------
@Composable
fun WeatherHomeContent(
    locationState: LocationUIState,
    weatherState: WeatherUIState,
    onRequestPermission: () -> Unit,
    onFetchLocation: () -> Unit,
    onUnitSelected: (UnitSystem) -> Unit,
    onRetyWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        // location permission not granted yet
        !locationState.permissionGranted -> MessageScreen(
            title = "Location permission needed.",
            message = "This app uses your location to show the weather for your current location. Please allow location access to continue.",
            primaryLabel = "Try again",
            onPrimary = onRequestPermission,
            modifier = modifier
        )
        // location coordinate is still fetching
        locationState.coordinate == null && locationState.isLoading ->
            LoadingScreen("Finding your location...", modifier)

        // location coordinate couldn't be fetched
        locationState.coordinate == null -> MessageScreen(
            title = "Couldn't find your location.",
            message = "Make sure location is turned on for your device and try again.",
            primaryLabel = "Try again",
            onPrimary = onFetchLocation,
            modifier = modifier
        )

        // location coordinate fetched!
        else -> WeatherScreen(
            uiState = weatherState,
            onUnitSelected = onUnitSelected,
            onRetry = onRetyWeather,
            modifier = modifier
        )
    }
}

@Composable
fun MessageScreen(
    title: String,
    message: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier
    ) {
    Surface(
        modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onPrimary
            ) {
                Text(primaryLabel)
            }
        }
    }
}

@Composable
private fun LoadingScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(
                message,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}