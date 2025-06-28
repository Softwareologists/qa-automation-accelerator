# STYLE_GUIDE.md

This document outlines coding style and formatting conventions for the QA Helper project. Adhering to these guidelines ensures consistency across modules and helps AI agents generate idiomatic, maintainable code.

---

## 1. Kotlin Code Style

### 1.1 Formatting
- **Tooling**: Use [ktlint](https://github.com/pinterest/ktlint) and [detekt](https://detekt.dev/) for auto-formatting and static analysis.  
- **Indentation**: 4 spaces (no tabs).  
- **Line Length**: 120 characters max; break long expressions into multiple lines.  
- **Braces**: Opening brace on same line for declarations and control structures:
  ```kotlin
  fun foo() {
      // good
  }
  ```
- **Imports**:
  - No wildcard imports: use explicit class imports.
  - Order: kotlin.*, java.*, third-party, project.
- **Trailing Commas**: Use trailing commas in multi-line collections and argument lists.

### 1.2 Naming Conventions
- **Packages**: `com.yourorg.qa.<module>` (all lowercase).  
- **Classes & Objects**: UpperCamelCase (e.g., `HttpEmulator`, `FlowExecutor`).  
- **Functions & Properties**: lowerCamelCase (e.g., `startEmulator()`, `flowSchema`).  
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_PORT`).  
- **Type Parameters**: Single uppercase letter (e.g., `T`, `R`).

### 1.3 Declarations
- **Visibility**: Default to `private` or `internal`; only expose `public` for SPI interfaces and module APIs.  
- **Data Classes**: Use for simple DTOs (e.g., `HttpInteraction`). Override `toString()` only if helpful.  
- **Companion Objects**: Minimize usage; place at top of class when needed.

### 1.4 Coroutines & Concurrency
- Use structured concurrency with `CoroutineScope` tied to lifecycle.  
- Prefer `withContext(Dispatchers.IO)` for blocking I/O in plugins.  
- Name coroutine dispatchers for clarity (e.g., `emulatorScope`).

---

## 2. YAML & JSON Schemas

### 2.1 YAML Formatting
- **Indentation**: 2 spaces per level.  
- **Anchors/Aliases**: Avoid unless DRY principles require.  
- **Key Order**: Follow schema order: `version`, `appVersion`, `emulator`, `steps`.  
- **Comments**: Use `#` sparingly to explain non-obvious fields.

### 2.2 JSON Schemas
- **Schema Files**: Store under `core/src/main/resources`.  
- **$id and $schema**: Include `$schema: "http://json-schema.org/draft-07/schema#"` at top.
- **Descriptions**: Provide `description` for all top-level properties.

---

## 3. CLI Conventions

- **Command Naming**: Use verbs (`run`, `record`, `replay`, `branch`) with subcommands for actions.  
- **Flags**: Long-form with single dash names only for single-letter flags; prefer `--long-name`.  
- **Help Text**: Every command and option must include descriptive `help` or `description`.

---

## 4. Documentation Style

- **Markdown**: Use `#` for titles, `##` for sections, `###` for subsections.  
- **Links**: Use relative links within the repo.  
- **Code Blocks**: Specify language (e.g., ```kotlin).  
- **Line Wrap**: Soft wrap at 80 characters in markdown for readability.

---

## 5. Testing Practices

- **Unit Tests**: One test class per production class; name `XxxTest.kt`.  
- **Integration Tests**: End-to-end tests under `src/integrationTest/kotlin`.  
- **Assertions**: Use `kotlin.test` or AssertJ for fluent assertions.  
- **Naming**: Test methods in `snake_case` or `camelCase` clearly describing the scenario (`should_record_http_interaction`).

---

## 6. Build & CI

- **Gradle**: Use Kotlin DSL (`build.gradle.kts`).  
- **Tasks**: Keep custom tasks minimal; name tasks with `qaHelper<Functionality>`.  
- **CI Linting**: Fail build on ktlint or detekt violations.

---

*End of STYLE_GUIDE.md*

