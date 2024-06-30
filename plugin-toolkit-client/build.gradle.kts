plugins {
    id("all-targets")
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
