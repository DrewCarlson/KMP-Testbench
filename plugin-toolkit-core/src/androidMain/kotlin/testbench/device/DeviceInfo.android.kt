package testbench.device

import android.os.Build

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = "${Build.MODEL} ${Build.VERSION.RELEASE}",
    platform = DevicePlatform.ANDROID,
    isSoftwareDevice = isEmulator,
)

/**
 * A simple emulator-detection based on the flutter tools detection logic and a couple of legacy
 * detection systems.
 *
 * Based on: https://github.com/fluttercommunity/plus_plugins/blob/f5244e368c74d8b6e7bdd0062a4a2250dcabe540/packages/device_info_plus/device_info_plus/android/src/main/kotlin/dev/fluttercommunity/plus/device_info/MethodCallHandlerImpl.kt#L110-L125
 */
private val isEmulator: Boolean
    get() = (
        (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
            Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.PRODUCT.contains("sdk") ||
            Build.PRODUCT.contains("vbox86p") ||
            Build.PRODUCT.contains("emulator") ||
            Build.PRODUCT.contains("simulator")
    )
