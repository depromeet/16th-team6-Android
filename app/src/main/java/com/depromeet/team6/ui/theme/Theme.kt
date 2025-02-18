package com.depromeet.team6.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = defaultTeam6Colors.white,
    secondary = defaultTeam6Colors.greySecondaryLabel,
    tertiary = defaultTeam6Colors.greyTertiaryLabel
)

private val LightColorScheme = lightColorScheme(
    primary = defaultTeam6Colors.white,
    secondary = defaultTeam6Colors.greySecondaryLabel,
    tertiary = defaultTeam6Colors.greyTertiaryLabel

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

object Team6Theme {
    val colors: Team6Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalTeam6Colors.current

    val typography: Team6Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTeam6Typography.current
}

@Composable
fun ProvideTeam6ColorsAndTypography(
    colors: Team6Colors,
    typography: Team6Typography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalTeam6Colors provides colors,
        LocalTeam6Typography provides typography,
        content = content
    )
}

@Composable
fun Team6Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    ProvideTeam6ColorsAndTypography(
        colors = defaultTeam6Colors,
        typography = defaultTeam6Typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
