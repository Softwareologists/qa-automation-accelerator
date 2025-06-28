# ADR 0001: Initial Architecture for QA Helper

**Status:** Accepted

## Context

The QA Helper project requires a sandboxed environment to record and replay external interactions (HTTP and file I/O) for Spring Boot and .NET JARs. It must be:

- Zero-setup for QA (no extra container or VM tooling).
- Cross-platform (Windows, macOS, Linux).
- Extensible via plugins for launchers, emulators, and databases.
- Support both interactive GUI and headless CLI workflows.
- Produce machine- and human-readable flow definitions and reports.

Multiple design options were considered, including container-based sandboxes, microVMs, and custom emulators.

## Decision

We adopted a modular, Kotlin-centric architecture:

1. **Unified Launcher**  
   - A desktop app built with Kotlin Compose (and Clikt for CLI) provides a single entry point.  
2. **Plugin SPI Architecture**  
   - SPI interfaces (`LauncherPlugin`, `HttpEmulator`, `FileIoEmulator`, `DatabaseManager`) enable runtime discovery of implementations via `ServiceLoader`.  
3. **Purpose-Built Sandbox Emulator**  
   - Rather than containers or VMs, we implement lightweight emulators:
   - **HTTP Emulator**: Embedded Ktor server to record/stub HTTP interactions.  
   - **File I/O Emulator**: Java NIO `WatchService` to capture file events.
4. **Flow Specification**  
   - YAML-based flows with JSON-Schema validation for `version`, `appVersion`, `emulator`, and `steps`.  
5. **CLI & GUI**  
   - Use Clikt for headless operations and Kotlin Compose Multiplatform for the desktop UI.  
6. **Evidence Reporting**  
   - JSON, JUnit-XML, and HTML reports, plus database dumps via an H2-based `DatabaseManager` plugin.

## Consequences

### Positive
- **Zero External Dependencies**: QA only needs the QA Helper executable, no Docker/VMs.  
- **Extensibility**: New launcher or emulator types can be added without core changes.  
- **Cross-Platform GUI**: Kotlin Compose ensures native look-and-feel across OSes.  
- **Clear Schema**: JSON-Schema enforces flow correctness at load time.

### Negative / Trade-offs
- **Reinventing Sandbox Constructs**: Custom emulator may lack full fidelity compared to OS-level isolation.  
- **Maturity of Compose Multiplatform**: Desktop support is still evolving; may encounter UI quirks.  
- **Plugin Management Simplicity**: Using `ServiceLoader` over more advanced frameworks (OSGi) limits dynamic reloading.

### Future Considerations
- Evaluate adoption of a dedicated plugin framework for runtime isolation.  
- Explore deep integration with eBPF for advanced file and network tracing.  
- Monitor Kotlin Compose development for stability and feature enhancements.

