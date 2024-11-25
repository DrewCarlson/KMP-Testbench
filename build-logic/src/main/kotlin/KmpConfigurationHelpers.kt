import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.allTargets(cmpOnly: Boolean = false) {
    applyDefaultHierarchyTemplate()

    jvm()
    androidTarget {
        publishAllLibraryVariants()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    if (!cmpOnly) {
        js(IR) {
            browser()
            nodejs()
        }

        macosX64()
        macosArm64()

        mingwX64()

        // linuxArm64()
        linuxX64()

        tvosX64()
        tvosArm64()
        tvosSimulatorArm64()

        watchosArm32()
        watchosArm64()
        watchosSimulatorArm64()
        watchosX64()
    }
}
