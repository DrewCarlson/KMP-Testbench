plugins {
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
                api(projects.pluginToolkitCore)
                api(projects.pluginToolkitClient)

                implementation(libs.ktor.client.core)

                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }
    }
}
