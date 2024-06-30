plugins {
    alias(libs.plugins.jvm)
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.gradleBuildConfig)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    explicitApi()
}

dependencies {
    if (gradle.parent == null) {
        compileOnly(libs.android.gradlePlugin)
        compileOnly(libs.kotlin.gradlePlugin)
    } else {
        implementation(libs.android.gradlePlugin)
        implementation(libs.kotlin.gradlePlugin)
    }
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.stdlib)
}

buildConfig {
    packageName("testbench.gradle")
    buildConfigField("VERSION", version.toString())
    buildConfigField("SERIALIZATION_VERSION", libs.versions.serialization)
}

gradlePlugin {
    plugins {
        create("testbenchPlugin") {
            id = "org.drewcarlson.kmp-test-bench"
            implementationClass = "testbench.gradle.TestBenchGradlePlugin"
        }
        create("testbenchSettingsPlugin") {
            id = "org.drewcarlson.kmp-test-bench-settings"
            implementationClass = "testbench.gradle.TestBenchGradleSettingsPlugin"
        }
    }
}
