package testbench.plugins.logs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import org.jetbrains.jewel.ui.component.Text
import testbench.compose.table.DataTable
import testbench.compose.table.DataTableColumn
import testbench.compose.table.DataTableHeader

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
        Text(
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
        Text(
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
        Text(
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
        Text(
            text = entry.message,
            overflow = TextOverflow.Visible,
            modifier = Modifier.padding(6.dp),
        )
    },
)
