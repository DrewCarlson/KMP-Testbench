plugins {
    alias(libs.plugins.jvm)
    `java-gradle-plugin`
    `kotlin-dsl`
}

kotlin {
    explicitApi()
}

dependencies {
    // TODO: These should be compileOnly dependencies but for some reason,
    //  likely the composite build setup, they must be provided via this
    //  gradle-plugin project.
    implementation(libs.android.gradlePlugin)
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
