plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jetbrains)
    id("publish-library")
}

kotlin {
    jvm()

    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                api(projects.pluginToolkitCore)

                api(libs.serialization.json)

                api(libs.jewel)
                api(compose.ui)
                api(compose.desktop.common) {
                    exclude(group = "org.jetbrains.compose.material")
                }
            }
        }
    }
}
