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
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.util.thenIf
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
        Box(modifier = modifier) {
            val entries by networkEntries.collectAsState(initial = emptyMap())
            var selectedRequest by remember { mutableStateOf<String?>(null) }

            Column(
                modifier = Modifier
                    .thenIf(selectedRequest != null) {
                        padding(end = 350.dp)
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (entries.isEmpty()) {
                    Text("waiting for requests")
                }
                Spacer(modifier = Modifier.size(0.dp))
                entries.forEach { (id, entry) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRequest = id }
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = entry.request.url,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(text = entry.request.method, maxLines = 1)
                        Text(text = entry.response?.status?.toString() ?: "Pending", maxLines = 1)
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
                    val scrollState = rememberScrollState()
                    Divider(
                        orientation = Orientation.Vertical,
                        modifier = Modifier.align(Alignment.CenterStart),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Column {
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

                            Text(
                                text = "Request Body",
                                fontWeight = FontWeight.Bold,
                            )

                            Text(
                                text = requestData.body ?: "<no content>",
                            )
                        }

                        responseData?.let { responseData ->
                            Column {
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
                                Text(
                                    text = "Response Body",
                                    fontWeight = FontWeight.Bold,
                                )

                                Text(
                                    text = responseData.body ?: "<no content>",
                                )
                            }
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState),
                    )
                }
            }
        }
    }
}
