# Getting Started

## Installation

Include the Desktop application plugin in your root `build.gradle.kts`:

```kotlin
plugins {
  id("build.wallet.testbench") version "{{ lib_version }}"
}
```

Optional configuration:
```kotlin
testbench {
  // ...
}
```

## Run Test Bench

```bash
./gradlew runTestbench
```
