package testbench.desktop.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import testbench.device.DeviceInfo
import testbench.device.DevicePlatform

private val APPLE = Color(0xFFFFFFFF) to "apple"
private val WINDOWS = Color(0xFF357EC7) to "windows"
private val LINUX = Color(0xFFE95420) to "linux"

@Composable
fun DeviceInfoIcon(
    deviceInfo: DeviceInfo,
    modifier: Modifier = Modifier,
    isConnected: Boolean? = null,
    withBackground: Boolean = true,
) {
    DeviceInfoIcon(
        platform = deviceInfo.platform,
        model = deviceInfo.model,
        isConnected = isConnected,
        withBackground = withBackground,
        modifier = modifier,
    )
}

@Composable
fun DeviceInfoIcon(
    platform: DevicePlatform,
    model: String,
    isConnected: Boolean? = null,
    withBackground: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val (color, icon) = when (platform) {
        DevicePlatform.WINDOWS -> WINDOWS

        DevicePlatform.TVOS,
        DevicePlatform.WATCHOS,
        DevicePlatform.IOS,
        DevicePlatform.MACOS,
        -> APPLE

        DevicePlatform.LINUX -> LINUX

        DevicePlatform.ANDROID -> Color(0xFFA4C639) to "android"

        DevicePlatform.NODEJS -> Color(0xFF3C873A) to "nodejs"

        DevicePlatform.JVM -> when {
            model.contains("mac", ignoreCase = true)
            -> APPLE

            model.contains("windows", ignoreCase = true)
            -> WINDOWS

            else -> LINUX
        }

        DevicePlatform.BROWSER -> when {
            model.contains("chrome", ignoreCase = true) ||
                model.contains("chromium", ignoreCase = true)
            -> Color(0xFFFCC31E) to "chrome"

            model.contains("firefox", ignoreCase = true)
            -> Color(0xFFE66000) to "firefox"

            model.contains("opera", ignoreCase = true)
            -> Color(0xFFFF1B2D) to "opera"

            model.contains("safari", ignoreCase = true)
            -> Color(0xFF006CFF) to "safari"

            model.contains("edge", ignoreCase = true)
            -> Color(0xFF40BFFF) to "edge"

            else -> Color(0xFF0FB5EE) to "browser"
        }
    }
    /*val painterProvider = rememberResourcePainterProvider(
        "icons/$icon.svg",
        TestBenchIcons::class.java,
    )
    val painter by if (isConnected == null) {
        painterProvider.getPainter(Size(14))
    } else {
        val badgeColor = if (isConnected) {
            JewelTheme.colorPalette.green(7)
        } else {
            JewelTheme.colorPalette.red(7)
        }
        painterProvider.getPainter(Badge(badgeColor, DotBadgeShape.Default), Size(14))
    }
    Box(
        modifier = modifier
            .size(22.dp)
            .thenIf(withBackground) {
                background(color, shape = RoundedCornerShape(4.dp))
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .thenIf(!withBackground) { size(22.dp) }
                .thenIf(withBackground) { size(14.dp) },
            tint = color.takeUnless { withBackground } ?: Color.Unspecified,
        )
    }*/
}

@Preview
@Composable
private fun DeviceInfoIconPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DevicePlatform.entries.forEach { platform ->
            when (platform) {
                DevicePlatform.BROWSER -> {
                    DeviceInfoIcon(platform, "opera", isConnected = true)
                    DeviceInfoIcon(platform, "chrome", isConnected = true)
                    DeviceInfoIcon(platform, "edge", isConnected = true)
                    DeviceInfoIcon(platform, "safari", isConnected = true)
                    DeviceInfoIcon(platform, "firefox", isConnected = true)
                }

                else -> {
                    DeviceInfoIcon(platform, "")
                }
            }
        }
    }
}
