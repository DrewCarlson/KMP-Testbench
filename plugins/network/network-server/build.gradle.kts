plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                api(projects.plugins.network.networkCore)
                implementation(projects.pluginToolkitServer)

                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }
    }
}
