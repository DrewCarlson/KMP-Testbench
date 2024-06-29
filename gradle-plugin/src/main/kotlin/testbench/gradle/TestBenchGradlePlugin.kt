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

public class TestBenchGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pluginProjects = configureCustomPlugins(project)

        val testBenchRuntime = project.configurations.create("testBenchRuntime") {
            attributes {
                attribute(Attribute.of("org.gradle.usage", String::class.java), "java-runtime")
                attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String::class.java), "jvm")
            }
        }
        project.dependencies {
            // todo: specify dependency version
            add(testBenchRuntime.name, "build.wallet.testbench:desktop")
            pluginProjects.forEach { pluginProject ->
                add(testBenchRuntime.name, pluginProject.server)
            }
        }

        project.tasks.create("runTestBench", JavaExec::class.java) {
            val javaToolchains = project.serviceOf<JavaToolchainService>()
            mainClass.set("testbench.MainKt")
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
