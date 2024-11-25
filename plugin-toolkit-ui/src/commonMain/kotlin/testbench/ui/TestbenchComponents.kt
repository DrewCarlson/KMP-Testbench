package testbench.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

public val testbench: TestbenchComponents = TestbenchComponents()

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
        color: Color = TestbenchTheme.colors.onPrimary,
        onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    ) {
        BasicText(
            text = text,
            style = style,
            minLines = minLines,
            maxLines = maxLines,
            modifier = modifier,
            softWrap = softWrap,
            overflow = overflow,
            color = { color },
            onTextLayout = onTextLayout
        )
    }
}

