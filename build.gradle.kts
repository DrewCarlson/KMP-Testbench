plugins {
    kotlin("multiplatform") apply false
    kotlin("jvm") apply false
    alias(libs.plugins.compose.jetbrains) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.serialization) apply false
    id("build.wallet.kmp-test-bench")
}

buildscript {
    dependencies {
        classpath("build.wallet:gradle-plugin")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
        google()
    }
}

subprojects {
    configurations.configureEach {
        resolutionStrategy.dependencySubstitution {
            substitute(module("build.wallet.testbench:plugin-toolkit-core"))
                .using(project(":plugin-toolkit-core"))
            substitute(module("build.wallet.testbench:plugin-toolkit-client"))
                .using(project(":plugin-toolkit-client"))
            substitute(module("build.wallet.testbench:plugin-toolkit-server"))
                .using(project(":plugin-toolkit-server"))
            substitute(module("build.wallet.testbench:service-compiler-plugin"))
                .using(project(":service-compiler-plugin"))
        }
    }
}
