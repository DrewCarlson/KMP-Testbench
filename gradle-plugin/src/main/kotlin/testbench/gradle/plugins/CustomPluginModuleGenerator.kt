@file:Suppress("ktlint:standard:filename")

package testbench.gradle.plugins

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import testbench.gradle.BuildConfig
import testbench.gradle.TEST_BENCH_PLUGIN_MODULE
import testbench.gradle.TestBenchPluginGroup
import testbench.gradle.subplugin.ServiceGeneratorSubplugin

internal data class CustomPluginModuleGroup(
    val client: List<Project>,
    val server: Project,
    val core: Project?,
)

internal fun configureCustomPlugins(project: Project): List<CustomPluginModuleGroup> {
    val benchPluginModules = if (project.gradle.extraProperties.has(TEST_BENCH_PLUGIN_MODULE)) {
        @Suppress("UNCHECKED_CAST")
        project.gradle.extraProperties.get(TEST_BENCH_PLUGIN_MODULE) as List<TestBenchPluginGroup>
    } else {
        return emptyList()
    }

    return benchPluginModules.map { moduleGroup ->
        val coreProject = moduleGroup.coreModulePath?.let(project::project)
        CustomPluginModuleGroup(
            core = coreProject?.apply(::applyCoreModuleConfiguration),
            server = applyServerModuleConfiguration(project.project(moduleGroup.serverModulePath), coreProject),
            client = moduleGroup.clientModulePaths.map { clientModulePath ->
                applyClientModuleConfiguration(project.project(clientModulePath), coreProject)
            },
        )
    }
}

private fun applyCoreModuleConfiguration(project: Project): Project {
    project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
    project.configureKmp {
        applyDefaultHierarchyTemplate()
        jvm()

        sourceSets.all {
            explicitApi()
        }

        sourceSets.commonMain {
            dependencies {
                api("build.wallet.testbench:plugin-toolkit-core:${BuildConfig.VERSION}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${BuildConfig.SERIALIZATION_VERSION}")
            }
        }
    }
    return project
}

private fun applyServerModuleConfiguration(
    project: Project,
    coreProject: Project?,
): Project {
    project.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    project.pluginManager.apply(ServiceGeneratorSubplugin::class.java)
    project.configureKmp {
        applyDefaultHierarchyTemplate()
        jvm()

        sourceSets.all {
            explicitApi()
        }

        sourceSets.commonMain {
            dependencies {
                coreProject?.let { api(it) }
                api("build.wallet.testbench:plugin-toolkit-server:${BuildConfig.VERSION}")
            }
        }
    }
    return project
}

private fun applyClientModuleConfiguration(
    project: Project,
    coreProject: Project?,
): Project {
    project.configureKmp {
        applyDefaultHierarchyTemplate()
        jvm()

        sourceSets.all {
            explicitApi()
        }

        sourceSets.commonMain {
            dependencies {
                if (coreProject != null) {
                    api(coreProject)
                }
                api("build.wallet.testbench:plugin-toolkit-client:${BuildConfig.VERSION}")
            }
        }
    }
    return project
}

private fun Project.configureKmp(block: KotlinMultiplatformExtension.() -> Unit) {
    project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
    project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        project.extensions.getByType<KotlinMultiplatformExtension>().apply(block)
    }
}
