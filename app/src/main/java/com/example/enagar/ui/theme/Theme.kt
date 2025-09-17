package com.example.enagar.ui.theme

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

// 🎨 Formal Government Style Palette
val BrownPrimary = Color(0xFF6D4C41)   // Deep Brown
val BrownSecondary = Color(0xFF8D6E63) // Soft Brown
val CreamBackground = Color(0xFFFFF8E1) // Light Cream
val SurfaceLight = Color(0xFFFFFFFF)    // White surface
val OnPrimaryText = Color.White
val OnBackgroundText = Color(0xFF3E2723) // Darker Brown text

// 🌙 Dark Mode Palette
private val DarkColorScheme = darkColorScheme(
    primary = BrownPrimary,
    secondary = BrownSecondary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = OnPrimaryText,
    onSecondary = OnPrimaryText,
    onBackground = Color(0xFFEDEDED),
    onSurface = Color(0xFFEDEDED)
)

// ☀️ Light Mode Palette
private val LightColorScheme = lightColorScheme(
    primary = BrownPrimary,
    secondary = BrownSecondary,
    background = CreamBackground,
    surface = SurfaceLight,
    onPrimary = OnPrimaryText,
    onSecondary = OnPrimaryText,
    onBackground = OnBackgroundText,
    onSurface = OnBackgroundText
)

@Composable
fun ENagarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // ❌ turned OFF for consistency
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
        content = content
    )
}
