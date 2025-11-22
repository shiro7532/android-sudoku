package anangram.apps.sudoku.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import animatedColorScheme
import kotlinx.coroutines.flow.map

// Light theme
val PeachLightColorScheme = lightColorScheme(
    primary = PeachPrimary,
    onPrimary = PeachOnPrimary,
    primaryContainer = PeachPrimaryContainer,
    onPrimaryContainer = PeachOnPrimaryContainer,
    secondary = PeachSecondary,
    onSecondary = PeachOnSecondary,
    secondaryContainer = PeachSecondaryContainer,
    onSecondaryContainer = PeachOnSecondaryContainer,
    background = PeachBackgroundLight,
    surface = PeachSurfaceLight,
    onBackground = Color.Black,
    onSurface = Color.Black,
    tertiary = PeachTertiary,
    onTertiary = PeachOnTertiary,
    error = PeachError
)

val PeachDarkColorScheme = darkColorScheme(
    primary = PeachPrimary,
    onPrimary = PeachOnPrimary,
    primaryContainer = PeachPrimaryContainer,
    onPrimaryContainer = PeachOnPrimaryContainer,
    secondary = PeachSecondary,
    onSecondary = PeachOnSecondary,
    secondaryContainer = PeachSecondaryContainer,
    onSecondaryContainer = PeachOnSecondaryContainer,
    background = PeachBackgroundDark,
    surface = PeachSurfaceDark,
    onBackground = Color.White,
    onSurface = Color.White,
    tertiary = PeachTertiary,
    onTertiary = PeachOnTertiary,
    error = PeachError
)

val Context.themeDataStore by preferencesDataStore("theme_preferences")

object ThemePrefs {
    val THEME_INDEX = intPreferencesKey("theme_index")
}

class ThemeRepository(private val context: Context) {

    val themeIndex = context.themeDataStore.data.map { prefs ->
        prefs[ThemePrefs.THEME_INDEX] ?: 0
    }

    suspend fun setTheme(index: Int) {
        context.themeDataStore.edit { prefs ->
            prefs[ThemePrefs.THEME_INDEX] = index
        }
    }
}

@Composable
fun SudokuTheme(
    themeIndex: Int,   // <-- controlled by datastore + VM
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Your 5 ColorSchemes
    val customPalettes = ThemePresets.All

    val colorScheme =
        if (themeIndex in customPalettes.indices) {
            customPalettes[themeIndex].let {
                if (darkTheme)
                    it.dark
                else it.light
            }  // <-- user-set theme
        } else {
            // fallback to system/dynamic
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)

                darkTheme -> PeachDarkColorScheme
                else -> PeachLightColorScheme
            }
        }
    val animatedScheme = animatedColorScheme(colorScheme)

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = Typography,
        content = content
    )
}
