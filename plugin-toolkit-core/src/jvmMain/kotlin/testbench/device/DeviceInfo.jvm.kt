package testbench.device

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = "JVM ${System.getProperty("java.vm.version").take(2)} ${System.getProperty("os.name")}",
    platform = DevicePlatform.JVM,
    isSoftwareDevice = false,
)
