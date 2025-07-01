# QA Automation Accelerator

[![CI](https://github.com/Softwareologists/qa-automation-accelerator/actions/workflows/ci.yml/badge.svg)](https://github.com/Softwareologists/qa-automation-accelerator/actions/workflows/ci.yml)
[![Release](https://github.com/Softwareologists/qa-automation-accelerator/actions/workflows/release.yml/badge.svg)](https://github.com/Softwareologists/qa-automation-accelerator/actions/workflows/release.yml)

**QA Automation Accelerator** is a Kotlin-based desktop and CLI tool that transforms manual QA workflows into automated, reproducible test flows. It captures HTTP interactions and file I/O events against your Spring Boot or .NET application JAR/DDl/EXE, then replays them to validate future builds.

## Key Features

- **Unified Launcher**: One-click record and playback via GUI (Kotlin Compose) or headless CLI (Clikt).
- **Purpose-Built Sandbox**: Lightweight HTTP and file I/O emulators—no Docker or VM required.
- **Extensible Plugin SPI**: Easily add new launchers, emulators, or database providers.
- **Flow Definitions**: YAML-based flows with JSON-Schema validation for accuracy.
- **Conditional Logic**: Execute steps conditionally and loop over actions for complex workflows.
- **Branch Management**: Create variants at any step to test alternate scenarios.
- **Evidence Collection**: Generate HTTP HARs, file event logs, database dumps, JUnit-XML, and HTML reports.
- **CI/CD Ready**: Automated builds, sample flow runs, and report publishing via GitHub Actions.

## Getting Started

### Prerequisites

- **Java 21+**
- **Gradle 7.5+** (wrapper included)
- **Kotlin 2.2+** (via Gradle toolchain)
- **Windows 10/11, macOS 11+, or Linux**

See [docs/INSTALLATION.md](docs/INSTALLATION.md) for detailed installation instructions.

### GUI Launcher
See [docs/UI_WALKTHROUGH.md](docs/UI_WALKTHROUGH.md) for a walkthrough of the desktop UI.

### GUI Quickstart

1. Build the distribution:

   ```bash
   gradle :app:installDist
   ```

2. Launch the application:

   ```bash
   app/build/install/app/bin/app
   ```

   On Windows use `app\build\install\app\bin\app.bat`.

3. In the main window:
   - Click **Browse** next to **Application JAR/DLL** and select the executable
     you want to test.
   - Optionally open a **Flow YAML** file to reuse or branch an existing flow.
   - Click **Start Recording** to capture interactions or choose **Run Flow**
     to replay the selected YAML.

### CLI Quickstart

See [docs/CLI_USAGE.md](docs/CLI_USAGE.md) for detailed command examples.

### Conditional Steps

Flows can include `if`/`then`/`else` blocks and `loop` directives. The
`FlowExecutor` evaluates these at runtime to support polling and cleanup logic.
## Examples

Sample workflows and reports are available under the `examples/` directory:

- `examples/sample-workflow.yaml` — demonstrates mixed HTTP and file events.
- `examples/reports/` — contains example HTML and JUnit reports.

## Documentation

- **Technical Spec:** `SPECIFICATION.md`
- **Implementation Plan:** `IMPLEMENTATION_PLAN.md`
- **Architecture:** `ARCHITECTURE.md`
- **Style Guide:** `STYLE_GUIDE.md`
- **CONTRIBUTING:** `CONTRIBUTING.md`
- **Code of Conduct:** `CODE_OF_CONDUCT.md`
- **ADR Templates:** `ADRs/decisions/`

## Contributing
See [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) for an overview of the contribution process.

## License

This project is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.

---

*Happy testing!*

