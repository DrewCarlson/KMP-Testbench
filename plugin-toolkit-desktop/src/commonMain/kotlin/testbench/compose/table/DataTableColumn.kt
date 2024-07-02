package testbench.compose.table

import androidx.compose.runtime.Composable

public data class DataTableColumn<T>(
    val expanded: Boolean = false,
    val header: @Composable () -> Unit,
    val cell: @Composable (T) -> Unit,
)
