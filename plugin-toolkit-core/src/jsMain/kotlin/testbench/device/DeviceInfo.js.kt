package testbench.device

import kotlinx.browser.window

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = getBrowserName(),
    platform = DevicePlatform.BROWSER,
    isSoftwareDevice = false,
)

private fun getBrowserName(): String = when {
    uaContains("Chrome/") &&
        !uaContains("Chromium/") &&
        !uaContains("Edg") ->
        "Chrome"

    uaContains("Chromium/") -> "Chromium"

    uaContains("Edg/") -> "Edge"

    uaContains("Safari/") -> "Safari"

    uaContains("OPR/") || uaContains("Opera/") -> "Opera"

    uaContains("Firefox/") && !uaContains("Seamonkey/") -> "Firefox"

    else -> window.navigator.appCodeName
}

private fun uaContains(value: String): Boolean = window.navigator.userAgent.contains(value, ignoreCase = true)
