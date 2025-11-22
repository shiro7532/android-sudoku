import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun animatedColorScheme(target: ColorScheme): ColorScheme {

    @Composable
    fun anim(target: Color) =
        animateColorAsState(target, tween(350)).value

    return target.copy(
        primary = anim(target.primary),
        onPrimary = anim(target.onPrimary),
        primaryContainer = anim(target.primaryContainer),
        onPrimaryContainer = anim(target.onPrimaryContainer),

        secondary = anim(target.secondary),
        onSecondary = anim(target.onSecondary),
        secondaryContainer = anim(target.secondaryContainer),
        onSecondaryContainer = anim(target.onSecondaryContainer),

        tertiary = anim(target.tertiary),
        onTertiary = anim(target.onTertiary),
        tertiaryContainer = anim(target.tertiaryContainer),
        onTertiaryContainer = anim(target.onTertiaryContainer),

        background = anim(target.background),
        onBackground = anim(target.onBackground),
        surface = anim(target.surface),
        onSurface = anim(target.onSurface),
        surfaceVariant = anim(target.surfaceVariant),
        onSurfaceVariant = anim(target.onSurfaceVariant),

        outline = anim(target.outline),
        outlineVariant = anim(target.outlineVariant),

        error = anim(target.error),
        onError = anim(target.onError),
        errorContainer = anim(target.errorContainer),
        onErrorContainer = anim(target.onErrorContainer),

        // NEW fields – copied, but not animated
        surfaceTint = target.surfaceTint,
        inverseSurface = target.inverseSurface,
        inverseOnSurface = target.inverseOnSurface,
        scrim = target.scrim,
        surfaceBright = target.surfaceBright,
        surfaceDim = target.surfaceDim,
        surfaceContainer = target.surfaceContainer,
        surfaceContainerHigh = target.surfaceContainerHigh,
        surfaceContainerHighest = target.surfaceContainerHighest,
        surfaceContainerLow = target.surfaceContainerLow,
        surfaceContainerLowest = target.surfaceContainerLowest,

        primaryFixed = target.primaryFixed,
        primaryFixedDim = target.primaryFixedDim,
        onPrimaryFixed = target.onPrimaryFixed,
        onPrimaryFixedVariant = target.onPrimaryFixedVariant,

        secondaryFixed = target.secondaryFixed,
        secondaryFixedDim = target.secondaryFixedDim,
        onSecondaryFixed = target.onSecondaryFixed,
        onSecondaryFixedVariant = target.onSecondaryFixedVariant,

        tertiaryFixed = target.tertiaryFixed,
        tertiaryFixedDim = target.tertiaryFixedDim,
        onTertiaryFixed = target.onTertiaryFixed,
        onTertiaryFixedVariant = target.onTertiaryFixedVariant
    )
}