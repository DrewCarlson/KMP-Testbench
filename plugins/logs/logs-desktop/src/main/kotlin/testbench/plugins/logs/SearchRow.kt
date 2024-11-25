package testbench.plugins.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import testbench.ui.testbench

@Composable
public fun SearchRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearEntries: () -> Unit,
) {
    val textFieldState = rememberTextFieldState(searchQuery)
    LaunchedEffect(textFieldState) {
        onSearchQueryChange(textFieldState.text.toString())
    }
    Row(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = Modifier.weight(1f, fill = true),
            state = textFieldState,
            /*placeholder = {
                Text(
                    text = "Search...",
                    maxLines = 1,
                )
            },
            leadingIcon = {
                Icon(
                    key = PathIconKey(
                        "icons/search_dark.svg",
                        LogsPlaceholderPlugin::class.java,
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },*/
        )
        testbench.Text(
            text = "Clear",
            maxLines = 1,
            modifier = Modifier.clickable(onClick = onClearEntries)
        )
    }
}
