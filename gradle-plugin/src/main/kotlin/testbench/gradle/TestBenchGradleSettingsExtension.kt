package testbench.gradle

import org.gradle.api.initialization.Settings
import org.jetbrains.kotlin.gradle.plugin.extraProperties

private val PLUGIN_MODULE_NAMES = listOf("core", "server")
internal const val TEST_BENCH_PLUGIN_MODULE = "TEST_BENCH_PLUGIN_MODULE"

public open class TestBenchGradleSettingsExtension(
    private val settings: Settings,
) {
    public fun includePlugin(
        pluginPath: String,
        block: TestBenchIncludePluginBuilder.() -> Unit = {},
    ) {
        val config = TestBenchIncludePluginBuilder().apply(block)
        val baseName = pluginPath.substringAfterLast(":")
        val newModulePaths = when {
            config.serverOnly -> listOf("server")
            config.clientVariations.isEmpty() -> PLUGIN_MODULE_NAMES + "client"
            else -> PLUGIN_MODULE_NAMES + config.clientVariations.map { "client-$it" }
        }.map { "$pluginPath:$baseName-$it" }

        newModulePaths.forEach { modulePath ->
            settings.include(modulePath)
            settings.project(modulePath).apply {
                name = modulePath.substringAfterLast(":")
                projectDir = settings.project(pluginPath).projectDir.resolve(name)
                if (!projectDir.exists()) {
                    projectDir.mkdirs()
                }
            }
        }

        val existing = if (settings.gradle.extraProperties.has(TEST_BENCH_PLUGIN_MODULE)) {
            @Suppress("UNCHECKED_CAST")
            settings.gradle.extraProperties.get(TEST_BENCH_PLUGIN_MODULE) as List<TestBenchPluginGroup>
        } else {
            emptyList()
        }

        val new = if (config.serverOnly) {
            TestBenchPluginGroup(
                coreModulePath = null,
                serverModulePath = newModulePaths[0],
                clientModulePaths = emptyList(),
            )
        } else {
            TestBenchPluginGroup(
                coreModulePath = newModulePaths[0],
                serverModulePath = newModulePaths[1],
                clientModulePaths = newModulePaths.drop(2),
            )
        }
        settings.gradle.extraProperties.set(TEST_BENCH_PLUGIN_MODULE, existing + new)
    }
}

public class TestBenchIncludePluginBuilder {
    internal val clientVariations = mutableListOf<String>()

    public var serverOnly: Boolean = false

    public fun clientVariations(vararg names: String) {
        clientVariations.addAll(names)
    }
}

internal data class TestBenchPluginGroup(
    val coreModulePath: String?,
    val clientModulePaths: List<String>,
    val serverModulePath: String,
)
