package testbench.desktop.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import testbench.device.DeviceInfo
import testbench.device.DevicePlatform
import testbench.ui.*

private val APPLE = Triple(Color(0xFFFFFFFF), TestbenchIcon.APPLE, Color.Black)
private val WINDOWS = Triple(Color(0xFF357EC7), TestbenchIcon.WINDOWS, Color.White)
private val LINUX = Triple(Color(0xFFE95420), TestbenchIcon.LINUX, Color.White)

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
    val (color, icon, iconColor) = when (platform) {
        DevicePlatform.WINDOWS -> WINDOWS

        DevicePlatform.TVOS,
        DevicePlatform.WATCHOS,
        DevicePlatform.IOS,
        DevicePlatform.MACOS,
            -> APPLE

        DevicePlatform.LINUX -> LINUX

        DevicePlatform.ANDROID -> Triple(Color(0xFFA4C639), TestbenchIcon.ANDROID, Color.White)

        DevicePlatform.NODEJS -> Triple(Color(0xFF3C873A), TestbenchIcon.NODEJS, Color.White)

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
                -> Triple(Color(0xFFFCC31E), TestbenchIcon.BROWSER_CHROME, Color.White)

            model.contains("firefox", ignoreCase = true)
                -> Triple(Color(0xFFE66000), TestbenchIcon.BROWSER_FIREFOX, Color.White)

            model.contains("opera", ignoreCase = true)
                -> Triple(Color(0xFFFF1B2D), TestbenchIcon.BROWSER_OPERA, Color.White)

            model.contains("safari", ignoreCase = true)
                -> Triple(Color(0xFF006CFF), TestbenchIcon.BROWSER_SAFARI, Color.White)

            model.contains("edge", ignoreCase = true)
                -> Triple(Color(0xFF40BFFF), TestbenchIcon.BROWSER_EDGE, Color.White)

            else -> Triple(Color(0xFF0FB5EE), TestbenchIcon.BROWSER, Color.White)
        }
    }
    val badgeColor = if (isConnected == true) {
        LocalTestbenchColors.current.success
    } else {
        LocalTestbenchColors.current.error
    }
    Box(
        modifier = modifier
            .size(22.dp)
            .thenIf(withBackground) {
                background(color, shape = RoundedCornerShape(4.dp))
            },
        contentAlignment = Alignment.Center,
    ) {
        testbench.Icon(
            icon = icon,
            tint = color.takeUnless { withBackground } ?: iconColor,
            modifier = Modifier
                .thenIf(!withBackground) { size(22.dp) }
                .thenIf(withBackground) { size(14.dp) }
                .drawWithContent {
                    drawContent()
                    val radius = 3.dp.toPx()
                    drawCircle(
                        color = badgeColor,
                        radius = radius,
                        center = Offset(size.width + radius, -radius)
                    )
                },
        )
    }
}

@Preview
@Composable
private fun DeviceInfoIconPreview() {
    TestbenchTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.background(Color.Gray)
        ) {
            DevicePlatform.entries.forEach { platform ->
                when (platform) {
                    DevicePlatform.BROWSER -> {
                        Text(platform.name)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            DeviceInfoIcon(platform, "")
                            DeviceInfoIcon(platform, "opera", isConnected = true)
                            DeviceInfoIcon(platform, "chrome", isConnected = true)
                            DeviceInfoIcon(platform, "edge", isConnected = true)
                            DeviceInfoIcon(platform, "safari", isConnected = true)
                            DeviceInfoIcon(platform, "firefox", isConnected = true)
                        }
                    }

                    DevicePlatform.JVM -> {
                        Text(platform.name)
                        DeviceInfoIcon(platform, "mac")
                        DeviceInfoIcon(platform, "windows")
                        DeviceInfoIcon(platform, "")
                    }

                    else -> {
                        Text(platform.name)
                        DeviceInfoIcon(platform, "")
                    }
                }
            }
        }
    }
}
