package testbench.device

import platform.WatchKit.WKInterfaceDevice

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = WKInterfaceDevice.currentDevice().name,
    platform = DevicePlatform.WATCHOS,
    isSoftwareDevice = simulatorcheck.isSimulator(),
)
