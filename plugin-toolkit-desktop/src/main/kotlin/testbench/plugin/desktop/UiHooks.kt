package testbench.plugin.desktop

public class UiHooks internal constructor(
    map: Map<UiHookLocation, UiHook>,
) : Map<UiHookLocation, UiHook> by map
