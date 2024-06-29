package testbench.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import testbench.gradle.plugins.configureCustomPlugins

public class TestBenchGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        configureCustomPlugins(project)
    }
}
