package testbench.plugins.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import testbench.compose.table.DataTable
import testbench.compose.table.DataTableColumn
import testbench.compose.table.DataTableHeader
import testbench.ui.testbench

@Composable
internal fun LogsMainPanel(
    entries: List<LogEntry>,
    modifier: Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SearchRow(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onClearEntries = {},
        )

        val filteredEntries = remember(searchQuery, entries) {
            if (searchQuery.isBlank()) {
                entries
            } else {
                entries.filter { entry ->
                    entry.message.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        DataTable(
            items = filteredEntries,
            modifier = Modifier.fillMaxWidth(),
            onItemClick = { },
            columns = listOf(
                timeColumn,
                pidColumn,
                tagColumn,
                messageColumn,
            ),
        )
    }
}

private val timeColumn = DataTableColumn<LogEntry>(
    header = { DataTableHeader(text = "Time") },
    cell = { entry ->
        testbench.Text(
            text = entry.timestamp.format(DateTimeComponents.Formats.RFC_1123),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val tagColumn = DataTableColumn<LogEntry>(
    header = { DataTableHeader(text = "Tag") },
    cell = { entry ->
        testbench.Text(
            text = entry.tag,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val pidColumn = DataTableColumn<LogEntry>(
    header = { DataTableHeader(text = "PID") },
    cell = { entry ->
        testbench.Text(
            text = entry.pid,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val messageColumn = DataTableColumn<LogEntry>(
    expanded = true,
    header = { DataTableHeader(text = "Message") },
    cell = { entry ->
        testbench.Text(
            text = entry.message,
            overflow = TextOverflow.Visible,
            modifier = Modifier.padding(6.dp),
        )
    },
)
