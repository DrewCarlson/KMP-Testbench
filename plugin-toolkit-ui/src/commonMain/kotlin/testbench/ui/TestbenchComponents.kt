package testbench.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import testbench.ui.components.*

public val testbench: TestbenchComponents by lazy { TestbenchComponents() }

public class TestbenchComponents {
    @Composable
    public fun Text(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = TestbenchTheme.textStyles.default,
        minLines: Int = 1,
        maxLines: Int = Int.MAX_VALUE,
        softWrap: Boolean = true,
        overflow: TextOverflow = TextOverflow.Clip,
        color: Color = TestbenchTheme.colors.onSurface,
        onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    ) {
        TestbenchText(
            text = text,
            style = style,
            minLines = minLines,
            maxLines = maxLines,
            modifier = modifier,
            softWrap = softWrap,
            overflow = overflow,
            color = color,
            onTextLayout = onTextLayout,
        )
    }

    @Composable
    public fun Button(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        buttonType: ButtonType = ButtonType.PRIMARY,
        buttonStyle: ButtonStyle = ButtonStyle.FILL,
        content: @Composable () -> Unit,
    ) {
        TestbenchButton(
            onClick = onClick,
            modifier = modifier,
            buttonType = buttonType,
            buttonStyle = buttonStyle,
            content = content,
        )
    }

    @Composable
    public fun OutlineButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        buttonType: ButtonType = ButtonType.PRIMARY,
        content: @Composable () -> Unit,
    ) {
        TestbenchButton(
            onClick = onClick,
            modifier = modifier,
            buttonType = buttonType,
            buttonStyle = ButtonStyle.OUTLINE,
            content = content,
        )
    }

    @Composable
    public fun TextButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        buttonType: ButtonType = ButtonType.PRIMARY,
        content: @Composable () -> Unit,
    ) {
        TestbenchButton(
            onClick = onClick,
            modifier = modifier,
            buttonType = buttonType,
            buttonStyle = ButtonStyle.TEXT,
            content = content,
        )
    }

    @Composable
    public fun Icon(
        icon: TestbenchIcon,
        modifier: Modifier = Modifier,
        tint: Color = TestbenchTheme.colors.onPrimary,
    ) {
        Image(
            painter = painterResource(icon.res),
            contentDescription = null,
            modifier = modifier.defaultMinSize(minWidth = 16.dp, minHeight = 16.dp),
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(tint),
        )
    }

    @Composable
    public fun HorizontalDivider(modifier: Modifier = Modifier) {
        Divider(
            orientation = Orientation.HORIZONTAL,
            modifier = modifier,
        )
    }

    @Composable
    public fun VerticalDivider(modifier: Modifier = Modifier) {
        Divider(
            orientation = Orientation.VERTICAL,
            modifier = modifier,
        )
    }

    @Composable
    public fun TextField(
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
        TestbenchTextField(
            state = state,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            inputTransformation = inputTransformation,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = lineLimits,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            outputTransformation = outputTransformation,
            scrollState = scrollState,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }

    @Composable
    public fun Surface(
        modifier: Modifier = Modifier,
        color: Color = TestbenchTheme.colors.surface,
        content: @Composable () -> Unit,
    ) {
        Box(
            modifier = modifier
                .background(color)
                .padding(8.dp),
            content = { content() },
        )
    }

    @Composable
    public fun Dropdown(
        menu: @Composable (close: () -> Unit) -> Unit,
        modifier: Modifier = Modifier,
        matchContentWidth: Boolean = true,
        enabled: Boolean = true,
        content: @Composable () -> Unit,
    ) {
        TestbenchDropdown(
            menu = menu,
            modifier = modifier,
            matchContentWidth = matchContentWidth,
            enabled = enabled,
            content = content,
        )
    }

    @Composable
    public fun HorizontalSplitLayout(
        first: @Composable () -> Unit,
        second: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        draggableWidth: Dp = 8.dp,
        firstPaneMinWidth: Dp = Dp.Unspecified,
        secondPaneMinWidth: Dp = Dp.Unspecified,
        state: SplitLayoutState = rememberSplitLayoutState(),
    ) {
        TestbenchSplitLayout(
            first = first,
            second = second,
            modifier = modifier,
            draggableWidth = draggableWidth,
            firstPaneMinWidth = firstPaneMinWidth,
            secondPaneMinWidth = secondPaneMinWidth,
            strategy = horizontalTwoPaneStrategy(),
            state = state,
        )
    }

    @Composable
    public fun VerticalSplitLayout(
        first: @Composable () -> Unit,
        second: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        draggableWidth: Dp = 8.dp,
        firstPaneMinWidth: Dp = Dp.Unspecified,
        secondPaneMinWidth: Dp = Dp.Unspecified,
        state: SplitLayoutState = rememberSplitLayoutState(),
    ) {
        TestbenchSplitLayout(
            first = first,
            second = second,
            modifier = modifier,
            draggableWidth = draggableWidth,
            firstPaneMinWidth = firstPaneMinWidth,
            secondPaneMinWidth = secondPaneMinWidth,
            strategy = verticalTwoPaneStrategy(),
            state = state,
        )
    }
}
