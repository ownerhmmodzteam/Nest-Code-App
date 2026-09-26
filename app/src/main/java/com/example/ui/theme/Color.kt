package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// iOS 28 Ultra-Smooth Liquid Glass Theme Colors
// ==========================================

// Cosmic OLED & Frosted Glass Surfaces
val IosDarkBackground = Color(0xFF06080F)
val IosDarkSurface = Color(0xFF0D121F)
val IosDarkSurfaceVariant = Color(0xFF161E30)
val IosDarkGlassSurface = Color(0xFF101827) // Translucent acrylic
val IosDarkGlassHighlight = Color(0xFF1A233A)
val IosDarkCard = Color(0xFF151E2F)

// iOS Specular Highlights & Glass Borders
val IosGlassBorderHighlight = Color(0x40FFFFFF)
val IosGlassBorderSubtle = Color(0x12FFFFFF)
val IosGlassBorderGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x4DFFFFFF),
        Color(0x15FFFFFF),
        Color(0x05FFFFFF)
    )
)

// Dynamic Island & Accent Gradients
val IosNeonCyan = Color(0xFF00F5FF)
val IosNeonBlue = Color(0xFF0A84FF)
val IosNeonViolet = Color(0xFFBF5AF2)
val IosNeonPurple = Color(0xFF9333EA)
val IosNeonEmerald = Color(0xFF30D158)
val IosNeonAmber = Color(0xFFFF9F0A)
val IosNeonCoral = Color(0xFFFF375F)
val IosNeonTeal = Color(0xFF64D2FF)

// Text Colors with SF Pro Readability
val IosTextPrimary = Color(0xFFFFFFFF)
val IosTextSecondary = Color(0xFF98A2B3)
val IosTextTertiary = Color(0xFF667085)

// Standard Color Aliases for Compatibility
val CyanPrimaryDark = IosNeonCyan
val CyanPrimaryLight = Color(0xFF0077E6)
val VioletSecondaryDark = IosNeonViolet
val VioletSecondaryLight = Color(0xFF7928CA)
val EmeraldTertiaryDark = IosNeonEmerald
val EmeraldTertiaryLight = Color(0xFF28A745)

val DarkBg = IosDarkBackground
val DarkSurface = IosDarkSurface
val DarkSurfaceVariant = IosDarkSurfaceVariant
val DarkCard = IosDarkCard
val DarkTextPrimary = IosTextPrimary
val DarkTextSecondary = IosTextSecondary

val LightBg = Color(0xFFF2F4F8)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE5E9F0)
val LightCard = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF0B132B)
val LightTextSecondary = Color(0xFF5A6982)

val AccentAmber = IosNeonAmber
val AccentRed = IosNeonCoral
val AccentGreen = IosNeonEmerald
val AccentPurple = IosNeonViolet
val AccentBlue = IosNeonBlue
val AccentTeal = IosNeonTeal
val AccentRose = IosNeonCoral

// Code Editor Surface
val CodeEditorBg = Color(0xFF080C14)
val CodeGutterBg = Color(0xFF0E1420)
val CodeGutterText = Color(0xFF475467)
val CodeKeyword = Color(0xFFFF5252)
val CodeString = Color(0xFF70C0FF)
val CodeFunction = Color(0xFFD68FFF)
val CodeComment = Color(0xFF637381)
val CodeVariable = Color(0xFF79FFE1)
