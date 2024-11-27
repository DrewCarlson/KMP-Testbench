package testbench.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import testbench.ui.LocalTestbenchColors
import testbench.ui.thenIf


internal enum class Orientation {
    VERTICAL, HORIZONTAL;
}

@Composable
internal fun Divider(
    orientation: Orientation,
    modifier: Modifier = Modifier,
) {
    val color = LocalTestbenchColors.current.onSurface
    Box(
        modifier = modifier
            .thenIf(orientation == Orientation.VERTICAL) { fillMaxHeight() }
            .thenIf(orientation == Orientation.HORIZONTAL) { fillMaxWidth() }
            .drawBehind {
                drawLine(
                    color = color,
                    start = Offset.Zero,
                    end = Offset(size.width, size.height),
                    strokeWidth = 4f
                )
            },
    )
}
