# Architecture

## Modules

- `desktop`: The Compose Multiplatform Desktop application.

### SDK

- `client-core`: The client used to connect a running application with the Desktop app.
- `plugin-toolkit-core`: Common utilities for creating any kind of plugin.
- `plugin-toolkit-client`: Utilities for creating client specific plugins.
- `plugin-toolkit-desktop`: Utilities for creating Desktop app specific plugins.

### Gradle Plugins

- `gradle-plugin-project`: Gradle plugin used to include and run the Desktop app.
- `gradle-plugin-settings`: Gradle Settings plugin used to create and configure custom plugins.

### Kotlin Compiler Plugins

- `service-compiler-plugin`: Kotlin compiler plugin for generating ServiceLoader files for Desktop plugins.

### Testbench Plugins

- `plugins/databases`
- `plugins/logs`
- `plugins/network`
- `plugins/preferences`
