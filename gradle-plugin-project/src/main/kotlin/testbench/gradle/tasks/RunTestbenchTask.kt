package testbench.gradle.tasks

import org.gradle.api.internal.provider.AbstractProperty.PropertyQueryException
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.support.serviceOf
import testbench.gradle.RUNTIME_CONFIG

private const val DESKTOP_MAIN = "testbench.desktop.MainKt"

@Suppress("LeakingThis", "UsePropertyAccessSyntax")
public abstract class RunTestbenchTask : JavaExec() {
    init {
        description = "Run the testbench desktop application"
        group = "run"

        val javaToolchains = project.serviceOf<JavaToolchainService>()
        mainClass.set(DESKTOP_MAIN)
        javaLauncher.set(
            javaToolchains.launcherFor {
                vendor.set(JvmVendorSpec.JETBRAINS)
                languageVersion.set(JavaLanguageVersion.of(17))
            },
        )
        val launcherBin = try {
            javaLauncher.map { it.executablePath.asFile.absolutePath }.orNull
        } catch (e: PropertyQueryException) {
            project.logger.warn("Jetbrains Runtime 17 not found, disabling testbench.")
            null
        }
        if (launcherBin == null) {
            enabled = false
        } else {
            setExecutable(launcherBin)
            classpath(project.configurations.getByName(RUNTIME_CONFIG))
        }
    }
}
