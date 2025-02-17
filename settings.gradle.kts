pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
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
    ":integration-tests",
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
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
    }
}

plugins {
    id("org.drewcarlson.testbench.plugin-toolkit")
}

testbench {
    registerPlugin(":plugins:logs")
    registerPlugin(":plugins:databases")
    registerPlugin(":plugins:preferences")
    registerPlugin(":plugins:network") {
        clientVariations("ktor")
        clientVariations("okhttp")
    }
}
