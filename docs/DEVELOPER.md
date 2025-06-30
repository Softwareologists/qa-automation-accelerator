# Developer Guide

This document explains the overall module layout, available SPIs and how to
extend them, and the basic testing approach for the project.

## Module Structure

The project is organized as a multi module Gradle build. The root
`settings.gradle.kts` lists all included modules:

```kotlin
include("core")
include("plugins:http-emulator")
include("plugins:fileio-emulator")
include("plugins:database-manager-h2")
include("plugins:database-manager-jdbc")
include("plugins:database-manager-sqlserver")
include("app")
```

- **core** – runtime engine and SPI definitions. It contains the `FlowExecutor`,
  YAML I/O utilities and the `PluginRegistry` used to discover implementations.
- **plugins/** – a collection of plugin modules implementing the SPIs. Current
  plugins provide HTTP and file emulators along with several database managers.
- **app** – the end user interface built with Clikt for the CLI and Kotlin
  Compose for the desktop UI.

## SPI Extension Points

All extensible behaviours are defined in the `core` module under
`tech.softwareologists.qa.core`:

- `HttpEmulator` – records and replays HTTP calls.
- `FileIoEmulator` – monitors file system events.
- `LauncherPlugin` – launches the system under test.
- `DatabaseManager` – provisions and exports sandbox databases.

A new plugin implements one of these interfaces and registers itself via the
Java `ServiceLoader`. Each plugin module provides a service descriptor under
`META-INF/services`, for example:

```
plugins/http-emulator/src/main/resources/
  META-INF/services/tech.softwareologists.qa.core.HttpEmulator
```

which contains the implementation class name.
`PluginRegistry` loads these descriptors so the core executor can locate all
available plugins at runtime.

## Testing Guidelines

- Use **JUnit 5** with `kotlin.test` assertions. Unit tests live under
  `src/test/kotlin` and mirror the production packages.
- Name test classes with the `*Test.kt` suffix and use descriptive method names
  such as `should_record_http_interaction`.
- Integration tests may be placed under `src/integrationTest/kotlin` when a
  module requires end‑to‑end verification.
- Execute `./gradlew clean test` before committing changes. Lint checks are run
  with `./gradlew ktlintCheck` and `./gradlew detekt`.

Following these conventions ensures new modules and plugins are consistent and
well covered by automated tests.
