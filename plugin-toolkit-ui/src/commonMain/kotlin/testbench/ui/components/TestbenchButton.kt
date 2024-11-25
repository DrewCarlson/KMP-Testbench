package testbench.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import testbench.ui.LocalTestbenchColors
import testbench.ui.TestbenchTheme

public enum class ButtonType {
    PRIMARY,
    SECONDARY,
    ACCENT,
    STANDARD,
}

public enum class ButtonStyle {
    FILL,
    OUTLINE,
    TEXT,
}

@Composable
internal fun TestbenchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonType: ButtonType,
    buttonStyle: ButtonStyle,
    content: @Composable () -> Unit,
) {
    // var isFocused by remember { mutableStateOf(false) }
    val mainBackgroundColor = when (buttonType) {
        ButtonType.PRIMARY -> TestbenchTheme.colors.primary
        ButtonType.SECONDARY -> TestbenchTheme.colors.secondary
        ButtonType.ACCENT -> TestbenchTheme.colors.accent
        ButtonType.STANDARD -> TestbenchTheme.colors.background
    }
    val textColor = when (buttonStyle) {
        ButtonStyle.TEXT -> when (buttonType) {
            ButtonType.PRIMARY -> TestbenchTheme.colors.primary
            ButtonType.SECONDARY -> TestbenchTheme.colors.secondary
            ButtonType.ACCENT -> TestbenchTheme.colors.accent
            ButtonType.STANDARD -> TestbenchTheme.colors.onSurface
        }

        ButtonStyle.FILL,
        ButtonStyle.OUTLINE,
        -> when (buttonType) {
            ButtonType.PRIMARY -> TestbenchTheme.colors.onPrimary
            ButtonType.SECONDARY -> TestbenchTheme.colors.onSecondary
            ButtonType.ACCENT -> TestbenchTheme.colors.onSecondary
            ButtonType.STANDARD -> TestbenchTheme.colors.onBackground
        }
    }
    val backgroundColor by animateColorAsState(
        when {
            enabled -> mainBackgroundColor
            else -> TestbenchTheme.colors.onSurface
        },
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .run {
                when (buttonStyle) {
                    ButtonStyle.FILL -> background(backgroundColor)
                    ButtonStyle.OUTLINE -> border(1.dp, backgroundColor, RoundedCornerShape(6.dp))
                    ButtonStyle.TEXT -> this
                }
            }.clickable(onClick = onClick, role = Role.Button)
            .padding(vertical = 6.dp, horizontal = 8.dp),
    ) {
        CompositionLocalProvider(
            LocalTestbenchColors provides TestbenchTheme.colors.copy(
                onSurface = textColor,
            ),
            content = content,
        )
    }
}
