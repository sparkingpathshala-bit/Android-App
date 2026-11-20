package com.sparkingpathshala.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Brush

// Ultra-Modern 2026/2027 Sparking Pathshala Theme Palette (Royal Electric Blue, Clean Slate & Vivid Amber)
val ModernCanvasLight = Color(0xFFF8FAFC) // Crisp Snow Slate
val ModernSurfaceLight = Color(0xFFFFFFFF) // Pure White Card Surface
val ModernSurfaceSubtleLight = Color(0xFFF1F5F9) // Muted Container Fill
val ModernBorderLight = Color(0xFFE2E8F0) // Sleek 1px Hairline Border
val ModernBorderFocusLight = Color(0xFF2563EB) // Vibrant Focus Ring

val ModernSapphire = Color(0xFF2563EB) // Vivid Royal Blue
val ModernDeepIndigo = Color(0xFF1E3A8A) // Deep Oxford Navy
val ModernSoftBlue = Color(0xFFEFF6FF) // Crisp Ice-Blue Tint

val ModernEmerald = Color(0xFF10B981) // Motivational Achievement Green
val ModernEmeraldBg = Color(0xFFECFDF5)
val ModernAmber = Color(0xFFF59E0B) // Study Streak & Milestone Gold
val ModernAmberBg = Color(0xFFFEF3C7)
val ModernRose = Color(0xFFEF4444) // Urgent Alert Red
val ModernRoseBg = Color(0xFFFEF2F2)
val ModernViolet = Color(0xFF6366F1) // Secondary Academic Focus
val ModernVioletBg = Color(0xFFEEF2FF)

// Dark Mode Tokens (Deep Space Obsidian)
val DarkCanvas = Color(0xFF090D16)
val DarkAppBg = Color(0xFF0F172A)
val DarkCardBg = Color(0xFF111827)
val DarkCardSubtle = Color(0xFF1E293B)
val DarkBorder = Color(0xFF1F2937)
val DarkBorderFocus = Color(0xFF3B82F6)

// Compatibility Mappings
val WarmPaper = ModernCanvasLight
val CanvasBg = ModernSurfaceSubtleLight
val CardBgLight = ModernSurfaceLight
val CardSubtleLight = ModernSurfaceSubtleLight
val BorderLight = ModernBorderLight
val BorderFocusLight = ModernBorderFocusLight
val DeepIndigo = ModernDeepIndigo
val SoftIndigo = ModernSoftBlue
val TactileAmber = ModernAmber
val TactileAmberBg = ModernAmberBg
val TactileGreen = ModernEmerald
val TactileGreenBg = ModernEmeraldBg
val TactileRed = ModernRose
val TactileRedBg = ModernRoseBg

val RoyalIndigo = ModernDeepIndigo
val SapphireBlue = ModernSapphire
val DeepNavy = Color(0xFF0F172A)
val ElectricViolet = ModernViolet
val ElectricCyan = Color(0xFF06B6D4)
val SoftLavender = ModernVioletBg
val MoltenAmber = ModernAmber
val RadiantGold = ModernAmber
val SunsetCoral = ModernRose
val EmeraldGreen = ModernEmerald

val VibrantRed = ModernRose
val AccentRed = ModernRose
val RoyalPurple = ModernViolet
val ElectricIndigo = ModernSapphire
val PrimaryMaroon = Color(0xFF1E3A8A)
val PrimaryBlue = Color(0xFF2563EB) // High-Energy Royal Blue
val SecondaryGold = ModernAmber
val AccentGold = ModernAmber

// Base Backgrounds & Surfaces
val LightBg = ModernCanvasLight
val DarkBg = DarkAppBg
val LightSurface = ModernSurfaceLight
val DarkSurface = DarkCardBg

// Modern Electric Gradients
val CosmicGradientLight = Brush.verticalGradient(
    colors = listOf(ModernCanvasLight, Color(0xFFEFF6FF))
)
val CosmicGradientDark = Brush.verticalGradient(
    colors = listOf(DarkCanvas, DarkAppBg)
)
val BrandCrimson = Color(0xFF881337)
val BrandRuby = Color(0xFF9F1239)
val FlameGold = Color(0xFFF59E0B)

val BrandGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF881337), Color(0xFF9F1239), Color(0xFFBE123C))
)
val HeaderGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF881337), Color(0xFF9F1239))
)
val GoldGlowGradient = Brush.horizontalGradient(
    colors = listOf(ModernAmber, Color(0xFFFBBF24))
)
val CardGlow = Brush.radialGradient(
    colors = listOf(ModernSapphire.copy(alpha = 0.05f), Color.Transparent)
)

// Semantic colors
val SuccessGreen = ModernEmerald
val ErrorRed = ModernRose
val InfoBlue = Color(0xFF2563EB)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFFF59E0B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = ModernEmerald,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1),
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF022C22),
    background = Color(0xFF090D16),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF1F2937),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

val LocalThemeIsDark = androidx.compose.runtime.compositionLocalOf { false }

// Comprehensive Material 3 Typography Scale with System SansSerif
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

val Shapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun SparkingPathshalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalThemeIsDark provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
