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
    ":plugins:network:client",
    ":plugins:network:core-network",
    ":plugins:network:server",
)

includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("org.drewcarlson:gradle-plugin")).using(project(":"))
    }
}
