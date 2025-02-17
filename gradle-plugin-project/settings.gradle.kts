pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        maybeCreate("libs").from(files("../gradle/libs.versions.toml"))
    }
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "gradle-plugin-project"
