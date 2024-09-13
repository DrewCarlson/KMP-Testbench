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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
        maven("https://www.jetbrains.com/intellij-repository/releases")
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
    includePlugin(":plugins:compose-debug")
}
