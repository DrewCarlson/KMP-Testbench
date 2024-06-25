plugins {
    alias(libs.plugins.jvm)
    `java-gradle-plugin`
}

kotlin {
    explicitApi()
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.stdlib)
}

gradlePlugin {
    plugins {
        create("testBenchPlugin") {
            id = "build.wallet.kmp-test-bench"
            implementationClass = "testbench.gradle.TestBenchGradlePlugin"
        }
    }
}
