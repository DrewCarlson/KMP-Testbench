package testbench.plugin.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public typealias UiHook = @Composable (modifier: Modifier) -> Unit
