# Plugin Basics

Custom plugins are a collection of modules:

- `<plugin>-core`: A module shared by the server and client.
- `<plugin>-client`: The client plugin to send data to the desktop client.
- `<plugin>-desktop`: The desktop client plugin containing UI and server logic.

## Common Plugin

```kotlin

```

## Desktop Plugin

```kotlin
class MyDesktopPlugin : ServerPlugin<Unit, ClientMessage> {

  private val batteryPercentFlow = MutableStateFlow(0f)

  override fun handleMessage(message: ClientMessage) {
    batteryPercentFlow.value = message.batteryPercent
  }

  @Composable
  override fun renderPanel(modifier: Modifier) {
    val batteryPercent by batteryPercentFlow.collectAsState()
    Column(modifier = modifier) {
      Text("Battery:")
      ProgressBar(batteryPercent)
    }
  }
}
```

## Client Plugin

```kotlin
class MyClientPlugin : ClientPlugin<Unit, ClientMessage> {

}
```
