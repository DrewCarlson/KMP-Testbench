package testbench.desktop.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import testbench.ui.TestbenchIcon
import testbench.ui.TestbenchTheme
import testbench.ui.testbench


@Preview
@Composable
private fun PreviewTextField() {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    TestbenchTheme {
        testbench.Surface {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                testbench.TextField(
                    state = TextFieldState("icon (value)"),
                    placeholder = { testbench.Text("Placeholder ...") },
                    leadingIcon = { testbench.Icon(TestbenchIcon.SEARCH) },
                )
                testbench.TextField(
                    state = TextFieldState(),
                    placeholder = { testbench.Text("icon (placeholder)") },
                    leadingIcon = { testbench.Icon(TestbenchIcon.SEARCH) },
                )
                testbench.TextField(
                    state = TextFieldState(),
                    modifier = Modifier.focusRequester(focusRequester),
                )
                testbench.TextField(
                    state = TextFieldState(),
                    placeholder = { testbench.Text("Placeholder ...") }
                )
                testbench.TextField(
                    state = TextFieldState("typed text"),
                )
                testbench.TextField(
                    state = TextFieldState("typed text with placeholder"),
                    placeholder = { testbench.Text("Placeholder ...") },
                )
                testbench.TextField(
                    state = TextFieldState("disabled (value)"),
                    enabled = false,
                )
                testbench.TextField(
                    state = TextFieldState(),
                    placeholder = { testbench.Text("disabled (placeholder)") },
                )
            }
        }
    }
}
