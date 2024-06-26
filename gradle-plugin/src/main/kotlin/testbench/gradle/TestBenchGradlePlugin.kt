package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.extraProperties

private val PLUGIN_MODULE_NAMES = listOf("core", "server")
private const val TEST_BENCH_PLUGIN_MODULE = "TEST_BENCH_PLUGIN_MODULE"

public class TestBenchGradleSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.extensions.create(
            "testbench",
            TestBenchGradleSettingsExtension::class.java,
            settings,
        )
    }
}

private data class TestBenchPluginGroup(
    val coreModulePath: String,
    val clientModulePaths: List<String>,
    val serverModulePath: String,
)

public class TestBenchIncludePluginBuilder {
    internal val clientVariations = mutableListOf<String>()

    public fun clientVariations(vararg names: String) {
        clientVariations.addAll(names)
    }
}

public open class TestBenchGradleSettingsExtension(
    private val settings: Settings,
) {
    public fun includePlugin(
        pluginPath: String,
        block: TestBenchIncludePluginBuilder.() -> Unit = {},
    ) {
        val config = TestBenchIncludePluginBuilder().apply(block)
        val baseName = pluginPath.substringAfterLast(":")
        val newModulePaths = if (config.clientVariations.isEmpty()) {
            (PLUGIN_MODULE_NAMES + "client")
        } else {
            (PLUGIN_MODULE_NAMES + config.clientVariations.map { "client-$it" })
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

        val new = TestBenchPluginGroup(
            coreModulePath = newModulePaths[0],
            serverModulePath = newModulePaths[1],
            clientModulePaths = newModulePaths.drop(2),
        )
        settings.gradle.extraProperties.set(TEST_BENCH_PLUGIN_MODULE, existing + new)
    }
}

public class TestBenchGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val benchPluginModules = if (project.gradle.extraProperties.has(TEST_BENCH_PLUGIN_MODULE)) {
            @Suppress("UNCHECKED_CAST")
            project.gradle.extraProperties.get(TEST_BENCH_PLUGIN_MODULE) as List<TestBenchPluginGroup>
        } else {
            return
        }

        benchPluginModules.forEach { moduleGroup ->
            val coreProject = project.project(moduleGroup.coreModulePath)
            applyCoreModuleConfiguration(coreProject)
            applyServerModuleConfiguration(project.project(moduleGroup.serverModulePath), coreProject)
            moduleGroup.clientModulePaths.forEach { clientModulePath ->
                applyClientModuleConfiguration(project.project(clientModulePath), coreProject)
            }
        }
    }

    private fun applyCoreModuleConfiguration(project: Project) {
        project.configureKmp {
            applyDefaultHierarchyTemplate()
            jvm()

            sourceSets.all {
                explicitApi()
            }

            sourceSets.commonMain {
                dependencies {
                    api("build.wallet.testbench:plugin-toolkit-core")
                }
            }
        }
    }

    private fun applyServerModuleConfiguration(
        project: Project,
        coreProject: Project,
    ) {
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        project.configureKmp {
            applyDefaultHierarchyTemplate()
            jvm()

            sourceSets.all {
                explicitApi()
            }

            sourceSets.commonMain {
                dependencies {
                    api(coreProject)
                    api("build.wallet.testbench:plugin-toolkit-server")
                }
            }
        }
    }

    private fun applyClientModuleConfiguration(
        project: Project,
        coreProject: Project,
    ) {
        project.configureKmp {
            applyDefaultHierarchyTemplate()
            jvm()

            sourceSets.all {
                explicitApi()
            }

            sourceSets.commonMain {
                dependencies {
                    api(coreProject)
                    api("build.wallet.testbench:plugin-toolkit-client")
                }
            }
        }
    }

    private fun Project.configureKmp(block: KotlinMultiplatformExtension.() -> Unit) {
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            project.extensions.getByType<KotlinMultiplatformExtension>().apply(block)
        }
    }
}
