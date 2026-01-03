package com.hisaabmate.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

enum class ThemeType {
    MINIMALIST,
    PLAYFUL
}

private val PlayfulColorScheme = lightColorScheme(
    primary = PlayfulPrimary,
    secondary = PlayfulSecondary,
    background = PlayfulBackground,
    surface = PlayfulSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PlayfulTextMain,
    onSurface = PlayfulTextMain
)

private val PlayfulShapes = Shapes(
    small = RoundedCornerShape(24.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(24.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    background = BackgroundDark,
    surface = CardDark,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = BackgroundLight,
    surface = CardLight,
    onPrimary = Color.White,
    onBackground = TextMainLight,
    onSurface = TextMainLight
)

@Composable
fun HisaabMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeType: ThemeType = ThemeType.MINIMALIST,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to enforce our custom palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeType == ThemeType.PLAYFUL -> PlayfulColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = if (themeType == ThemeType.PLAYFUL) PlayfulShapes else MaterialTheme.shapes,
        typography = Typography,
        content = content
    )
}
