package testbench.plugins.compose

import testbench.plugin.BenchPlugin

public abstract class ComposePlugin : BenchPlugin<Unit, Unit> {
    override val id: String = "compose-debug"

    override val name: String = "Compose Debug"
}
