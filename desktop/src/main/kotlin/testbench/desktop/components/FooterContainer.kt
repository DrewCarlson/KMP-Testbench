package testbench.desktop.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
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
