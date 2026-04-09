package com.example.studentnestfinder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF0066FF)
val SecondaryColor = Color(0xFFF8F9FA)
val AccentColor = Color(0xFFE63946)
val NeutralColor = Color(0xFF212529)
val BackgroundLightColor = Color(0xFFFFFFFF)
val SurfaceLightColor = Color(0xFFF8F9FA)
val BorderLightColor = Color(0xFFE9ECEF)
val TextPrimaryColor = Color(0xFF212529)
val TextSecondaryColor = Color(0xFF6C757D)
val SuccessColor = Color(0xFF28A745)
val WarningColor = Color(0xFFFFC107)
val ErrorColor = Color(0xFFE63946)

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = AccentColor,
    background = BackgroundLightColor,
    surface = SurfaceLightColor,
    onPrimary = Color.White,
    onSecondary = TextPrimaryColor,
    onTertiary = Color.White,
    onBackground = TextPrimaryColor,
    onSurface = TextPrimaryColor,
    error = ErrorColor,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = AccentColor,
    background = NeutralColor,
    surface = Color(0xFF2A2E33),
    onPrimary = Color.White,
    onSecondary = TextPrimaryColor,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = ErrorColor,
    onError = Color.White
)

@Composable
fun StudentNestTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
