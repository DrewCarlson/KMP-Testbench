package testbench.plugin.desktop

@Suppress("ktlint:standard:function-naming", "FunctionName")
public class UiHookMapBuilder {
    private val hooks = mutableMapOf<UiHookLocation, UiHook>()

    public fun MainPanel(hook: UiHook) {
        hooks[UiHookLocation.MAIN_PANEL] = hook
    }

    internal fun build(): UiHooks {
        return UiHooks(hooks.toMap())
    }
}
