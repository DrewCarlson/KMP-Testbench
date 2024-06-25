package testbench.plugins.network

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.util.thenIf
import testbench.plugin.server.ServerPlugin

private data class NetworkEntryHolder(
    val request: NetworkRequestMessage,
    val response: NetworkResponseMessage? = null,
)

public class NetworkServerPlugin : ServerPlugin {
    override val id: String = "network"

    override val name: String = "Network"

    override val pluginIcon: String = "globe"

    private val networkEntries = MutableStateFlow(emptyMap<String, NetworkEntryHolder>())

    override fun handleMessage(message: String) {
        when (val networkMessage = Json.decodeFromString<NetworkPluginMessage>(message)) {
            is NetworkRequestMessage -> {
                networkEntries.update {
                    it.plus(Pair(networkMessage.id, NetworkEntryHolder(networkMessage)))
                }
            }

            is NetworkResponseMessage -> {
                networkEntries.update { entries ->
                    val entry = entries.getValue(networkMessage.id)
                    entries
                        .toMutableMap()
                        .apply { replace(networkMessage.id, entry.copy(response = networkMessage)) }
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
                        Text(text = entry.request.url)
                        Text(text = entry.request.method)
                        Text(text = entry.response?.status?.toString() ?: "Pending")
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
                        .padding(4.dp)
                        .align(Alignment.CenterEnd),
                ) {
                    Divider(
                        orientation = Orientation.Vertical,
                        modifier = Modifier.align(Alignment.CenterStart),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
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
                }
            }
        }
    }
}
