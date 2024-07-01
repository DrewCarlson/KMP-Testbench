@file:Suppress("ktlint:standard:filename")

package testbench.gradle.plugins

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import testbench.gradle.BuildConfig
import testbench.gradle.subplugin.ServiceGeneratorSubplugin

internal data class CustomPluginModuleGroup(
    val client: List<Project>,
    val desktop: Project,
    val core: Project?,
)

public typealias TestBenchPluginGroup = Triple<String?, List<String>, String>

public fun String.deserialize(): TestBenchPluginGroup {
    val (first, second, third) = split('/')
    return TestBenchPluginGroup(
        first.takeIf { it.isNotBlank() },
        second.split(",").filter { it.isNotBlank() },
        third,
    )
}

private val TestBenchPluginGroup.coreModulePath: String?
    get() = first

private val TestBenchPluginGroup.clientModulePaths: List<String>
    get() = second

private val TestBenchPluginGroup.desktopModulePath: String
    get() = third

internal fun configureCustomPlugins(project: Project): List<CustomPluginModuleGroup> {
    val modulesListFile = project.rootProject.rootDir.resolve("build/testbench.modules")
    val benchPluginModules = if (modulesListFile.exists()) {
        modulesListFile.readText().lines()
    } else {
        return emptyList()
    }.map { it.deserialize() }

    return benchPluginModules.map { moduleGroup ->
        val coreProject = moduleGroup.coreModulePath
            ?.takeIf { it.isNotBlank() }
            ?.let(project::project)
        CustomPluginModuleGroup(
            core = coreProject?.let(::applyCoreModuleConfiguration),
            desktop = applyDesktopModuleConfiguration(project.project(moduleGroup.desktopModulePath), coreProject),
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
                api("org.drewcarlson.testbench:plugin-toolkit-core:${BuildConfig.VERSION}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${BuildConfig.SERIALIZATION_VERSION}")
            }
        }
    }
    return project
}

private fun applyDesktopModuleConfiguration(
    project: Project,
    coreProject: Project?,
): Project {
    project.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    project.pluginManager.apply(ServiceGeneratorSubplugin::class)
    project.configureKmp {
        applyDefaultHierarchyTemplate()
        jvm()

        sourceSets.all {
            explicitApi()
        }

        sourceSets.commonMain {
            dependencies {
                coreProject?.let { api(it) }
                api("org.drewcarlson.testbench:plugin-toolkit-desktop:${BuildConfig.VERSION}")
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
                api("org.drewcarlson.testbench:plugin-toolkit-client:${BuildConfig.VERSION}")
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
