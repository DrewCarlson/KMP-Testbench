package demo

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coingecko.CoinGeckoClient
import coingecko.models.coins.CoinList
import io.ktor.client.*
import testbench.client.TestBenchClient
import testbench.plugins.network.KtorNetworkClientPlugin

fun main() = application {
    val networkPlugin = remember { KtorNetworkClientPlugin() }
    val coingecko = remember {
        CoinGeckoClient(
            HttpClient {
                networkPlugin.install(this)
            },
        )
    }
    LaunchedEffect(Unit) {
        TestBenchClient(
            plugins = listOf(
                networkPlugin,
            ),
        )
    }
    MaterialTheme {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Demo App",
        ) {
            var refresh by remember { mutableStateOf(0) }
            val coins by produceState(emptyList<CoinList>(), refresh) {
                coingecko.getCoinById("bitcoin")
                value = coingecko.getCoinList()
            }

            Column {
                Button(onClick = { refresh += 1 }) {
                    Text("Refresh")
                }
                coins.forEach { coin ->
                    Text(text = coin.name)
                }
            }
        }
    }
}
