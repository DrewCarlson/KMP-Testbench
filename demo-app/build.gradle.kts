plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(libs.coroutines.core)
    implementation(libs.ktor.client.core)

    implementation(libs.serialization.core)
    implementation(libs.serialization.json)

    implementation(projects.clientCore)
    implementation(projects.plugins.network.networkClientKtor)

    implementation("org.drewcarlson:coingecko:1.0.0-rc01")
}

compose.desktop {
    application {
        mainClass = "demo.DemoAppKt"
    }
}
