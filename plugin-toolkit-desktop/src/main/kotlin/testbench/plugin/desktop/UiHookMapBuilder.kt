package testbench.plugin.desktop

public class UiHookMapBuilder {
    private val hooks = mutableMapOf<UiHookLocation, UiHook>()

    public fun addMainPanel(hook: UiHook) {
        hooks[UiHookLocation.MAIN_PANEL] = hook
    }

    internal fun build(): Map<UiHookLocation, UiHook> {
        return hooks.toMap()
    }
}

@Suppress("UnusedReceiverParameter")
public fun DesktopPlugin<*, *>.registerUi(block: UiHookMapBuilder.() -> Unit): Map<UiHookLocation, UiHook> {
    return UiHookMapBuilder().apply(block).build()
}
