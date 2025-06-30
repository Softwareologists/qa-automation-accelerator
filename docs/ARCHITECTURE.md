# Architecture Overview

This document provides a high‑level view of the QA Helper architecture, including module boundaries, data flow between components, and primary extension points. It is intended to help new developers and AI agents quickly understand the system organization.

---

## 1. Module Breakdown

```
+----------------------+           +------------------+
|    Desktop UI / CLI  |           |    REST API      |
|   (Kotlin Compose /  |           |(Spring WebFlux)  |
|      Clikt CLI)      |           +------------------+
+----------+-----------+                    |
           |                                |
           | Orchestration                  | Agent/Webhook
           v                                v
+----------------------+    SPI   +-----------------------+
|      Core Module     |<-------->|   Plugin Registry     |
| (FlowExecutor, SPI)  |         +--+--------------------+
+----+--------------+--+            |  |    |    |
     |              |               |  |    |    +----------------+
     |              |               |  |    |                     |
     v              v               v  v    v                     v
+----+--+       +---+---+       +---+---+ +--+--+     +----------+-----------+
| HTTP   |       | File   |     | Launch | | DB  |     |  Additional Plugins  |
|Emulator|       |I/O     |     |Plugin | |Mgr |     | (Custom emulators,    |
|Plugin  |       |Emulator|     |(JAR/ .NET)|Plugin|     |  analytics, reporting)|
+--------+       +--------+     +---------+ +-----+     +----------------------+

Legend:
- Boxes: modules or plugins
- Arrows: runtime interactions
- SPI: ServiceLoader–based extension point
```

### 1.1 Desktop UI / CLI (`app`)

- **Responsibilities:**
  - Present unified launcher UI (Kotlin Compose) and CLI (Clikt).
  - Accept user inputs: select JAR/exe, configure env, start/stop recording or replay.
  - Display status, logs, and reports.

### 1.2 REST API (`agent`)

- **Responsibilities:**
  - Expose HTTP endpoints for agent integration: import flows, trigger runs, retrieve results.
  - Support webhooks to notify external systems when runs complete.

### 1.3 Core Module (`core`)

- **FlowExecutor:** Orchestrates recording and replay lifecycles:
  - Starts and stops emulator plugins and database manager.
  - Launches SUT via `LauncherPlugin`.
  - Serializes and deserializes flows (YAML ⟷ Kotlin data models).
  - Validates flows against JSON-Schema.

### 1.4 Plugin Registry (`core`)

- **Responsibilities:**
  - Discover and load implementations of SPI interfaces at runtime.
  - Provide lookup APIs for each plugin type: `HttpEmulator`, `FileIoEmulator`, `LauncherPlugin`, `DatabaseManager`.

### 1.5 Plugins (`plugins/*`)

- **HTTP Emulator:** Captures HTTP requests and serves stubbed responses.
- **File I/O Emulator:** Watches file system events in configured directories.
- **Database Manager:** Starts sandbox databases and exports dumps.
- **Launcher Plugins:** Supports launching Spring Boot JARs or .NET executables.
- **Additional Plugins:** Any future emulators or extensions (e.g., analytics, custom protocols).

---

## 2. Data Flow

1. **User Action** – QA selects mode (record/replay) in UI or CLI.
2. **Configuration** – UI/CLI collects configuration and passes to `FlowExecutor`.
3. **Plugin Startup** – `FlowExecutor` invokes:
   - `HttpEmulator.start()` → returns base URL for SUT.
   - `FileIoEmulator.watch()` → begins monitoring directories.
   - `DatabaseManager.startDatabase()` → provisions sandbox DB.
4. **SUT Launch** – `FlowExecutor` calls `LauncherPlugin.launch()` with injected properties:
   - `external.api.baseUrl` pointing to `HttpEmulator`.
   - `application.upload.dir` pointing to watched folder.
   - `spring.datasource.*` pointing to sandbox DB.
5. **Recording** – As SUT runs in record mode:
   - HTTP requests flow through the emulator and are logged.
   - File events are captured by the file emulator.
6. **Serialization** – On stop:
   - Core serializes all captured interactions and file events into a YAML flow file.
7. **Playback** – In replay mode:
   - Recorded HTTP steps converted to stub mappings for `HttpEmulator`.
   - File events replayed by creating/modifying/deleting files.
   - SUT runs against these stubs; `FlowExecutor` verifies actual calls match expectations.
8. **Reporting** – `FlowExecutor` gathers evidence:
   - HTTP interactions, file event logs, DB dump, HAR file.
   - Generates `result.json`, JUnit-XML, and HTML reports.
9. **Agent Notification** – If invoked via REST API, notify subscribed agents or CI via webhook.

---

## 3. Extension Points

All major behaviors are pluggable via SPI interfaces:

| Interface         | Purpose                                     | Default Implementation      |
| ----------------- | ------------------------------------------- | --------------------------- |
| `LauncherPlugin`  | Launches the SUT (JAR or executable)        | `JarLauncherPlugin`, `.NET` |
| `HttpEmulator`    | Records/stubs HTTP calls                    | `KtorHttpEmulator`          |
| `FileIoEmulator`  | Monitors and replays file system events     | `NioFileIoEmulator`         |
| `DatabaseManager` | Provisions sandbox database & exports dumps | `H2DatabaseManager`         |

Adding a new emulator or launcher simply requires:

1. Implementing the SPI interface in a new module.
2. Declaring that implementation under `META-INF/services`.
3. Packaging the JAR on the classpath.

---

*End of Architecture Overview*

