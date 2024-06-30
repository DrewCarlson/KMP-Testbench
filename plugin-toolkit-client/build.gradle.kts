plugins {
    id("all-targets")
    alias(libs.plugins.mavenPublish)
}

kotlin {
    sourceSets {
        all {
            explicitApi()
        }

        commonMain {
            dependencies {
                api(projects.pluginToolkitCore)
            }
        }
    }
}
