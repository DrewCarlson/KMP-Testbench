package testbench.plugins.network.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.format
import testbench.compose.table.DataTable
import testbench.compose.table.DataTableColumn
import testbench.compose.table.DataTableHeader
import testbench.plugins.network.NetworkEntryHolder
import testbench.plugins.network.NetworkResponseMessage
import testbench.plugins.network.timeFormat
import testbench.ui.testbench
import testbench.ui.thenIf

@Composable
internal fun NetworkMainPanel(
    entries: Map<String, NetworkEntryHolder>,
    onClearEntries: () -> Unit,
    modifier: Modifier,
) {
    var sidebarWidth by remember { mutableStateOf(350.dp) }
    var searchQuery by remember { mutableStateOf("") }
    Box(modifier = modifier) {
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
                onClearEntries = onClearEntries,
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

private val requestTimeColumn = DataTableColumn<NetworkEntryHolder>(
    header = { DataTableHeader(text = "Request Time") },
    cell = { entry ->
        testbench.Text(
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
        testbench.Text(
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
        testbench.Text(
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
        val labelText = remember(entry.response) {
            when (val response = entry.response) {
                is NetworkResponseMessage.Completed -> response.status.toString()
                is NetworkResponseMessage.Cancelled -> "cancelled"
                is NetworkResponseMessage.Failed -> "failed"
                null -> "pending"
            }
        }

        testbench.Text(
            text = labelText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)
