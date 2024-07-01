# Custom Plugins

## Installation

Add the plugin toolkit gradle plugin to `settings.gradle.kts`

```kotlin
plugins {
  id("org.drewcarlson.testbench.plugin-toolkit") version "{{ lib_version }}"
}
```

## Define Plugins

Define custom plugins in `settings.gradle.kts`:

```kotlin
testbench {
  // Plugin with client and server implementation
  includePlugin(":plugins:my-plugin")

  // Plugin with server only implementation
  includePlugin(":plugin:my-server-plugin") {
    desktopOnly = true
  }

  // Plugin with different client implementations
  includePlugin(":plugin:my-plugin") {
    clientVariations("a", "b")
  }
}
```
