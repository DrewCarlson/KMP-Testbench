package testbench.plugins.network

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.format
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.util.thenIf
import testbench.compose.table.DataTable
import testbench.compose.table.DataTableColumn
import testbench.compose.table.DataTableHeader
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugins.network.ui.RequestDataView
import testbench.plugins.network.ui.SearchRow

public data class NetworkEntryHolder(
    val request: NetworkRequestMessage,
    val response: NetworkResponseMessage? = null,
)

public class NetworkDesktopPlugin :
    NetworkPlugin(),
    DesktopPlugin<NetworkPluginMessage, Unit> {
    private val networkEntries = MutableStateFlow(emptyMap<String, NetworkEntryHolder>())

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

    @Composable
    override fun renderPanel(modifier: Modifier) {
        var sidebarWidth by remember { mutableStateOf(350.dp) }
        var searchQuery by remember { mutableStateOf("") }
        Box(modifier = modifier) {
            val entries by networkEntries.collectAsState(initial = emptyMap())
            var selectedRequestId by remember { mutableStateOf<String?>(null) }
            val selectedRequest by produceState<NetworkEntryHolder?>(null, selectedRequestId, entries) {
                value = selectedRequestId?.let { entries[it] ?: entries.values.firstOrNull() }
            }

            Column(
                modifier = Modifier
                    .thenIf(selectedRequest != null) {
                        padding(end = sidebarWidth)
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SearchRow(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearEntries = { networkEntries.update { emptyMap() } },
                )

                val filteredEntries = remember(searchQuery, entries) {
                    if (searchQuery.isBlank()) {
                        entries
                    } else {
                        entries.filter { (_, holder) ->
                            holder.request.url.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }

                DataTable(
                    items = filteredEntries.values.toList(),
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = { selectedRequestId = it.request.id },
                    columns = listOf(
                        requestTimeColumn,
                        domainColumn,
                        methodColumn,
                        statusColumn,
                    ),
                )
            }

            selectedRequest?.let { (requestData, responseData) ->
                RequestDataView(
                    requestData = requestData,
                    responseData = responseData,
                    onResize = {
                        sidebarWidth = (sidebarWidth - it).coerceAtLeast(0.dp)
                    },
                    modifier = Modifier
                        .width(sidebarWidth)
                        .align(Alignment.CenterEnd),
                )
            }
        }
    }
}

private val requestTimeColumn = DataTableColumn<NetworkEntryHolder>(
    header = { DataTableHeader(text = "Request Time") },
    cell = { entry ->
        Text(
            text = entry.request.initiatedAt.format(timeFormat),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val domainColumn = DataTableColumn<NetworkEntryHolder>(
    expanded = true,
    header = { DataTableHeader(text = "Domain") },
    cell = { entry ->
        Text(
            text = entry.request.url,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val methodColumn = DataTableColumn<NetworkEntryHolder>(
    header = { DataTableHeader(text = "Method") },
    cell = { entry ->
        Text(
            text = entry.request.method,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val statusColumn = DataTableColumn<NetworkEntryHolder>(
    header = { DataTableHeader(text = "Status") },
    cell = { entry ->
        Text(
            text = entry.response?.status?.toString() ?: "pending",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)
