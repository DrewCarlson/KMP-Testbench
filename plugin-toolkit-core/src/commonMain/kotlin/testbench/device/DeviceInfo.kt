package testbench.device

import kotlinx.serialization.Serializable

@Serializable
public data class DeviceInfo(
    public val model: String,
    public val platform: DevicePlatform,
    public val isSoftwareDevice: Boolean,
) {
    public companion object {
        public val host: DeviceInfo = getDeviceInfo()
    }
}

internal expect fun getDeviceInfo(): DeviceInfo
