package testbench.communication

import kotlinx.serialization.Serializable
import testbench.device.DeviceInfo

@Serializable
public data class DesktopConnectMessage(
    val deviceInfo: DeviceInfo,
    val pluginIds: List<String>,
)
