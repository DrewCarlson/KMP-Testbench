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
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.okhttp)

    implementation(libs.serialization.core)
    implementation(libs.serialization.json)

    implementation(projects.clientCore)
    implementation(projects.plugins.network.networkClientKtor)
    implementation(projects.plugins.network.networkClientOkhttp)

    implementation("org.drewcarlson:coingecko:1.0.0-rc01")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.13")
}

compose.desktop {
    application {
        mainClass = "demo.DemoAppKt"
    }
}
