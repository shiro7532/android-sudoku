package anangram.apps.sudoku.ui.theme

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

// Dark theme
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

@Composable
fun SudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> PeachDarkColorScheme
        else -> PeachLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}