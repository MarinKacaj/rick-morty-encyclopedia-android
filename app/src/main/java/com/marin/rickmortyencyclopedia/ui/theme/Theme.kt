package com.marin.rickmortyencyclopedia.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

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

@Immutable
data class SpacingSystem(
    val spacerXXSmall: Dp,
    val spacerXSmall: Dp,
    val spacerSmall: Dp,
    val spacerMedium: Dp,
)

@Immutable
data class SizingSystem(
    val sizerXXSmall: Dp,
)

val LocalSpacingSystem = staticCompositionLocalOf {
    SpacingSystem(
        spacerXXSmall = 0.dp,
        spacerXSmall = 0.dp,
        spacerSmall = 0.dp,
        spacerMedium = 0.dp,
    )
}

val LocalSizingSystem = staticCompositionLocalOf {
    SizingSystem(sizerXXSmall = 0.dp)
}

@Composable
fun RickMortyEncyclopediaTheme(
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

    val spacingSystem = SpacingSystem(
        spacerXXSmall = 2.dp,
        spacerXSmall = 4.dp,
        spacerSmall = 8.dp,
        spacerMedium = 16.dp,
    )
    val sizingSystem = SizingSystem(
        sizerXXSmall = 2.dp,
    )

    CompositionLocalProvider(
        LocalSpacingSystem provides spacingSystem,
        LocalSizingSystem provides sizingSystem,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object RickMortyEncyclopediaAppTheme {

    val spacing: SpacingSystem
        @Composable
        get() = LocalSpacingSystem.current

    val sizing: SizingSystem
        @Composable
        get() = LocalSizingSystem.current
}