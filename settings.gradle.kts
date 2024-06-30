import testbench.gradle.TestBenchGradleSettingsExtension

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    includeBuild("build-logic")
    includeBuild("gradle-plugin")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "KMP-Test-Bench"

include(
    ":demo-app",
    ":desktop",
    ":client-core",
    ":plugin-toolkit-core",
    ":plugin-toolkit-client",
    ":plugin-toolkit-server",
    ":plugins",
    ":service-compiler-plugin",
)

includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("build.wallet:gradle-plugin")).using(project(":"))
    }
}

plugins {
    id("build.wallet.kmp-test-bench-settings")
}

configure<TestBenchGradleSettingsExtension> {
    includePlugin(":plugins:logs")
    includePlugin(":plugins:databases")
    includePlugin(":plugins:preferences")
    includePlugin(":plugins:network") {
        clientVariations("ktor")
    }
}
