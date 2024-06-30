package testbench.device

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = "Linux", // get os distro name
    platform = DevicePlatform.LINUX,
    isSoftwareDevice = false,
)
