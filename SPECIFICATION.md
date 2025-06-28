# QA Helper Technical Specification

**Version:** 1.0\
**Date:** 2025-06-27

---

## 1. Overview

The **QA Helper** is a standalone desktop application—built with Kotlin Compose—that empowers QA engineers to:

- Launch and configure Spring Boot or .NET JARs.
- Bulk-load test data via REST/OpenAPI endpoints.
- Record manual workflows, capturing HTTP interactions and file I/O events.
- Replay recorded flows against new application versions.
- Collect evidence (logs, HTTP HARs, file events, database dumps).
- Integrate seamlessly with CI/CD pipelines and automated agents.

**Key Principles:**

1. **Unified Launcher:** One-click start/stop recording and playback, no external setup required.
2. **Modular Extensions:** Plugin SPI for launchers, emulators, and databases.
3. **Human- and Machine-Friendly:** YAML/JSON flow definitions with strict schema validation.
4. **Dual Mode:** Interactive GUI for QA and headless CLI for CI/CD.
5. **Comprehensive Reporting:** Detailed JSON, JUnit‑XML, and HTML summaries.

---

## 2. Functional Requirements

### 2.1 Unified Launcher Application

- Provide a single desktop app (Kotlin Compose) where QA can:
  1. Select the application JAR (or DLL/exe).
  2. Edit environment variables, JVM/CLI arguments, and application properties.
  3. Click **Start Recording** to launch the sandbox and begin capturing.
  4. Click **Stop** (or let the app exit) to end recording and save the flow.

### 2.2 Launcher Plugin

- SPI interface `LauncherPlugin`:
  ```kotlin
  interface LauncherPlugin {
    fun supports(config: LaunchConfig): Boolean
    fun launch(config: LaunchConfig): Process
  }
  ```
- Implementations for:
  - Spring Boot JARs
  - .NET DLLs/EXEs

### 2.3 Configuration Manager

- Scan and present all `application.properties`/`.yml` files across modules (glob patterns).
- Provide form-based editing with autocompletion of known keys.
- Persist overrides in a local `override.properties` file.

### 2.4 Purpose-Built Sandbox Emulator

- **HTTP Emulator** (`HttpEmulator` SPI):
  - Embedded Ktor server capturing HTTP requests and returning stubbed responses.
  - Records `HttpInteraction(method, path, headers, body)`.
- **File I/O Emulator** (`FileIoEmulator` SPI):
  - Watches configured directories via Java NIO `WatchService`.
  - Records `FileEvent(type, path, timestamp)` for create, modify, delete, move.

### 2.5 Flow Recorder & Executor

- **Flow File Format** (`flow.schema.yaml`):
  - `version`, `appVersion`, `emulator.http`, `emulator.file`, and `steps`.
  - Strict JSON-Schema validation.
- **Recording Mode:**
  1. Launcher starts emulators.
  2. App runs with injected endpoints and watch directories.
  3. Emulators capture all interactions.
  4. Helper serializes emulator data and QA-defined steps into the flow file.
- **Playback Mode:**
  1. Launcher starts emulators with stub mappings from the flow.
  2. File emulator replays file events (creates, modifies, deletes files).
  3. App runs against stubs.
  4. Helper verifies that actual interactions match recorded sequences.

### 2.6 Branch Management

- Create variants by branching flows at any step.
- CLI: `branch create --base flow.yaml --at STEP_ID --name variant`.
- GUI: right-click on a node to branch.

### 2.7 Data & Evidence Collection

- **HTTP HAR** files and JSON lists of interactions.
- **File event** logs in JSON.
- **Database dump** (SQL or CSV) via `DatabaseManager` SPI.
- **Report Manifest** (`result.json`) summarizing pass/fail, timings, and mismatches.
- **JUnit-XML** and **HTML** exports for CI visibility.

---

## 3. Non-Functional Requirements

- **Cross-Platform:** Windows, macOS, Linux—desktop via Kotlin Compose.
- **Modular & Extensible:** SPI-based plugin architecture (launchers, emulators, databases).
- **Version-Controlled Flows:** Human-readable YAML stored alongside code in Git.
- **Secure:** Encrypted storage of credentials and properties.
- **Performant:** Parallel flow execution in CLI mode.
- **Resilient:** Timeouts and error-handling on emulator startup and SUT launch.

---

## 4. Agent Integration

- **REST API** (Spring WebFlux + OpenAPI):
  - `GET /flows` → list flows
  - `POST /flows` → import flow
  - `POST /flows/{id}/run` → execute flow
  - `GET /runs/{runId}` → fetch results
- **Webhooks:** Notify agents or CI when runs complete with payload of `result.json`.

---

## 5. Roadmap & Next Steps

1. Scaffold the multi-module Gradle project with Kotlin DSL.
2. Define and publish `flow.schema.yaml` in the core module.
3. Implement SPI interfaces and service-loader metadata.
4. Develop `http-emulator` and `fileio-emulator` plugins.
5. Build the Unified Launcher with Clikt CLI and Kotlin Compose UI.
6. Integrate `DatabaseManager` for sandboxed DB provisioning and dumps.
7. Add reporting (JUnit‑XML, HTML, JSON) and CI/CD pipelines.
8. Prepare example flows and stub JAR for end-to-end validation.

---

*End of QA Helper Technical Specification*

