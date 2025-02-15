package testbench.plugins.network.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import testbench.compose.table.DataTable
import testbench.compose.table.DataTableColumn
import testbench.compose.table.DataTableHeader
import testbench.plugins.network.NetworkRequestMessage
import testbench.plugins.network.NetworkResponseMessage
import testbench.ui.TestbenchTheme
import testbench.ui.components.ButtonType
import testbench.ui.components.JsonTreeViewer
import testbench.ui.testbench
import java.awt.Cursor
import androidx.compose.foundation.gestures.Orientation as DragOrientation

@Composable
public fun RequestDataView(
    requestData: NetworkRequestMessage,
    responseData: NetworkResponseMessage?,
    onResize: (Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val dividerInteractionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(TestbenchTheme.colors.background),
    ) {
        testbench.VerticalSplitLayout(
            modifier = Modifier
                .fillMaxSize(),
            first = {
                var viewingResponse by remember { mutableStateOf(false) }
                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        testbench.TextButton(
                            onClick = { viewingResponse = false },
                            buttonType = if (viewingResponse) ButtonType.STANDARD else ButtonType.PRIMARY,
                            content = { testbench.Text("Request Headers") },
                        )
                        testbench.VerticalDivider()
                        testbench.TextButton(
                            onClick = { viewingResponse = true },
                            buttonType = if (viewingResponse) ButtonType.PRIMARY else ButtonType.STANDARD,
                            content = { testbench.Text("Response Headers") },
                        )
                    }
                    testbench.HorizontalDivider()

                    if (viewingResponse) {
                        val responseHeaders = remember(responseData) {
                            (responseData as? NetworkResponseMessage.Completed)?.headers?.toList().orEmpty()
                        }
                        DataTable(
                            modifier = Modifier.fillMaxWidth(),
                            items = responseHeaders,
                            lazyList = false,
                            columns = listOf(HeaderKeyColumn, HeaderValueColumn),
                        )
                    } else {
                        DataTable(
                            modifier = Modifier.fillMaxWidth(),
                            items = requestData.headers.toList(),
                            lazyList = false,
                            columns = listOf(HeaderKeyColumn, HeaderValueColumn),
                        )
                    }
                }
            },
            second = {
                var viewingResponse by remember { mutableStateOf(true) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        testbench.TextButton(
                            onClick = { viewingResponse = false },
                            buttonType = if (viewingResponse) ButtonType.STANDARD else ButtonType.PRIMARY,
                            content = { testbench.Text("Request Body") },
                        )
                        testbench.VerticalDivider()
                        testbench.TextButton(
                            onClick = { viewingResponse = true },
                            buttonType = if (viewingResponse) ButtonType.PRIMARY else ButtonType.STANDARD,
                            content = { testbench.Text("Response Body") },
                        )
                    }

                    val body = if (viewingResponse) {
                        (responseData as? NetworkResponseMessage.Completed)?.body
                    } else {
                        requestData.body
                    }

                    BodyContainer(body)
                }
            },
        )

        testbench.VerticalDivider(
            modifier = Modifier
                .width(4.dp)
                .align(Alignment.CenterStart)
                .draggable(
                    interactionSource = dividerInteractionSource,
                    orientation = DragOrientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        with(density) {
                            onResize(delta.toDp())
                        }
                    },
                ).pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR))),
        )
    }
}

@Composable
private fun BodyContainer(
    body: String? = null,
    modifier: Modifier = Modifier,
) {
    val json = try {
        if (body == null) {
            JsonNull
        } else {
            Json.decodeFromString<JsonElement>(body)
        }
    } catch (e: SerializationException) {
        JsonPrimitive(body)
    }
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        JsonTreeViewer(
            rootElement = json,
            modifier = Modifier
                .fillMaxWidth(),
        )
        /*VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.TopEnd),
        )*/
    }
}

private val HeaderKeyColumn = DataTableColumn<Pair<String, List<String>>>(
    expanded = true,
    header = {
        DataTableHeader("Key")
    },
    cell = { data ->
        val (key, _) = data
        testbench.Text(
            text = key,
            modifier = Modifier.padding(6.dp),
        )
    },
)

private val HeaderValueColumn = DataTableColumn<Pair<String, List<String>>>(
    expanded = true,
    header = {
        DataTableHeader("Value")
    },
    cell = { data ->
        val (_, value) = data
        testbench.Text(
            text = value.joinToString("\n"),
            modifier = Modifier.padding(6.dp),
        )
    },
)
