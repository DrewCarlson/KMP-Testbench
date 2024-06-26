package testbench.gradle

/*
 * The goal with this extension is to create and configure the
 * modules required for a custom plugin.
 *
 * This includes:
 * - A plugin-core module containing shared message models
 * - A plugin-client module containing the client side of the plugin
 * - A plugin-server module containing the server side of the plugin, including Compose UI
 *
 * Ideally the user creates a single module folder/buildscript and
 * the rest of the module structure is sorted out automatically
 * (i.e. no nested build scripts required for each module)
 */
public open class TestBenchPluginExtension
