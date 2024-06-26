pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
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
    ":plugins:network:network-core",
    ":plugins:network:network-server",
    ":plugins:network:network-client-ktor",
)

includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("build.wallet:gradle-plugin")).using(project(":"))
    }
}
