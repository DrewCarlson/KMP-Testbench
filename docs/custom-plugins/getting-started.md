# Getting Started

Testbench makes it easy to define custom plugins and include them when running the desktop app.


## Installation

Add the plugin-toolkit Gradle plugin and configure custom plugins in `settings.gradle.kts`:

```kotlin
plugins {
  id("org.drewcarlson.testbench.plugin-toolkit") version "{{ lib_version }}"
}

testbench {
  registerPlugin(":plugins:my-plugin") {
    // optional: Create multiple client implementation variations
    clientVariations("ktor", "okhttp", "fuel")
    // optional: Create only a desktop module without client or core modules
    desktopOnly()
    // optional: Enable to configure the desktop module with Kotlin Multiplatform instead of Kotlin JVM
    desktopUseKmp = true
  }
}
```

## Next Steps

For next steps, see [Plugin Basics](plugin-basics.md).
