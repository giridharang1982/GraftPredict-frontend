package com.example.graftpredict.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Primary = Color(0xFF137FEC)
val BackgroundLight = Color(0xFFF6F7F8)
val BackgroundDark = Color(0xFF101922)
val Slate50 = Color(0xFFF8FAFC)    // Tailwind slate-50
val Slate900 = Color(0xFF0F172A)   // Tailwind slate-900
val Slate100 = Color(0xFFF1F5F9)   // Tailwind slate-100
val Slate700 = Color(0xFF334155)   // Tailwind slate-700
val Slate600 = Color(0xFF475569)   // Tailwind slate-600
val Slate500 = Color(0xFF64748B)   // Tailwind slate-500
val Slate400 = Color(0xFF94A3B8)   // Tailwind slate-400
val Slate300 = Color(0xFFCBD5E1)   // Tailwind slate-300
val Slate200 = Color(0xFFE2E8F0)   // Tailwind slate-200
val Slate800 = Color(0xFF1E293B)   // Tailwind slate-800

// Light colors
val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = BackgroundLight,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900
)

// Dark colors
val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = BackgroundDark,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100
)

@Composable
fun GraftpredictTheme(
    darkTheme: Boolean = true, // matching html class="dark"
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            bodyLarge = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
            ),
            // We use a custom "Manrope" look via fontFamily, but here we default to sans-serif.
            // In a real app you could include the Manrope font in resources and set it.
            titleLarge = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            labelSmall = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        ),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(4.dp),    // base rounded
            small = RoundedCornerShape(8.dp),         // lg
            medium = RoundedCornerShape(12.dp)        // xl
        ),
        content = content
    )
}
