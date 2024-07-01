package testbench.gradle

import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra

private val PLUGIN_MODULE_NAMES = listOf("core", "desktop")
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
            config.desktopOnly -> listOf("desktop")
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

        val existing = if (settings.gradle.extra.has(TEST_BENCH_PLUGIN_MODULE)) {
            @Suppress("UNCHECKED_CAST")
            settings.gradle.extra.get(TEST_BENCH_PLUGIN_MODULE) as List<String>
        } else {
            emptyList()
        }

        val new = if (config.desktopOnly) {
            TestBenchPluginGroup(
                // coreModulePath =
                null,
                // clientModulePaths =
                emptyList(),
                // desktopModulePath =
                newModulePaths[0],
            )
        } else {
            TestBenchPluginGroup(
                // coreModulePath =
                newModulePaths[0],
                // clientModulePaths =e
                newModulePaths.drop(2),
                // desktopModulePath =
                newModulePaths[1],
            )
        }.serialize()
        settings.gradle.extra.set(TEST_BENCH_PLUGIN_MODULE, existing + new)
    }
}

public class TestBenchIncludePluginBuilder {
    internal val clientVariations = mutableListOf<String>()

    public var desktopOnly: Boolean = false

    public fun clientVariations(vararg names: String) {
        clientVariations.addAll(names)
    }
}

public typealias TestBenchPluginGroup = Triple<String?, List<String>, String>

public fun TestBenchPluginGroup.serialize(): String = "$first/${second.joinToString(separator = ",")}/$third"
