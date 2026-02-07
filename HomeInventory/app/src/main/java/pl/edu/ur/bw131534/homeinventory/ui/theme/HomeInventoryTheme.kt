package pl.edu.ur.bw131534.homeinventory.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color




val LightPrimary = Color(0xFF006760)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFF7CF4E6)
val LightOnPrimaryContainer = Color(0xFF00201D)

val LightSecondary = Color(0xFF4A6360)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFCDE8E4)
val LightOnSecondaryContainer = Color(0xFF051E1C)

val LightTertiary = Color(0xFF4A627B)
val LightSurface = Color(0xFFF9FAFA)
val LightError = Color(0xFFBA1A1A)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    background = LightBackground,
    surface = LightSurface,
    error = LightError
)


val DarkPrimary = Color(0xFF5EDAD2)
val DarkOnPrimary = Color(0xFF003733)
val DarkPrimaryContainer = Color(0xFF005049)
val DarkOnPrimaryContainer = Color(0xFF7CF4E6)


val DarkSecondary = Color(0xFFB1CCC6)
val DarkOnSecondary = Color(0xFF1B3532)
val DarkSecondaryContainer = Color(0xFF324B49)
val DarkOnSecondaryContainer = Color(0xFFCDE8E4)

val DarkTertiary = Color(0xFFADC7E5)
val DarkBackground = Color(0xFF191C1C)
val DarkSurface = Color(0xFF191C1C)
val DarkError = Color(0xFFFFB4AB)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    error = DarkError
)