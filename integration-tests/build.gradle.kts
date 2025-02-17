plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        all {
            explicitApi()
        }

        test {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(projects.clientCore)
                implementation(projects.pluginToolkitCore)
                implementation(projects.pluginToolkitUi)
                implementation(projects.pluginToolkitClient)
                implementation(projects.pluginToolkitDesktop)
                implementation(projects.desktop)
                implementation(libs.coroutines.core)
                implementation(libs.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.ktor.client.cio)
                implementation(libs.serialization.json)
            }
        }
    }
}
