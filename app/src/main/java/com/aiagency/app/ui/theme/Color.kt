package com.aiagency.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val OrangeAccent = Color(0xFFFFA500)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val OnDarkBackground = Color(0xFFFFFFFF)

val DarkColorScheme = darkColorScheme(
    primary = OrangeAccent,
    secondary = OrangeAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = OnDarkBackground,
    onSurface = OnDarkBackground
)

val LightColorScheme = lightColorScheme(
    primary = OrangeAccent,
    secondary = OrangeAccent
)
