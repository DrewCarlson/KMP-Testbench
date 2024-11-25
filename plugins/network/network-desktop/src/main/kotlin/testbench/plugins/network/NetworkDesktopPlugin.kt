package testbench.plugins.network

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks
import testbench.plugins.network.ui.NetworkMainPanel

public data class NetworkEntryHolder(
    val request: NetworkRequestMessage,
    val response: NetworkResponseMessage? = null,
)

public class NetworkDesktopPlugin :
    NetworkPlugin(),
    DesktopPlugin<NetworkPluginMessage, Unit> {
    private val networkEntries = MutableStateFlow(emptyMap<String, NetworkEntryHolder>())

    override val ui: UiHooks = UiHooks {
        MainPanel { modifier ->
            val entries by networkEntries.collectAsState(initial = emptyMap())
            NetworkMainPanel(
                entries = entries,
                onClearEntries = { networkEntries.update { emptyMap() } },
                modifier = modifier,
            )
        }
    }

    override suspend fun handleMessage(message: NetworkPluginMessage) {
        when (message) {
            is NetworkRequestMessage -> {
                networkEntries.update {
                    mapOf(Pair(message.id, NetworkEntryHolder(message))) + it
                }
            }

            is NetworkResponseMessage -> {
                networkEntries.update { entries ->
                    val entry = entries.getValue(message.id)
                    entries
                        .toMutableMap()
                        .apply { replace(message.id, entry.copy(response = message)) }
                        .toMap()
                }
            }
        }
    }
}
