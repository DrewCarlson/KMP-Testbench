plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jetbrains)
    id("publish-library")
}

kotlin {
    sourceSets {
        all {
            explicitApi()
        }
    }
}

dependencies {
    api(projects.pluginToolkitCore)

    api(libs.serialization.json)
    api(libs.datetime)

    api(libs.jewel)
    api(compose.ui)
    api(compose.desktop.common) {
        exclude(group = "org.jetbrains.compose.material")
    }
}
