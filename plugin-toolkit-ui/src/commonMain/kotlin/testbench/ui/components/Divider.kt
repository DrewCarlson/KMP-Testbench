package testbench.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import testbench.ui.TestbenchTheme

internal enum class Orientation {
    VERTICAL,
    HORIZONTAL,
}

@Composable
internal fun Divider(
    orientation: Orientation,
    modifier: Modifier = Modifier,
) {
    val color = TestbenchTheme.colors.onSurface
    Box(
        modifier = modifier
            .defaultMinSize(1.dp, 1.dp)
            .run {
                when (orientation) {
                    Orientation.VERTICAL -> fillMaxHeight()
                    Orientation.HORIZONTAL -> fillMaxWidth()
                }
            }.drawBehind {
                when (orientation) {
                    Orientation.VERTICAL -> {
                        drawLine(
                            color = color,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 0.5f,
                        )
                    }

                    Orientation.HORIZONTAL -> {
                        val y = size.height / 2
                        drawLine(
                            color = color,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 0.5f,
                        )
                    }
                }
            },
    )
}
