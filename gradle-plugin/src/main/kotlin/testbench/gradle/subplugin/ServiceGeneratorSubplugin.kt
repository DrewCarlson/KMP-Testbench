package testbench.gradle.subplugin

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

public class ServiceGeneratorSubplugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = "build.wallet.service.compiler"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "build.wallet.testbench",
        artifactId = "service-compiler-plugin",
        version = null,
    )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val metaInfBuildDir = project.layout
            .buildDirectory
            .get()
            .asFile
            .resolve("classes/kotlin/jvm/main/META-INF/services")
            .absolutePath
        return project.provider {
            listOf(
                SubpluginOption(key = "meta_inf_build_dir", value = metaInfBuildDir),
            )
        }
    }
}
