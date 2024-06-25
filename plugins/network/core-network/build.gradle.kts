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
                implementation(libs.serialization.core)
                implementation(projects.pluginToolkitCore)
            }
        }
    }
}