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
        mavenCentral()
    }
}
