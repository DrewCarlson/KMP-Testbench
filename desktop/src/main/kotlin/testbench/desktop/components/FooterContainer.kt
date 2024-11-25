package testbench.desktop.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FooterContainer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(24.dp),
    ) {
        //Divider(orientation = Orientation.Horizontal)
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
        }
    }
}
