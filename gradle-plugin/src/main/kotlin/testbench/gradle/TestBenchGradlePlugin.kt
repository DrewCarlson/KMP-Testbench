package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.extraProperties

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
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        project.configureKmp {
            applyDefaultHierarchyTemplate()
            jvm()

            sourceSets.all {
                explicitApi()
            }

            sourceSets.commonMain {
                dependencies {
                    api("build.wallet.testbench:plugin-toolkit-core")
                    // TODO: Allow serialization version to be specified by project
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.0")
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
