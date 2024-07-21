package testbench.compose.table

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.colorPalette
import java.awt.Cursor

@Composable
public fun <T> DataTable(
    columns: List<DataTableColumn<T>>,
    items: List<T>,
    lazyList: Boolean = true,
    onItemClick: (T) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val handleColor = JewelTheme.colorPalette.grey(5)
    var tableWidth by remember { mutableStateOf(0f) }
    var columnWeights by remember {
        mutableStateOf(columns.map { if (it.expanded) 2f else 1f })
    }
    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val scrollbarAdapter = if (lazyList) {
        rememberScrollbarAdapter(lazyListState)
    } else {
        rememberScrollbarAdapter(scrollState)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = handleColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f,
                    )
                }.onGloballyPositioned { coordinates ->
                    tableWidth = coordinates.size.width.toFloat()
                },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEachIndexed { index, column ->
                HeaderCell(
                    modifier = Modifier.weight(columnWeights[index]),
                    divider = index < columns.lastIndex,
                    handleColor = handleColor,
                    onDrag = onDrag@{
                        if (tableWidth == 0f) return@onDrag
                        columnWeights = calculateColumnWeightsForDrag(columnWeights, it, tableWidth, index)
                    },
                    body = column.header,
                )
            }
        }
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
                        tableRow(onItemClick, item, handleColor, columns, columnWeights)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .verticalScroll(scrollState),
                ) {
                    items.forEach { item ->
                        tableRow(onItemClick, item, handleColor, columns, columnWeights)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeaderCell(
    modifier: Modifier,
    divider: Boolean,
    handleColor: Color,
    handleSize: DpSize = DpSize(2.dp, Dp.Infinity),
    onDrag: (dragAmountX: Float) -> Unit,
    body: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        body()
        if (divider) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
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
                            x = size.width - handleWidth,
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
    }
}

@Composable
private fun <T> tableRow(
    onItemClick: (T) -> Unit,
    item: T,
    handleColor: Color,
    columns: List<DataTableColumn<T>>,
    columnWeights: List<Float>,
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
            Box(
                modifier = Modifier
                    .weight(columnWeights[index]),
                contentAlignment = Alignment.CenterStart,
            ) {
                dataTableColumn.cell(item)
            }
        }
    }
}

private fun calculateColumnWeightsForDrag(
    columnWeights: List<Float>,
    dragAmountX: Float,
    tableWidth: Float,
    index: Int,
): MutableList<Float> {
    val columnWeightSum = columnWeights.sum()
    val deltaWeight = dragAmountX / tableWidth * columnWeightSum

    val newWidths = columnWeights.toMutableList()
    val left = (newWidths[index] + deltaWeight).coerceAtLeast(0.1f)
    val right = (newWidths[index + 1] - deltaWeight).coerceAtLeast(0.1f)

    val adjustedDeltaWeight = if (deltaWeight > 0) {
        minOf(
            left - columnWeights[index],
            columnWeights[index + 1] - right,
        )
    } else {
        maxOf(
            left - columnWeights[index],
            columnWeights[index + 1] - right,
        )
    }

    newWidths[index] =
        (columnWeights[index] + adjustedDeltaWeight)
            .coerceAtLeast(0.1f)
    newWidths[index + 1] =
        (columnWeights[index + 1] - adjustedDeltaWeight)
            .coerceAtLeast(0.1f)

    val correctedTotal = newWidths.sum()
    val error = columnWeightSum - correctedTotal
    newWidths[index] += error / 2
    newWidths[index + 1] += error / 2
    return newWidths
}
