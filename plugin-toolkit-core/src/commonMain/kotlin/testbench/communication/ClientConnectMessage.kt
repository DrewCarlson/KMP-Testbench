package testbench.communication

import kotlinx.serialization.Serializable
import testbench.device.DeviceInfo

@Serializable
public data class ClientConnectMessage(
    val sessionId: String,
    val deviceInfo: DeviceInfo,
)
