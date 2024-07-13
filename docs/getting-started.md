# Getting Started

## Installation

Include the Desktop application plugin in your root `build.gradle.kts`:

```kotlin
plugins {
  id("org.drewcarlson.testbench") version "{{ lib_version }}"
}
```

Optional configuration:
```kotlin
testbench {
  // ...
}
```

## Run Testbench

Launch the desktop app with the `runTestbench` task.

```bash
./gradlew runTestbench
```

You should see an idle testbench window:

![](img/screenshot-empty.png)
