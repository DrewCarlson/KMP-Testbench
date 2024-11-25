package testbench.device

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.sizeOf
import platform.windows.OSVERSIONINFOW

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = getWindowsVersion(),
    platform = DevicePlatform.WINDOWS,
    isSoftwareDevice = false,
)

private fun getWindowsVersion(): String = memScoped {
    val osVersionInfo = alloc<OSVERSIONINFOW>()
    osVersionInfo.dwOSVersionInfoSize = sizeOf<OSVERSIONINFOW>().toUInt()
    return when {
        osVersionInfo.dwOSVersionInfoSize == 10U &&
            osVersionInfo.dwMinorVersion == 0U &&
            osVersionInfo.dwBuildNumber >= 22000u -> "Windows 11"

        osVersionInfo.dwOSVersionInfoSize == 10U &&
            osVersionInfo.dwMinorVersion == 0U -> "Windows 10"

        osVersionInfo.dwOSVersionInfoSize == 6U &&
            osVersionInfo.dwMinorVersion == 3U -> "Windows 8.1"

        osVersionInfo.dwOSVersionInfoSize == 6U &&
            osVersionInfo.dwMinorVersion == 2U -> "Windows 8"

        osVersionInfo.dwOSVersionInfoSize == 6U &&
            osVersionInfo.dwMinorVersion == 1U -> "Windows 7"

        else -> "unknown-windows"
    }
}
