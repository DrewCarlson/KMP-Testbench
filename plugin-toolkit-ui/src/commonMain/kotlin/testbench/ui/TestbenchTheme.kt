package testbench.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalTestbenchColors = staticCompositionLocalOf<TestbenchColors> { error("TestbenchTheme not configured") }
val LocalTestbenchTextStyles = staticCompositionLocalOf<TestbenchTextStyles> { error("TestbenchTheme not configured") }

@Composable
fun TestbenchTheme(
    enableDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = remember(enableDarkMode) {
        if (enableDarkMode) TestbenchDarkColors else TestbenchLightColors
    }
    val textStyles = remember { TestbenchTextStyles() }
    CompositionLocalProvider(
        LocalTestbenchColors provides colors,
        LocalTestbenchTextStyles provides textStyles,
        content = content
    )
}

object TestbenchTheme {
    val colors: TestbenchColors
        @Composable
        get() = LocalTestbenchColors.current

    val textStyles: TestbenchTextStyles
        @Composable
        get() = LocalTestbenchTextStyles.current
}

data class TestbenchColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val success: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color,
)

private val TestbenchDarkColors = TestbenchColors(
    primary = ColorPalette.geekblue9,
    secondary = ColorPalette.blue4,
    accent = ColorPalette.blue6,
    background = ColorPalette.backgroundDark,
    surface = Color.LightGray, //
    error = ColorPalette.red5,
    success = ColorPalette.green5,
    onPrimary = ColorPalette.textDark,
    onSecondary = ColorPalette.backgroundDark,
    onBackground = ColorPalette.backgroundDark,
    onSurface = Color.Black, //
    onError = ColorPalette.textDark,
)

private val TestbenchLightColors = TestbenchColors(
    primary = ColorPalette.blue3,
    secondary = ColorPalette.blue8,
    accent = ColorPalette.blue6,
    background = ColorPalette.background,
    surface = Color.LightGray, //
    success = ColorPalette.green7,
    error = ColorPalette.red7,
    onPrimary = ColorPalette.text,
    onSecondary = ColorPalette.background,
    onBackground = ColorPalette.background,
    onSurface = Color.White, //
    onError = ColorPalette.text
)
