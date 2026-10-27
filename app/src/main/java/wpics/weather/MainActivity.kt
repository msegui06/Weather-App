// Margo Segui (msegui@wpi.edu)
// TODO: Add your name here as a header comment (required on every Kotlin file you write or edit).
package wpics.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import wpics.weather.model.location.PlayServicesLocationRepository
import wpics.weather.model.weather.WeatherRepository
import wpics.weather.ui.components.MainScreen
import wpics.weather.ui.theme.WeatherTheme
import wpics.weather.viewmodel.LocationViewModel
import wpics.weather.viewmodel.WeatherViewModel

/**
 * This is the main entry activity for our weather application.
 *
 * TODO: Build the application the same way we built City Explorer:
 *  - Create the repositories in onCreate (location and weather).
 *  - Use a ViewModel factory (viewModelFactory / initializer) to create your ViewModels.
 *  - Replace the placeholder below with your own screen composable.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationRepo = PlayServicesLocationRepository(applicationContext)
        val weatherRepo = WeatherRepository(BuildConfig.WEATHER_KEY)
        val appFactory = viewModelFactory {
            initializer {
                LocationViewModel(locationRepo)
            }
            initializer {
                WeatherViewModel(weatherRepo)
            }
        }

        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: Replace this placeholder with your weather screen.
                    val locViewModel: LocationViewModel = viewModel(factory = appFactory)
                    val weatherViewModel: WeatherViewModel = viewModel(factory = appFactory)

                    MainScreen(
                        locViewModel = locViewModel,
                        weatherViewModel = weatherViewModel
                    )
                }
            }
        }
    }
}
