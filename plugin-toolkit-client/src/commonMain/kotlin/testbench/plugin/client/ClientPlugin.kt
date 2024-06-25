package testbench.plugin.client

import kotlinx.coroutines.flow.Flow
import testbench.plugin.BenchPlugin

public interface ClientPlugin : BenchPlugin {
    public val outgoingMessages: Flow<String>

    public fun handleMessage(message: String)
}
