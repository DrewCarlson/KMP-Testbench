package testbench.plugins.network

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.util.thenIf
import testbench.plugin.server.ServerPlugin
import testbench.plugins.network.ui.NetworkEntryRow
import testbench.plugins.network.ui.RequestDataView
import testbench.plugins.network.ui.SearchRow

public data class NetworkEntryHolder(
    val request: NetworkRequestMessage,
    val response: NetworkResponseMessage? = null,
)

public class NetworkServerPlugin :
    NetworkPlugin(),
    ServerPlugin<NetworkPluginMessage, Unit> {
    private val networkEntries = MutableStateFlow(emptyMap<String, NetworkEntryHolder>())

    override fun handleMessage(message: NetworkPluginMessage) {
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

    @Composable
    override fun renderPanel(modifier: Modifier) {
        val density = LocalDensity.current
        var sidebarWidth by remember { mutableStateOf(with(density) { 350.dp.roundToPx() }) }
        var searchQuery by remember { mutableStateOf("") }
        Box(modifier = modifier) {
            val entries by networkEntries.collectAsState(initial = emptyMap())
            var selectedRequest by remember(entries) { mutableStateOf<String?>(null) }

            Column(
                modifier = Modifier
                    .thenIf(selectedRequest != null) {
                        padding(end = sidebarWidth.dp)
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SearchRow(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearEntries = { networkEntries.update { emptyMap() } },
                )

                if (entries.isEmpty()) {
                    Text("waiting for requests")
                }

                val filteredEntries = remember(searchQuery, entries) {
                    if (searchQuery.isBlank()) {
                        entries
                    } else {
                        entries.filter { (_, holder) ->
                            holder.request.url.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }

                filteredEntries.forEach { (id, entry) ->
                    NetworkEntryRow(
                        entry = entry,
                        onClick = { selectedRequest = id },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.size(6.dp))
            }

            selectedRequest?.let { id ->
                val requestData = remember(id, entries) { entries.getValue(id).request }
                val responseData = remember(id, entries) { entries.getValue(id).response }
                RequestDataView(
                    requestData = requestData,
                    responseData = responseData,
                    onResize = { sidebarWidth = (sidebarWidth - it).coerceAtLeast(0) },
                    modifier = Modifier
                        .width(sidebarWidth.dp)
                        .align(Alignment.CenterEnd),
                )
            }
        }
    }
}
