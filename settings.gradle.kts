pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    includeBuild("build-logic")
    includeBuild("gradle-plugin-settings")
    includeBuild("gradle-plugin-project")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "KMP-Testbench"

include(
    ":demo-app",
    ":desktop",
    ":client-core",
    ":plugin-toolkit-core",
    ":plugin-toolkit-client",
    ":plugin-toolkit-desktop",
    ":plugin-toolkit-ui",
    ":plugins",
    ":service-compiler-plugin",
)

includeBuild("gradle-plugin-settings") {
    dependencySubstitution {
        substitute(module("org.drewcarlson.testbench:gradle-plugin-settings")).using(project(":"))
    }
}
includeBuild("gradle-plugin-project") {
    dependencySubstitution {
        substitute(module("org.drewcarlson.testbench:gradle-plugin-project")).using(project(":"))
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("org.drewcarlson.testbench.plugin-toolkit")
}

testbench {
    includePlugin(":plugins:logs")
    includePlugin(":plugins:databases")
    includePlugin(":plugins:preferences")
    includePlugin(":plugins:network") {
        clientVariations("ktor")
        clientVariations("okhttp")
    }
}
