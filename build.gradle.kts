plugins {
    kotlin("multiplatform") apply false
    kotlin("jvm") apply false
    id("com.android.library") apply false
    alias(libs.plugins.compose.jetbrains) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryCompat) apply false
    id("org.drewcarlson.testbench")
}

subprojects {
    System
        .getenv("GITHUB_REF_NAME")
        ?.takeIf { it.startsWith("v") }
        ?.let { version = it.removePrefix("v") }
}

subprojects {
    configurations.configureEach {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.drewcarlson.testbench:plugin-toolkit-core"))
                .using(project(":plugin-toolkit-core"))
            substitute(module("org.drewcarlson.testbench:plugin-toolkit-client"))
                .using(project(":plugin-toolkit-client"))
            substitute(module("org.drewcarlson.testbench:plugin-toolkit-desktop"))
                .using(project(":plugin-toolkit-desktop"))
            substitute(module("org.drewcarlson.testbench:service-compiler-plugin"))
                .using(project(":service-compiler-plugin"))
        }
    }
}
