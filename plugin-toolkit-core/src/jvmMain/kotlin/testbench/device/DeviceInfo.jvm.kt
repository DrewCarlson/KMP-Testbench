package testbench.device

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = "JVM ${System.getProperty("java.vm.version").take(2)} ${osName()}",
    platform = DevicePlatform.JVM,
    isSoftwareDevice = false,
)


private fun osName(): String {
    val name = System.getProperty("os.name")
    return when {
        name.contains("mac", ignoreCase = true) -> "macOS"
        else -> name
    }
}
