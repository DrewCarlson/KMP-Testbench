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
                implementation(projects.plugins.network.coreNetwork)
                implementation(projects.pluginToolkitCore)
                implementation(projects.pluginToolkitClient)

                implementation(libs.ktor.client.core)

                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
            }
        }
    }
}