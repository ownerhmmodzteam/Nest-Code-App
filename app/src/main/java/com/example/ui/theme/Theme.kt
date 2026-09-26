package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// iOS 28 Shapes - Smooth Continuous Curves (Squircles)
val IosShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = IosNeonCyan,
    onPrimary = Color(0xFF001B24),
    primaryContainer = Color(0xFF003747),
    onPrimaryContainer = Color(0xFFB8F5FF),
    secondary = IosNeonViolet,
    onSecondary = Color(0xFF26053D),
    secondaryContainer = Color(0xFF45186B),
    onSecondaryContainer = Color(0xFFF3D5FF),
    tertiary = IosNeonEmerald,
    onTertiary = Color(0xFF00391A),
    tertiaryContainer = Color(0xFF005329),
    onTertiaryContainer = Color(0xFF91F8BD),
    background = IosDarkBackground,
    onBackground = IosTextPrimary,
    surface = IosDarkSurface,
    onSurface = IosTextPrimary,
    surfaceVariant = IosDarkSurfaceVariant,
    onSurfaceVariant = IosTextSecondary,
    outline = Color(0xFF2A344A),
    outlineVariant = Color(0xFF1E2638),
    error = IosNeonCoral,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyanPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F3FF),
    onPrimaryContainer = Color(0xFF002A36),
    secondary = VioletSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFE2FF),
    onSecondaryContainer = Color(0xFF2A0057),
    tertiary = EmeraldTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD0F8E1),
    onTertiaryContainer = Color(0xFF003918),
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = IosNeonCoral,
    onError = Color.White
)

@Composable
fun CodeNestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = IosShapes,
        content = content
    )
}
