@file:Suppress("ktlint:standard:filename")

package testbench.gradle.plugins

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import testbench.gradle.BuildConfig
import testbench.gradle.subplugin.ServiceGeneratorSubplugin

internal const val TESTBENCH_PLUGIN_MODULE = "TESTBENCH_PLUGIN_MODULE"

internal data class CustomPluginModuleGroup(
    val client: List<Project>,
    val desktop: Project,
    val core: Project?,
)

public typealias TestbenchPluginGroup = Triple<String?, List<String>, String>

public fun String.deserialize(): TestbenchPluginGroup {
    val (first, second, third) = split('/')
    return TestbenchPluginGroup(
        first.takeIf { it.isNotBlank() },
        second.split(",").filter { it.isNotBlank() },
        third,
    )
}

private val TestbenchPluginGroup.coreModulePath: String?
    get() = first

private val TestbenchPluginGroup.clientModulePaths: List<String>
    get() = second

private val TestbenchPluginGroup.desktopModulePath: String
    get() = third

internal fun configureCustomPlugins(project: Project): List<CustomPluginModuleGroup> {
    val settingsExtras = project.rootProject.gradle.extra

    if (!settingsExtras.has(TESTBENCH_PLUGIN_MODULE)) {
        return emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    val customPluginModules = (settingsExtras.get(TESTBENCH_PLUGIN_MODULE) as? List<String>).orEmpty()
    val benchPluginModules = customPluginModules.map { it.deserialize() }

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
    project.configureAndroidLibrary()
    project.configureKmp {
        sourceSets.commonMain {
            dependencies {
                if (project.rootProject.name == "KMP-Testbench") {
                    api(project(":plugin-toolkit-core"))
                } else {
                    api("org.drewcarlson.testbench:plugin-toolkit-core:${BuildConfig.VERSION}")
                }
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
    project.pluginManager.apply("org.jetbrains.kotlin.jvm")
    project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        project.dependencies.apply {
            coreProject?.let { add("api", it) }
            if (project.rootProject.name == "KMP-Testbench") {
                add("api", project.project(":plugin-toolkit-desktop"))
            } else {
                add("api", "org.drewcarlson.testbench:plugin-toolkit-desktop:${BuildConfig.VERSION}")
            }
        }
    }
    return project
}

private fun applyClientModuleConfiguration(
    project: Project,
    coreProject: Project?,
): Project {
    project.configureAndroidLibrary()
    project.configureKmp {
        sourceSets.commonMain {
            dependencies {
                coreProject?.let { api(it) }
                if (project.rootProject.name == "KMP-Testbench") {
                    api(project.project(":plugin-toolkit-client"))
                } else {
                    api("org.drewcarlson.testbench:plugin-toolkit-client:${BuildConfig.VERSION}")
                }
            }
        }
    }
    return project
}

private fun Project.configureAndroidLibrary() {
    pluginManager.apply("com.android.library")
    pluginManager.withPlugin("com.android.library") {
        extensions.configure<LibraryExtension> {
            namespace = "$group.${project.name.replace("-", "")}"
            compileSdk = 34
            defaultConfig {
                minSdk = 23
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
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
