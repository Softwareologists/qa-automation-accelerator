# QA Automation Accelerator



**QA Automation Accelerator** is a Kotlin-based desktop and CLI tool that transforms manual QA workflows into automated, reproducible test flows. It captures HTTP interactions and file I/O events against your Spring Boot or .NET application JAR/DDl/EXE, then replays them to validate future builds.

## Key Features

- **Unified Launcher**: One-click record and playback via GUI (Kotlin Compose) or headless CLI (Clikt).
- **Purpose-Built Sandbox**: Lightweight HTTP and file I/O emulators—no Docker or VM required.
- **Extensible Plugin SPI**: Easily add new launchers, emulators, or database providers.
- **Flow Definitions**: YAML-based flows with JSON-Schema validation for accuracy.
- **Branch Management**: Create variants at any step to test alternate scenarios.
- **Evidence Collection**: Generate HTTP HARs, file event logs, database dumps, JUnit-XML, and HTML reports.
- **CI/CD Ready**: Automated builds, sample flow runs, and report publishing via GitHub Actions.

## Getting Started

### Prerequisites

- **Java 21+**
- **Gradle 7.5+** (wrapper included)
- **Kotlin 2.2+** (via Gradle toolchain)
- **Windows 10/11, macOS 11+, or Linux**

### Installation

```bash
git clone git@github.com:your-org/qa-automation-accelerator.git
cd qa-automation-accelerator
./gradlew clean assemble
```

### Running the GUI Launcher

1. Build the application:
   ```bash
   ./gradlew :app:installDist
   ```
2. Launch:
   - **macOS/Linux**: `app/build/install/app/bin/app`
   - **Windows**: `app\build\install\app\bin\app.bat`

### Using the CLI

Record a new flow:

```bash
./gradlew run --args="record --output examples/sample-flow.yaml"
```

Replay a flow:

```bash
./gradlew run --args="replay --flow examples/sample-flow.yaml --reportDir reports/"
```

The report directory will contain `junit.xml` and `summary.html` alongside JSON
evidence files.

Create a branch from an existing flow:

```bash
./gradlew run --args="branch create --base examples/sample-flow.yaml --at 1 --name variant"
```

List available commands:

```bash
./gradlew run --args="--help"
```

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

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on reporting issues, branching, and submitting pull requests.

## License

This project is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.

---

*Happy testing!*

