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
        substitute(module("org.drewcarlson:gradle-plugin")).using(project(":"))
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    }
}

plugins {
    id("org.drewcarlson.kmp-test-bench-settings")
}

testbench {
    includePlugin(":plugins:logs")
    includePlugin(":plugins:databases")
    includePlugin(":plugins:preferences")
    includePlugin(":plugins:network") {
        clientVariations("ktor")
    }
}
