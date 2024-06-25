package demo

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coingecko.CoinGeckoClient
import coingecko.models.coins.CoinList
import io.ktor.client.*
import testbench.client.TestBenchClient
import testbench.plugins.network.NetworkClientPlugin

fun main() = application {
    val networkPlugin = remember { NetworkClientPlugin() }
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
            val coins by produceState(emptyList<CoinList>()) {
                value = coingecko.getCoinList()
            }

            Column {
                coins.forEach { coin ->
                    Text(text = coin.name)
                }
            }
        }
    }
}
