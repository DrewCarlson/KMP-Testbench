package testbench.desktop.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import testbench.ui.TestbenchTheme
import testbench.ui.testbench

@Composable
fun WelcomePanel(modifier: Modifier = Modifier) {
    // TODO: Better default welcome screen
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        testbench.Text(
            text = "Welcome to KMP Testbench!",
            style = TestbenchTheme.textStyles.h1,
        )
    }
}

@Composable
private fun Container(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = TestbenchTheme.colors.onSurface,
                shape = RoundedCornerShape(4.dp),
            ).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}
