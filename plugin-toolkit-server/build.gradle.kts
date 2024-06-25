plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                api(compose.ui)
                api(projects.pluginToolkitCore)
                api(libs.jewel)
            }
        }
    }
}