package demo

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import testbench.client.TestbenchClient
import testbench.plugins.network.KtorNetworkClientPlugin
import testbench.plugins.network.OkHttpNetworkClientPlugin

fun main() = application {
    val networkPlugin = remember { KtorNetworkClientPlugin() }
    val okhttpNetworkPlugin = remember { OkHttpNetworkClientPlugin() }
    val http = remember {
        HttpClient(
            OkHttp.create {
                addInterceptor(okhttpNetworkPlugin)
            },
        ) {
            networkPlugin.install(this)
        }
    }
    val testbenchClient = remember {
        TestbenchClient(
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
            LaunchedEffect(refresh) {
                http.get("https://pokeapi.co/api/v2/pokemon/").bodyAsText()
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
