package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.maven
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import testbench.gradle.plugins.configureCustomPlugins
import testbench.gradle.tasks.RunTestbenchTask

internal const val RUNTIME_CONFIG = "testBenchRuntime"
internal const val RUN_TASK_NAME = "runTestbench"

public open class TestbenchGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("testbench", TestbenchExtension::class.java)

        setupTestbenchRuntime(project)

        project.tasks.create(RUN_TASK_NAME, RunTestbenchTask::class.java)

        project.repositories
            .apply {
                mavenCentral()
                google()
                maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            }
    }

    private fun setupTestbenchRuntime(project: Project) {
        val pluginProjects = try {
            configureCustomPlugins(project)
        } catch (e: Throwable) {
            project.logger.error("Failed to configure custom plugins", e)
            return
        }
        val testBenchRuntime = project.configurations.create(RUNTIME_CONFIG) {
            attributes {
                attribute(Attribute.of("org.gradle.usage", String::class.java), "java-runtime")
                attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java), "jvm")
            }
        }
        project.dependencies {
            add(testBenchRuntime.name, getComposeRuntimeForHost())
            add(testBenchRuntime.name, "org.drewcarlson.testbench:desktop:${BuildConfig.VERSION}")
            pluginProjects.forEach { pluginProject ->
                add(testBenchRuntime.name, pluginProject.desktop)
            }
        }
    }

    private fun getComposeRuntimeForHost(): String {
        val os = DefaultNativePlatform.getCurrentOperatingSystem()
        val artifactVariant = when {
            os.isWindows -> "windows-x64"

            os.isLinux -> if (DefaultNativePlatform.getCurrentArchitecture().isArm) {
                "linux-arm64"
            } else {
                "linux-x64"
            }

            os.isMacOsX -> if (DefaultNativePlatform.getCurrentArchitecture().isArm) {
                "macos-arm64"
            } else {
                "macos-x64"
            }

            else -> error("Unsupported operating system: $os")
        }
        return "org.jetbrains.compose.desktop:desktop-jvm-$artifactVariant:${BuildConfig.COMPOSE_VERSION}"
    }
}
