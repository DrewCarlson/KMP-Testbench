plugins {
    alias(libs.plugins.jvm)
    `java-gradle-plugin`
    `kotlin-dsl`
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.stdlib)
}

gradlePlugin {
    plugins {
        create("testbenchPlugin") {
            id = "build.wallet.kmp-test-bench"
            implementationClass = "testbench.gradle.TestBenchGradlePlugin"
        }
        create("testbenchSettingsPlugin") {
            id = "build.wallet.kmp-test-bench-settings"
            implementationClass = "testbench.gradle.TestBenchGradleSettingsPlugin"
        }
    }
}
