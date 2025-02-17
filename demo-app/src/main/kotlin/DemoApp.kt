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
import io.ktor.client.engine.okhttp.*
import testbench.client.TestBenchClient
import testbench.plugins.network.KtorNetworkClientPlugin
import testbench.plugins.network.OkHttpNetworkClientPlugin

fun main() = application {
    val networkPlugin = remember { KtorNetworkClientPlugin() }
    val okhttpNetworkPlugin = remember { OkHttpNetworkClientPlugin() }
    val coingecko = remember {
        CoinGeckoClient(
            HttpClient(
                OkHttp.create {
                    addInterceptor(okhttpNetworkPlugin.interceptor)
                },
            ) {
                networkPlugin.install(this)
            },
        )
    }
    val testbenchClient = remember {
        TestBenchClient(
            plugins = listOf(
                networkPlugin,
                okhttpNetworkPlugin,
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
                kotlin.runCatching { coingecko.getCoinById("bitcoin") }
                // value = coingecko.getCoinList()
            }

            val isConnected by testbenchClient.isConnectedFlow.collectAsState()
            val isEnabled by testbenchClient.isEnabledFlow.collectAsState()

            Column {
                if (isConnected) {
                    Text("Connected")
                } else {
                    Text("Disconnected")
                }

                Button(
                    onClick = {
                        if (testbenchClient.isEnabled) {
                            testbenchClient.disable()
                        } else {
                            testbenchClient.enable()
                        }
                    },
                ) {
                    if (isEnabled) {
                        Text("Disable")
                    } else {
                        Text("Enable")
                    }
                }

                Button(onClick = { refresh += 1 }) {
                    Text("Load Data")
                }
            }
        }
    }
}
