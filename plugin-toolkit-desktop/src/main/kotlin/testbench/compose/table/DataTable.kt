package testbench.compose.table

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import java.awt.Cursor

@Composable
public fun <T> DataTable(
    columns: List<DataTableColumn<T>>,
    items: List<T>,
    lazyList: Boolean = true,
    onItemClick: (T) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val handleColor = Color.Gray
    val columnWidths = remember { mutableStateOf(columns.map { 0.dp }) }
    val columnWidthOverride = remember { mutableStateOf(columnWidths.value) }
    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val scrollbarAdapter = if (lazyList) {
        rememberScrollbarAdapter(lazyListState)
    } else {
        rememberScrollbarAdapter(scrollState)
    }

    Column(modifier = modifier) {
        ColumnHeaders(
            columns = columns,
            handleColor = handleColor,
            columnWidths = columnWidths,
            columnWidthOverride = columnWidthOverride,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (lazyList) {
                LazyColumn(
                    modifier = Modifier
                        .matchParentSize(),
                    state = lazyListState,
                ) {
                    items(items) { item ->
                        TableRow(onItemClick, item, handleColor, columns, columnWidths.value)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .verticalScroll(scrollState),
                ) {
                    items.forEach { item ->
                        TableRow(onItemClick, item, handleColor, columns, columnWidths.value)
                    }
                }
            }

            VerticalScrollbar(
                adapter = scrollbarAdapter,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
            )
        }
    }
}

@Composable
private fun <T> ColumnHeaders(
    columns: List<DataTableColumn<T>>,
    handleColor: Color,
    columnWidths: MutableState<List<Dp>>,
    columnWidthOverride: MutableState<List<Dp>>,
) {
    Layout(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = handleColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f,
                )
            },
        content = {
            val density = LocalDensity.current
            columns.forEachIndexed { index, column ->
                if (index > 0) {
                    HeaderHandle(
                        modifier = Modifier.layoutId("handle"),
                        handleColor = handleColor,
                        onDrag = onDrag@{ x ->
                            val delta = with(density) { x.toDp() }
                            val newWidths = columnWidthOverride.value.toMutableList()
                            if ((index == columns.lastIndex && !column.expanded) || columns[index - 1].expanded) {
                                val newWidth = newWidths[index] - delta
                                if (newWidth >= 0.dp) {
                                    newWidths[index] = newWidth
                                }
                            } else {
                                val newWidth = newWidths[index - 1] + delta
                                if (newWidth >= 0.dp) {
                                    newWidths[index - 1] = newWidth
                                }
                            }

                            if (newWidths.any { it < 0.dp }) return@onDrag

                            columnWidthOverride.value = newWidths
                        },
                    )
                }
                HeaderCell(
                    modifier = Modifier,
                    body = column.header,
                )
            }
        },
    ) { measurables, constraints ->
        // Measure handles, keeping them as small as possible
        val handlePlaceables = measurables
            .filter { it.layoutId == "handle" }
            .map { measurable -> measurable.measure(constraints.copy(minWidth = 0)) }
        // Measure non-expanding columns to find remaining width for expanded columns
        val headerMeasurables = measurables.filterNot { it.layoutId == "handle" }
        val staticPlaceables = headerMeasurables
            .mapIndexed { index, measurable ->
                if (columns[index].expanded) {
                    // expanded columns will have their size calculated after all other columns are measured
                    null
                } else {
                    // measure the non-expanded column with its minimum width
                    val min = measurable
                        .maxIntrinsicWidth(0)
                    val remainingMaxWidth = constraints.maxWidth - headerMeasurables
                        .filterIndexed { i, _ -> i != index && !columns[i].expanded }
                        .sumOf { it.maxIntrinsicWidth(0) }
                    val actualMin = (min + columnWidthOverride.value[index].roundToPx())
                        .coerceIn(0, (min + remainingMaxWidth).coerceAtLeast(0))
                    measurable.measure(constraints.copy(minWidth = actualMin, maxWidth = actualMin))
                }
            }

        // Calculate total fixed width and determine remaining width for expanded columns
        val totalFixedWidth = staticPlaceables
            .filterIndexed { index, _ -> !columns[index].expanded }
            .sumOf { it?.width ?: 0 }
        val remainingWidth = (constraints.maxWidth - totalFixedWidth).coerceAtLeast(0)
        val expandedColumnCount = columns.count { it.expanded }

        // Calculate the width for each column
        columnWidths.value = staticPlaceables.mapIndexed { index, placeable ->
            val minWidth = headerMeasurables[index].minIntrinsicWidth(0)
            val remaining = remainingWidth.coerceAtLeast(minWidth)
            if (columns[index].expanded) {
                // Distribute remaining width among expanded columns
                (remaining / expandedColumnCount + columnWidthOverride.value[index].roundToPx())
                    .toDp()
                    .coerceIn(0.dp, remaining.toDp())
            } else {
                // Use measured min width for non-expanded columns
                (placeable?.measuredWidth ?: 0).toDp()
            }
        }

        val placeables = staticPlaceables.mapIndexed { index, placeable ->
            // return static placeable or calculate the expanding size and measure cell
            placeable ?: headerMeasurables[index].measure(
                Constraints.fixedWidth(
                    (columnWidths.value[index].roundToPx())
                        .coerceAtLeast(0),
                ),
            )
        }

        val maxHeight = placeables.maxOfOrNull { it.height } ?: constraints.minHeight
        layout(constraints.maxWidth, maxHeight) {
            placeables.foldIndexed(0) { index, xPosition, placeable ->
                placeable.placeRelative(x = xPosition, y = 0)

                if (index > 0) {
                    val handle = handlePlaceables[index - 1]
                    handle.placeRelative(
                        x = xPosition - (handle.width / 2),
                        y = ((maxHeight / 2) - (handle.height / 2)),
                    )
                }

                xPosition + placeable.width
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeaderHandle(
    modifier: Modifier,
    handleColor: Color,
    handleSize: DpSize = DpSize(2.dp, Dp.Infinity),
    onDrag: (dragAmountX: Float) -> Unit,
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .clipToBounds()
            .drawWithCache {
                val handleWidth = handleSize.width.toPx()
                val handleHeight = when (handleSize.height) {
                    Dp.Unspecified, Dp.Infinity -> size.height
                    else -> handleSize.height.toPx()
                }
                val adjustedHandleSize = Size(handleWidth, handleHeight)
                val handleOffset = Offset(
                    x = (size.width / 2) - (handleWidth / 2),
                    y = size.height - handleHeight,
                )
                onDrawBehind {
                    drawRect(
                        color = handleColor,
                        topLeft = handleOffset,
                        size = adjustedHandleSize,
                    )
                }
            }.onDrag { dragAmount -> onDrag(dragAmount.x) }
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR))),
    )
}

@Composable
private fun HeaderCell(
    modifier: Modifier,
    body: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        body()
    }
}

@Composable
private fun <T> TableRow(
    onItemClick: (T) -> Unit,
    item: T,
    handleColor: Color,
    columns: List<DataTableColumn<T>>,
    columnWidths: List<Dp>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .drawBehind {
                drawLine(
                    color = handleColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f,
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        columns.forEachIndexed { index, dataTableColumn ->
            val width = remember(index, dataTableColumn, columnWidths) { columnWidths[index] }
            Box(
                modifier = Modifier
                    .width(width),
                contentAlignment = Alignment.CenterStart,
            ) {
                dataTableColumn.cell(item)
            }
        }
    }
}
