plugins {
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.serialization.core)
                implementation(projects.pluginToolkitCore)
            }
        }
    }
}
