package testbench.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import testbench.ui.TestbenchTheme
import testbench.ui.thenIf
import kotlin.math.roundToInt

@Composable
internal fun TestbenchDropdown(
    menu: @Composable (close: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    matchContentWidth: Boolean = true,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var menuMinWidth by remember { mutableStateOf(0.dp) }
    var heightOffset by remember { mutableStateOf(0) }
    Box {
        Box(
            modifier = modifier
                .clickable { expanded = enabled && !expanded }
                .onSizeChanged {
                    heightOffset = it.height
                    menuMinWidth = with(density) { it.width.toDp() }
                },
            content = { content() }
        )
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, heightOffset),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
                content = {
                    Column(
                        modifier = Modifier
                            .border(1.dp, TestbenchTheme.colors.onSurface, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .run {
                                if (matchContentWidth) {
                                    width(menuMinWidth)
                                } else {
                                    defaultMinSize(minWidth = menuMinWidth)
                                }
                            }
                            .shadow(8.dp, shape = RoundedCornerShape(4.dp))
                            .background(TestbenchTheme.colors.surface),
                        content = { menu { expanded = false } }
                    )
                }
            )
        }
    }
}

