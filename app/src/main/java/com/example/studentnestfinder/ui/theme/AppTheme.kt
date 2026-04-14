package com.example.studentnestfinder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Gaborone Edition Palette ──────────────────────────────────────────────────

/** Primary accent – Gaborone Clay. Buttons, active states, key accents. */
val PrimaryColor = Color(0xFFB84A2D)

/**
 * Material3 secondary slot – Mokolodi Green.
 * FAB, success states, landlord verification badge, category chips.
 */
val AppSecondaryColor = Color(0xFF2F6B4A)

/** Tertiary / highlight – Savanna Gold. Price tags, "new" badges, rating stars. */
val AccentColor = Color(0xFFD49A2A)

/** Soft Kalahari Sand – main background. */
val BackgroundLightColor = Color(0xFFF9F6F0)

/** Ivory White – cards, sheets, dialogs. */
val SurfaceLightColor = Color(0xFFFFFFFF)

/**
 * Kept as the scaffold / top-bar background alias (= Kalahari Sand).
 * Screens reference this variable directly for container colours.
 */
val SecondaryColor = Color(0xFFF9F6F0)

/** Charcoal Dusk – primary text. */
val NeutralColor = Color(0xFF2E2E2E)

/** Charcoal Dusk – primary text (alias for NeutralColor). */
val TextPrimaryColor = Color(0xFF2E2E2E)

/** Stone Gray – secondary text, captions, icons. */
val TextSecondaryColor = Color(0xFF6B5E54)

/** Dusty Border – subtle borders and separators. */
val BorderLightColor = Color(0xFFE2DCD5)

/** Warm sand tint used for input/chip surfaces in the light theme. */
val SurfaceVariantColor = Color(0xFFF0EBE5)

/** Mokolodi Green – success / available states. */
val SuccessColor = Color(0xFF2F6B4A)

/** Savanna Gold – warning states. */
val WarningColor = Color(0xFFD49A2A)

/** Acacia Thorn Red – alerts and error messages. */
val ErrorColor = Color(0xFFC13C3C)

// ── Typography ────────────────────────────────────────────────────────────────
// SF Pro is Apple's proprietary typeface and cannot be bundled in an Android app.
// We use the system sans-serif stack (Roboto / device default) as the closest
// practical alternative, structured so SF Pro font files can be wired in later:
//   1. Add ttf/otf files to res/font/ (e.g. sf_pro_display_regular.ttf, …).
//   2. Declare a FontFamily referencing those files.
//   3. Replace `FontFamily.Default` below with that custom FontFamily.
private val AppFontFamily = FontFamily.Default   // replace with SF Pro FontFamily when available

val AppTypography = Typography(
    displayLarge   = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold,     fontSize = 57.sp, lineHeight = 64.sp,  letterSpacing = (-0.25).sp),
    displayMedium  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold,     fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall   = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Bold,     fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 24.sp,  letterSpacing = 0.15.sp),
    titleSmall     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp,  letterSpacing = 0.1.sp),
    bodyLarge      = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp,  letterSpacing = 0.5.sp),
    bodyMedium     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp,  letterSpacing = 0.25.sp),
    bodySmall      = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp,  letterSpacing = 0.4.sp),
    labelLarge     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp,  letterSpacing = 0.1.sp),
    labelMedium    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp,  letterSpacing = 0.5.sp),
    labelSmall     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp,  letterSpacing = 0.5.sp),
)

// ── Light colour scheme ───────────────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary          = PrimaryColor,
    onPrimary        = Color.White,
    secondary        = AppSecondaryColor,
    onSecondary      = Color.White,
    tertiary         = AccentColor,
    onTertiary       = Color.White,
    background       = BackgroundLightColor,
    onBackground     = TextPrimaryColor,
    surface          = SurfaceLightColor,
    onSurface        = TextPrimaryColor,
    surfaceVariant   = SurfaceVariantColor,
    onSurfaceVariant = TextSecondaryColor,
    error            = ErrorColor,
    onError          = Color.White,
    outline          = BorderLightColor,
)

// ── Dark colour scheme ────────────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary          = Color(0xFFE8896E),   // lighter clay on dark background
    onPrimary        = Color(0xFF5C1900),
    secondary        = Color(0xFF74B896),   // lighter Mokolodi Green
    onSecondary      = Color(0xFF003920),
    tertiary         = Color(0xFFE8BC6A),   // lighter Savanna Gold
    onTertiary       = Color(0xFF3A2600),
    background       = Color(0xFF1C1512),   // very dark warm brown
    onBackground     = Color(0xFFEDE0D4),
    surface          = Color(0xFF2A2319),   // dark card surface
    onSurface        = Color(0xFFEDE0D4),
    surfaceVariant   = Color(0xFF3D3228),
    onSurfaceVariant = Color(0xFFCDB9AB),
    error            = Color(0xFFFFB4A9),
    onError          = Color(0xFF680003),
    outline          = Color(0xFF826558),
)

// ── Theme entry-point ─────────────────────────────────────────────────────────
@Composable
fun StudentNestTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = AppTypography,
        content     = content
    )
}
