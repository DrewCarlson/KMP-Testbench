# Getting Started

## Installation

Include the Desktop application plugin in your root `build.gradle.kts`:

```kotlin
plugins {
  id("build.wallet.kmp-test-bench") version "{{ lib_version }}"
}

testbench {
  // ...
}
```

(optional) Add Custom bench plugins in `settings.gradle.kts`

```
plugins {
  id("build.wallet.kmp-test-bench-settings") version "{{ lib_version }}"
}

testbench {
  includePlugin(":plugins:logs")
  includePlugin(":plugins:databases")
  includePlugin(":plugins:preferences")
  includePlugin(":plugins:network") {
      clientVariations("ktor", "okhttp", "platform")
  }
}
```
