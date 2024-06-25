package testbench.plugin.server

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import testbench.plugin.BenchPlugin

public interface ServerPlugin : BenchPlugin {

    public fun handleMessage(message: String)

    @Composable
    public fun renderPanel(modifier: Modifier)
}