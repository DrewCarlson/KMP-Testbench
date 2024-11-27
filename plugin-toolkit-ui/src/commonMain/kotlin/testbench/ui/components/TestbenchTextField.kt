package testbench.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import testbench.ui.LocalTestbenchColors
import testbench.ui.LocalTestbenchTextStyles

@Composable
internal fun TestbenchTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = LocalTestbenchTextStyles.current.default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val colors = LocalTestbenchColors.current
    var isFocused by remember { mutableStateOf(false) }
    val cursorBrush = remember(colors, isFocused) {
        if (isFocused) {
            SolidColor(colors.primary)
        } else {
            SolidColor(colors.background)
        }
    }
    BasicTextField(
        state = state,
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = 1.dp,
                color = if (isFocused) {
                    colors.primary
                } else {
                    colors.background.copy(
                        alpha = if (enabled) 1f else 0.6f
                    )
                },
                shape = RoundedCornerShape(4.dp)
            ),
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = inputTransformation,
        textStyle = textStyle.copy(
            color = colors.onSurface
        ),
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        outputTransformation = outputTransformation,
        scrollState = scrollState,
        decorator = { innerTextField ->
            Box(modifier = Modifier.padding(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leadingIcon?.run {
                        Box(modifier = Modifier.size(10.dp)) {
                            invoke()
                        }
                    }
                    Box {
                        innerTextField()
                        if (state.text.isEmpty() && !isFocused) {
                            CompositionLocalProvider(
                                LocalTestbenchColors provides colors.copy(
                                    onSurface = colors.background.copy(
                                        alpha = if (enabled) 1f else 0.6f
                                    )
                                ),
                            ) {
                                placeholder?.invoke()
                            }
                        }
                    }
                    trailingIcon?.run {
                        Box(modifier = Modifier.size(10.dp)) {
                            invoke()
                        }
                    }
                }
            }
        },
    )
}


