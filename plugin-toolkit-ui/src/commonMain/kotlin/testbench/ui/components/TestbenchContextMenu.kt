package testbench.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import testbench.ui.TestbenchTheme
import kotlin.math.roundToInt

@Composable
internal fun TestbenchContextMenu(
    menu: @Composable (close: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    body: @Composable () -> Unit,
) {
    var menuOffset by remember { mutableStateOf<IntOffset?>(null) }
    val close = remember { { menuOffset = null } }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == Press && event.buttons.isSecondaryPressed) {
                                val change = event.changes.single()
                                val offset = change.position
                                menuOffset = IntOffset(
                                    x = offset.x.roundToInt(),
                                    y = offset.y.roundToInt(),
                                )
                                change.consume()
                            }
                        }
                    }
                },
        ) {
            body()
        }
        menuOffset?.let { offset ->
            val shape = RoundedCornerShape(6.dp)
            Popup(
                offset = offset,
                properties = PopupProperties(focusable = true),
                onDismissRequest = close,
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(1.dp, TestbenchTheme.colors.onSurface, shape)
                        .clip(shape)
                        .background(TestbenchTheme.colors.surface),
                ) {
                    menu(close)
                }
            }
        }
    }
}
