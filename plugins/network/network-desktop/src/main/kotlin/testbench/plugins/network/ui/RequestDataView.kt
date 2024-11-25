package testbench.plugins.network.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import testbench.compose.JsonTreeViewer
import testbench.compose.table.DataTable
import testbench.compose.table.DataTableColumn
import testbench.compose.table.DataTableHeader
import testbench.plugins.network.NetworkRequestMessage
import testbench.plugins.network.NetworkResponseMessage
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
            .fillMaxHeight(),
    ) {
        /*VerticalSplitLayout(
            modifier = Modifier
                .fillMaxSize(),
            first = {
                var viewingResponse by remember { mutableStateOf(false) }
                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                    TabStrip(
                        style = LocalDefaultTabStyle.current,
                        tabs = listOf(
                            TabData.Default(
                                selected = !viewingResponse,
                                closable = false,
                                onClick = { viewingResponse = false },
                                content = { tabState ->
                                    SimpleTabContent(
                                        label = "Request Headers",
                                        state = tabState,
                                    )
                                },
                            ),
                            TabData.Default(
                                selected = viewingResponse,
                                closable = false,
                                onClick = { viewingResponse = true },
                                content = { tabState ->
                                    SimpleTabContent(
                                        label = "Response Headers",
                                        state = tabState,
                                    )
                                },
                            ),
                        ),
                    )

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
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TabStrip(
                        style = LocalDefaultTabStyle.current,
                        tabs = listOf(
                            TabData.Default(
                                selected = !viewingResponse,
                                closable = false,
                                onClick = { viewingResponse = false },
                                content = { tabState ->
                                    SimpleTabContent(
                                        label = "Request Body",
                                        state = tabState,
                                    )
                                },
                            ),
                            TabData.Default(
                                selected = viewingResponse,
                                closable = false,
                                onClick = { viewingResponse = true },
                                content = { tabState ->
                                    SimpleTabContent(
                                        label = "Response Body",
                                        state = tabState,
                                    )
                                },
                            ),
                        ),
                    )

                    val body = if (viewingResponse) {
                        (responseData as? NetworkResponseMessage.Completed)?.body
                    } else {
                        requestData.body
                    }

                    BodyContainer(
                        body = body,
                    )
                }
            },
        )

        Divider(
            orientation = Orientation.Vertical,
            modifier = Modifier
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
        )*/
    }
}

@Composable
private fun BodyContainer(body: String? = null) {
    val json = try {
        if (body == null) {
            JsonNull
        } else {
            Json.decodeFromString<JsonElement>(body)
        }
    } catch (e: SerializationException) {
        JsonPrimitive(body)
    }
    JsonTreeViewer(
        rootElement = json,
        modifier = Modifier
            .defaultMinSize(minHeight = 400.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
    )
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
