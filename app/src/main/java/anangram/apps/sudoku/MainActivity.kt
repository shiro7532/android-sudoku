package anangram.apps.sudoku

import anangram.apps.sudoku.ui.navigation.AppNavigation
import anangram.apps.sudoku.ui.theme.SudokuTheme
import anangram.apps.sudoku.ui.theme.ThemeRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val themeRepository: ThemeRepository = get()
        val currentThemeIndex = runBlocking { themeRepository.themeIndex.first() }
        enableEdgeToEdge()
        setContent {
            val themeRepository: ThemeRepository = get()
            val themeIndex by themeRepository.themeIndex.collectAsState(initial = currentThemeIndex)
            SudokuTheme(themeIndex) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
