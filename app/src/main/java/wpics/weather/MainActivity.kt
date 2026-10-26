// Margo Segui (msegui@wpi.edu)
// TODO: Add your name here as a header comment (required on every Kotlin file you write or edit).
package wpics.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wpics.weather.ui.theme.WeatherTheme

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
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: Replace this placeholder with your weather screen.
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Weather")
                    }
                }
            }
        }
    }
}
