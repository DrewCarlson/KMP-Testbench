package testbench.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

public val LocalTestbenchColors: ProvidableCompositionLocal<TestbenchColors> =
    compositionLocalOf { error("TestbenchTheme not configured") }
public val LocalTestbenchTextStyles: ProvidableCompositionLocal<TestbenchTextStyles> =
    compositionLocalOf { error("TestbenchTheme not configured") }

@Composable
public fun TestbenchTheme(
    enableDarkMode: Boolean = true,
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

public object TestbenchTheme {
    public val colors: TestbenchColors
        @Composable
        get() = LocalTestbenchColors.current

    public val textStyles: TestbenchTextStyles
        @Composable
        get() = LocalTestbenchTextStyles.current
}

public data class TestbenchColors(
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
    primary = ColorPalette.geekblue7,
    secondary = ColorPalette.blue5,
    accent = ColorPalette.blue6,
    background = ColorPalette.gray3,
    surface = ColorPalette.gray2,
    error = ColorPalette.red5,
    success = ColorPalette.green5,
    onPrimary = ColorPalette.textDark,
    onSecondary = ColorPalette.textDark,
    onBackground = ColorPalette.textDark,
    onSurface = ColorPalette.gray7,
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
