package mx.utng.smarthealthmonitor.maai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = SHPrimary,
    onPrimary        = SHOnPrimary,
    primaryContainer = SHPrimaryContainer,
    secondary        = SHSecondary,
    error            = SHError,
    background       = SHBackground,
    surface          = SHSurface,
    onSurface        = SHOnSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary    = SHPrimaryDark,
    onPrimary  = SHOnPrimaryDark,
    background = SHBackgroundDark,
    surface    = SHSurfaceDark,
)

@Composable
fun SmartHealthMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = androidx.compose.material3.Typography(),
        content     = content
    )
}