package anangram.apps.sudoku.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val PeachPrimary = Color(0xFFFFB07C) // soft Peach
val PeachOnPrimary = Color(0xFF4B1C11) // darker for contrast
val PeachPrimaryContainer = Color(0xFFFFDBCC)
val PeachOnPrimaryContainer = Color(0xFF2C0A00)

val PeachSecondary = Color(0xFFFFC8A2)
val PeachOnSecondary = Color(0xFF44291A)
val PeachSecondaryContainer = Color(0xFFFFE3D0)
val PeachOnSecondaryContainer = Color(0xFF2E1507)

val PeachBackgroundLight = Color(0xFFFFF8F5)
val PeachSurfaceLight = Color(0xFFFFF0E9)
val PeachBackgroundDark = Color(0xFF2C1C18)
val PeachSurfaceDark = Color(0xFF3A2723)

val PeachTertiary = Color(0xFFDCA38C)
val PeachOnTertiary = Color(0xFF3D1C0F)

val PeachError = Color(0xFFB00020)

data class ThemePreset(
    val name: String,
    val light: ColorScheme,
    val dark: ColorScheme
)

object ThemePresets {

    val Peach = ThemePreset(
        name = "Peach",
        light = lightColorScheme(
            primary = Color(0xFFBB5D5D),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDAD6),
            onPrimaryContainer = Color(0xFF410004),

            secondary = Color(0xFF775653),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFDAD6),
            onSecondaryContainer = Color(0xFF2C1512),

            tertiary = Color(0xFF6F5D2E),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFBDFA6),
            onTertiaryContainer = Color(0xFF261A00),

            background = Color(0xFFFDF3F2),
            onBackground = Color(0xFF201A19),

            surface = Color(0xFFF7EDED),
            onSurface = Color(0xFF201A19),
            surfaceVariant = Color(0xFFF5DDDA),
            onSurfaceVariant = Color(0xFF534341),

            outline = Color(0xFF857371),
            outlineVariant = Color(0xFFD8C2BF),

            error = Color(0xFFBA1A1A),
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002)
        ),
        dark = darkColorScheme(
            primary = Color(0xFFFFB3AC),
            onPrimary = Color(0xFF5F1318),
            primaryContainer = Color(0xFF7D2A2D),
            onPrimaryContainer = Color(0xFFFFDAD6),

            secondary = Color(0xFFE7BDB8),
            onSecondary = Color(0xFF442927),
            secondaryContainer = Color(0xFF5D3F3C),
            onSecondaryContainer = Color(0xFFFFDAD6),

            tertiary = Color(0xFFDDBE82),
            onTertiary = Color(0xFF3F2E04),
            tertiaryContainer = Color(0xFF574419),
            onTertiaryContainer = Color(0xFFFBDFA6),

            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFECE0DF),

            surface = Color(0xFF141314),
            onSurface = Color(0xFFECE0DF),
            surfaceVariant = Color(0xFF534341),
            onSurfaceVariant = Color(0xFFD8C2BF),

            outline = Color(0xFFA08C8A),
            outlineVariant = Color(0xFF534341),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF680003),
            errorContainer = Color(0xFF930006),
            onErrorContainer = Color(0xFFFFDAD6)
        )

    )

    val Lavender = ThemePreset(
        name = "Lavender",

        light = lightColorScheme(
            primary = Color(0xFF6650A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),

            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),

            tertiary = Color(0xFF7D5260),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31101D),

            background = Color(0xFFF7F2FA),   // soft Lavender
            onBackground = Color(0xFF1D1B20),

            surface = Color(0xFFF3EDF7),
            onSurface = Color(0xFF1D1B20),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),

            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0)
        ),
        dark = darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF371E73),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFEADDFF),

            secondary = Color(0xFFCCC2DC),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),

            tertiary = Color(0xFFEFB8C8),
            onTertiary = Color(0xFF4A2532),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),

            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E6),

            surface = Color(0xFF141316),
            onSurface = Color(0xFFE6E1E6),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),

            outline = Color(0xFF948F99),
            outlineVariant = Color(0xFF49454F)
        )
    )
    val Classic = ThemePreset(
        "Classic",
        light = lightColorScheme(
            primary = Color(0xFF3E4C59),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDCE3EA),
            onPrimaryContainer = Color(0xFF131C23),

            secondary = Color(0xFF52606D),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFD7DDE2),
            onSecondaryContainer = Color(0xFF0F1A21),

            tertiary = Color(0xFF829AB1),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFE4EEF5),
            onTertiaryContainer = Color(0xFF24323B),

            background = Color(0xFFF0F4F8),
            onBackground = Color(0xFF1A1D1F),

            surface = Color(0xFFE8EDF1),
            onSurface = Color(0xFF1A1D1F),
            surfaceVariant = Color(0xFFD8DEE3),
            onSurfaceVariant = Color(0xFF444A4F),

            outline = Color(0xFF767C81),
            outlineVariant = Color(0xFFC3C8CD),

            error = Color(0xFFBA1A1A),
            onError = Color.White
        ),
        dark = darkColorScheme(
            primary = Color(0xFFBAC8D5),
            onPrimary = Color(0xFF24323E),
            primaryContainer = Color(0xFF2F3D49),
            onPrimaryContainer = Color(0xFFDCE3EA),

            secondary = Color(0xFFBCC6CE),
            onSecondary = Color(0xFF263038),
            secondaryContainer = Color(0xFF3C474F),
            onSecondaryContainer = Color(0xFFD7DDE2),

            tertiary = Color(0xFFC6D3DD),
            onTertiary = Color(0xFF2C3A43),
            tertiaryContainer = Color(0xFF3F4D57),
            onTertiaryContainer = Color(0xFFE4EEF5),

            background = Color(0xFF1A1B1E),
            onBackground = Color(0xFFE3E5E8),

            surface = Color(0xFF121315),
            onSurface = Color(0xFFE3E5E8),
            surfaceVariant = Color(0xFF434A50),
            onSurfaceVariant = Color(0xFFC3C8CD),

            outline = Color(0xFF8D969D),
            outlineVariant = Color(0xFF434A50)
        )
    )

    val Forest = ThemePreset(
        "Forest",
        light = lightColorScheme(
            primary = Color(0xFF2A6041),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFB4E4C8),
            onPrimaryContainer = Color(0xFF042013),

            secondary = Color(0xFF1E3F2F),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFAEDDCB),
            onSecondaryContainer = Color(0xFF072116),

            tertiary = Color(0xFF68A57C),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFCFF1DB),
            onTertiaryContainer = Color(0xFF0D2E1B),

            background = Color(0xFFE9F5EA),
            onBackground = Color(0xFF1A1D1B),

            surface = Color(0xFFE0EFE2),
            onSurface = Color(0xFF1A1D1B),
            surfaceVariant = Color(0xFFCFE0D5),
            onSurfaceVariant = Color(0xFF3E4A44),

            outline = Color(0xFF6E7A72),
            outlineVariant = Color(0xFFCAD8CF)
        ), dark = darkColorScheme(
            primary = Color(0xFF99D4B2),
            onPrimary = Color(0xFF0F3723),
            primaryContainer = Color(0xFF1D4A33),
            onPrimaryContainer = Color(0xFFB4E4C8),

            secondary = Color(0xFF92CABA),
            onSecondary = Color(0xFF0C3223),
            secondaryContainer = Color(0xFF124A36),
            onSecondaryContainer = Color(0xFFAEDDCB),

            tertiary = Color(0xFFD2EEDC),
            onTertiary = Color(0xFF173524),
            tertiaryContainer = Color(0xFF2E4C38),
            onTertiaryContainer = Color(0xFFCFF1DB),

            background = Color(0xFF1A1B1D),
            onBackground = Color(0xFFE2E6E3),

            surface = Color(0xFF131414),
            onSurface = Color(0xFFE2E6E3),
            surfaceVariant = Color(0xFF3F4A44),
            onSurfaceVariant = Color(0xFFCFE0D5),

            outline = Color(0xFF86948B),
            outlineVariant = Color(0xFF3F4A44)
        )
    )
    val Ocean = ThemePreset(
        "Ocean",
        light = lightColorScheme(
            primary = Color(0xFF155E75),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFAEE9FF),
            onPrimaryContainer = Color(0xFF001F28),

            secondary = Color(0xFF0E7490),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFB8ECFF),
            onSecondaryContainer = Color(0xFF001F28),

            tertiary = Color(0xFF88D4E3),
            onTertiary = Color(0xFF00363D),
            tertiaryContainer = Color(0xFFC1F0F7),
            onTertiaryContainer = Color(0xFF002024),

            background = Color(0xFFE0F7FB),
            onBackground = Color(0xFF182022),

            surface = Color(0xFFD7EFF3),
            onSurface = Color(0xFF182022),
            surfaceVariant = Color(0xFFC3E3EB),
            onSurfaceVariant = Color(0xFF3D5055),

            outline = Color(0xFF6B8086),
            outlineVariant = Color(0xFFC3E3EB)
        ),
        dark = darkColorScheme(
            primary = Color(0xFF8AD0E6),
            onPrimary = Color(0xFF003640),
            primaryContainer = Color(0xFF004E61),
            onPrimaryContainer = Color(0xFFAEE9FF),

            secondary = Color(0xFF81D3E6),
            onSecondary = Color(0xFF003640),
            secondaryContainer = Color(0xFF004E61),
            onSecondaryContainer = Color(0xFFB8ECFF),

            tertiary = Color(0xFFB5EFF7),
            onTertiary = Color(0xFF00363D),
            tertiaryContainer = Color(0xFF1F4A50),
            onTertiaryContainer = Color(0xFFC1F0F7),

            background = Color(0xFF1A1C1D),
            onBackground = Color(0xFFE2E6E7),

            surface = Color(0xFF131415),
            onSurface = Color(0xFFE2E6E7),
            surfaceVariant = Color(0xFF3E4F54),
            onSurfaceVariant = Color(0xFFC3E3EB),

            outline = Color(0xFF7E989D),
            outlineVariant = Color(0xFF3E4F54)
        )
    )
    val Sunset = ThemePreset(
        "Sunset",
        light = lightColorScheme(
            primary = Color(0xFFDA4932),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDAD3),
            onPrimaryContainer = Color(0xFF3B0800),

            secondary = Color(0xFFF16A51),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFDBD2),
            onSecondaryContainer = Color(0xFF3F0C01),

            tertiary = Color(0xFFFFA892),
            onTertiary = Color(0xFF4A0E00),
            tertiaryContainer = Color(0xFFFFDBCF),
            onTertiaryContainer = Color(0xFF3B0A00),

            background = Color(0xFFFFF1EC),
            onBackground = Color(0xFF1F1A18),

            surface = Color(0xFFFDE9E3),
            onSurface = Color(0xFF1F1A18),
            surfaceVariant = Color(0xFFF0D4CD),
            onSurfaceVariant = Color(0xFF50423E),

            outline = Color(0xFF816D68),
            outlineVariant = Color(0xFFE4C5BE)
        ),
        dark = darkColorScheme(
            primary = Color(0xFFFFB4A8),
            onPrimary = Color(0xFF621103),
            primaryContainer = Color(0xFF8B2818),
            onPrimaryContainer = Color(0xFFFFDAD3),

            secondary = Color(0xFFFFB5A3),
            onSecondary = Color(0xFF5A1604),
            secondaryContainer = Color(0xFF7A2C19),
            onSecondaryContainer = Color(0xFFFFDBD2),

            tertiary = Color(0xFFFFB8A7),
            onTertiary = Color(0xFF561400),
            tertiaryContainer = Color(0xFF77301A),
            onTertiaryContainer = Color(0xFFFFDBCF),

            background = Color(0xFF1C1B1B),
            onBackground = Color(0xFFEDE0DD),

            surface = Color(0xFF151313),
            onSurface = Color(0xFFEDE0DD),
            surfaceVariant = Color(0xFF52433F),
            onSurfaceVariant = Color(0xFFE4C5BE),

            outline = Color(0xFF9C857F),
            outlineVariant = Color(0xFF52433F)
        )
    )

    val All = listOf(Peach, Lavender, Classic, Forest, Ocean, Sunset)
}
