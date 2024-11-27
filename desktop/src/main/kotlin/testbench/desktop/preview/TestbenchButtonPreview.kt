package testbench.desktop.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import testbench.ui.TestbenchTheme
import testbench.ui.components.ButtonType
import testbench.ui.testbench

@Preview
@Composable
private fun TestbenchButtonPreview() {
    TestbenchTheme {
        testbench.Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ButtonType.entries.forEach { type ->
                    testbench.Button(
                        onClick = {},
                        buttonType = type,
                    ) {
                        testbench.Text(type.name)
                    }
                }
            }
        }
    }
}
