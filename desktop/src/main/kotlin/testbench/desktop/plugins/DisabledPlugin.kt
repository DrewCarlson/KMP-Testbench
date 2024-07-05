package testbench.desktop.plugins

import testbench.plugin.desktop.DesktopPlugin

data class DisabledPlugin(
    val plugin: DesktopPlugin<*, *>,
    val reason: Reason,
) {
    enum class Reason {
        UNSUPPORTED,
        MISSING_CLIENT,
    }
}
