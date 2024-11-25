plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    // alias(libs.plugins.compose.hotReload)
    alias(libs.plugins.serialization)
    id("publish-library")
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        optIn.add("androidx.compose.foundation.ExperimentalFoundationApi")
    }
}

dependencies {
    implementation(projects.pluginToolkitUi)
    implementation(projects.pluginToolkitDesktop)
    implementation(projects.plugins.network.networkDesktop)
    implementation(projects.plugins.logs.logsDesktop)
    implementation(projects.plugins.preferences.preferencesDesktop)
    implementation(projects.plugins.databases.databasesDesktop)

    implementation(compose.desktop.common) {
        exclude(group = "org.jetbrains.compose.material")
    }

    implementation(libs.coroutines.core)

    implementation(libs.serialization.core)
    implementation(libs.serialization.json)

    implementation(libs.datetime)

    // Ktor Server
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.callLogging)
}

compose.desktop {
    application {
        mainClass = "testbench.desktop.MainKt"
    }
}

tasks.withType<JavaExec> {
    afterEvaluate {
        dependencies.implementation(dependencies.compose.desktop.currentOs)
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
        }
        jvmArgs("-Xmx2048m")
        setExecutable(javaLauncher.map { it.executablePath.asFile.absolutePath }.get())
    }
}
