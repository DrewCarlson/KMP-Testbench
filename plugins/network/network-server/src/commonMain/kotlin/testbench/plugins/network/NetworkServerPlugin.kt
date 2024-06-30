package testbench.plugins.network

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.util.thenIf
import testbench.compose.JsonTreeViewer
import testbench.plugin.server.ServerPlugin

private data class NetworkEntryHolder(
    val request: NetworkRequestMessage,
    val response: NetworkResponseMessage? = null,
)

public class NetworkServerPlugin :
    NetworkPlugin(),
    ServerPlugin<NetworkPluginMessage, Unit> {
    private val networkEntries = MutableStateFlow(emptyMap<String, NetworkEntryHolder>())

    override fun handleMessage(message: NetworkPluginMessage) {
        when (message) {
            is NetworkRequestMessage -> {
                networkEntries.update {
                    mapOf(Pair(message.id, NetworkEntryHolder(message))) + it
                }
            }

            is NetworkResponseMessage -> {
                networkEntries.update { entries ->
                    val entry = entries.getValue(message.id)
                    entries
                        .toMutableMap()
                        .apply { replace(message.id, entry.copy(response = message)) }
                        .toMap()
                }
            }
        }
    }

    @Composable
    override fun renderPanel(modifier: Modifier) {
        var searchQuery by remember { mutableStateOf("") }
        Box(modifier = modifier) {
            val entries by networkEntries.collectAsState(initial = emptyMap())
            var selectedRequest by remember(entries) { mutableStateOf<String?>(null) }

            Column(
                modifier = Modifier
                    .thenIf(selectedRequest != null) {
                        padding(end = 350.dp)
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        modifier = Modifier.weight(1f, fill = true),
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search...",
                                maxLines = 1,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                resource = "icons/search_dark.svg",
                                contentDescription = null,
                                iconClass = NetworkServerPlugin::class.java,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                    )
                    DefaultButton(
                        onClick = { networkEntries.update { emptyMap() } },
                    ) {
                        Text(
                            text = "Clear",
                            maxLines = 1,
                        )
                    }
                }
                if (entries.isEmpty()) {
                    Text("waiting for requests")
                }
                entries
                    .run {
                        if (searchQuery.isNotBlank()) {
                            filter { (_, value) -> value.request.url.contains(searchQuery, ignoreCase = true) }
                        } else {
                            this
                        }
                    }.forEach { (id, entry) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRequest = id }
                                .padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = entry.request.url,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = true),
                            )
                            Text(
                                text = entry.request.method,
                                maxLines = 1,
                            )
                            Text(
                                text = entry.response?.status?.toString() ?: "...",
                                maxLines = 1,
                            )
                        }
                    }
                Spacer(modifier = Modifier.size(0.dp))
            }

            selectedRequest?.let { id ->
                val requestData = remember(id, entries) { entries.getValue(id).request }
                val responseData = remember(id, entries) { entries.getValue(id).response }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(350.dp)
                        .align(Alignment.CenterEnd),
                ) {
                    Divider(
                        orientation = Orientation.Vertical,
                        modifier = Modifier.align(Alignment.CenterStart),
                    )

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
                }
            }
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
}
