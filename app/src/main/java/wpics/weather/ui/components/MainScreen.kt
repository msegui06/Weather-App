// Margo Segui (msegui@wpi.edu)
package wpics.weather.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wpics.weather.viewmodel.LocationViewModel
import wpics.weather.viewmodel.WeatherViewModel

@Composable
fun MainScreen(
    locViewModel: LocationViewModel,
    weatherViewModel: WeatherViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WEATHER_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(WEATHER_ROUTE) {
                WeatherHome(
                    locViewModel = locViewModel,
                    weatherViewModel = weatherViewModel
                )
            }
        }
    }
}