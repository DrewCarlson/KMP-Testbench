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
import testbench.gradle.plugins.configureCustomPlugins

private const val RUNTIME_CONFIG = "testBenchRuntime"
private const val RUN_TASK_NAME = "runTestBench"
private const val DESKTOP_MAIN = "testbench.MainKt"

public class TestBenchGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("testbench", TestBenchExtension::class.java)
        val pluginProjects = configureCustomPlugins(project)

        val testBenchRuntime = project.configurations.create(RUNTIME_CONFIG) {
            attributes {
                attribute(Attribute.of("org.gradle.usage", String::class.java), "java-runtime")
                attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java), "jvm")
            }
        }
        project.dependencies {
            add(testBenchRuntime.name, "org.drewcarlson:desktop:${BuildConfig.VERSION}")
            pluginProjects.forEach { pluginProject ->
                add(testBenchRuntime.name, pluginProject.server)
            }
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
