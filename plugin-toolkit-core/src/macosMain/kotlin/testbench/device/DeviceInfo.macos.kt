package testbench.device

import kotlinx.cinterop.*
import platform.darwin.sysctlbyname
import platform.posix.size_tVar

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = getMacosModel(),
    platform = DevicePlatform.MACOS,
    isSoftwareDevice = false,
)

private fun getMacosModel(): String = memScoped {
    val keyName = "hw.model"
    val size = alloc<size_tVar>()
    sysctlbyname(keyName, null, size.ptr, null, 0uL)
    return if (size.value <= 0uL) {
        "unknown-macos"
    } else {
        val buffer = allocArray<ByteVar>(size.value.toInt())
        sysctlbyname(keyName, buffer, size.ptr, null, 0uL)
        return buffer.toKStringFromUtf8()
    }
}
