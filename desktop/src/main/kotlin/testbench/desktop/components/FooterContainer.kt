package testbench.desktop.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Composable
fun FooterContainer(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Divider(orientation = Orientation.Horizontal)
        Spacer(modifier.height(24.dp))
    }
}