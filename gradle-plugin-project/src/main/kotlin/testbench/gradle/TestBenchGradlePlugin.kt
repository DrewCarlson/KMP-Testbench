package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import testbench.gradle.plugins.configureCustomPlugins

private const val RUNTIME_CONFIG = "testBenchRuntime"
private const val RUN_TASK_NAME = "runTestBench"
private const val DESKTOP_MAIN = "testbench.desktop.MainKt"

public open class TestBenchGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("testbench", TestBenchExtension::class.java)
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
            add(testBenchRuntime.name, "org.drewcarlson.testbench:desktop:${BuildConfig.VERSION}")
            pluginProjects.forEach { pluginProject ->
                add(testBenchRuntime.name, pluginProject.desktop)
            }
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
            add(
                testBenchRuntime.name,
                "org.jetbrains.compose.desktop:desktop-jvm-$artifactVariant:${BuildConfig.COMPOSE_VERSION}",
            )
        }

        project.tasks.create(RUN_TASK_NAME, JavaExec::class.java) {
            val javaToolchains = project.serviceOf<JavaToolchainService>()
            mainClass.set(DESKTOP_MAIN)
            javaLauncher.set(
                javaToolchains.launcherFor {
                    vendor.set(JvmVendorSpec.JETBRAINS)
                    languageVersion.set(JavaLanguageVersion.of(17))
                },
            )
            setExecutable(javaLauncher.map { it.executablePath.asFile.absolutePath }.get())
            classpath(testBenchRuntime)
        }
    }
}
