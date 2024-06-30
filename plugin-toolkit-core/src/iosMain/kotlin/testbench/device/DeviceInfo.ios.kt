package testbench.device

import platform.UIKit.UIDevice

internal actual fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    model = UIDevice.currentDevice.name,
    platform = DevicePlatform.IOS,
    isSoftwareDevice = simulatorcheck.isSimulator(),
)
