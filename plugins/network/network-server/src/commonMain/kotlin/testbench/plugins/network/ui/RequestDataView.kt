package testbench.plugins.network.ui

import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*
import testbench.compose.JsonTreeViewer
import testbench.plugins.network.NetworkRequestMessage
import testbench.plugins.network.NetworkResponseMessage
import java.awt.Cursor
import androidx.compose.foundation.gestures.Orientation as DragOrientation

@Composable
public fun RequestDataView(
    requestData: NetworkRequestMessage,
    responseData: NetworkResponseMessage?,
    onResize: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dividerInteractionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxHeight(),
    ) {
        VerticalSplitLayout(
            modifier = Modifier
                .fillMaxSize(),
            first = { modifier ->
                Column(
                    modifier = modifier,
                ) {
                    Text(
                        text = "Request Headers",
                        fontWeight = FontWeight.Bold,
                    )

                    requestData.headers.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(text = key)
                            Text(text = value.joinToString("\n"))
                        }
                    }

                    responseData?.let { responseData ->
                        Text(
                            text = "Response Headers",
                            fontWeight = FontWeight.Bold,
                        )

                        responseData.headers.entries.take(4).forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(text = key)
                                Text(text = value.joinToString("\n"))
                            }
                        }
                    }
                }
            },
            second = { modifier ->
                var viewingResponse by remember { mutableStateOf(true) }
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TabStrip(
                        tabs = listOf(
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
                        ),
                    )

                    val body = if (viewingResponse) {
                        responseData?.body
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
                    state = rememberDraggableState { delta -> onResize(delta.toInt()) },
                ).pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR))),
        )
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
            .fillMaxWidth()
            .height(400.dp),
    )
}
