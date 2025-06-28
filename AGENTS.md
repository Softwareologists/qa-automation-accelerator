# AI Agents Guide for QA Helper Project

This document defines conventions, workflows, and guidelines for AI agents contributing to the QA Helper codebase. It assumes the presence of:

- **Technical Specification** (`core/src/main/resources/flow.schema.yaml`, **QA Helper Technical Specification**)
- **Implementation Plan & Guidelines** (`QA Helper Implementation Plan & Guidelines`)

Use this guide to generate code, open pull requests, and interact with the repository in an automated, consistent manner.

---

## 1. Agent Roles & Responsibilities

### 1.1 Code Generation Agent

- **Purpose:** Scaffold modules, implement SPI, plugins, CLI commands, and UI.
- **Input:** Task description from Implementation Plan & Guidelines.
- **Output:** Pull request with code, tests, and documentation.

### 1.2 Documentation Agent

- **Purpose:** Generate and update markdown files (`README.md`, `DEVELOPER.md`, `AGENTS.md`).
- **Input:** Specification and project code structure.
- **Output:** Well-formatted docs, link checks, code examples.

### 1.3 CI/CD Agent

- **Purpose:** Create and maintain CI workflows, Docker/Jib configurations, and test pipelines.
- **Input:** Project modules and testing tools.
- **Output:** `.github/workflows/ci.yml`, Dockerfiles, build badges.

### 1.4 Testing Agent

- **Purpose:** Write unit and integration tests for each module.
- **Input:** Implemented code and SPI definitions.
- **Output:** JUnit 5 tests, schema validation tests, emulator behavior tests.

---

## 2. Pull Request Workflow

1. **Task Assignment**: Read a single task from the Implementation Plan & Guidelines.
2. **Branching**:
   - Use prefix: `phaseX/task-description`, e.g. `core/spi-interfaces`
3. **Commit Messages**:
   - Format: `<type>(<scope>): <brief description>`
   - Types: `feat`, `fix`, `chore`, `docs`, `build`, `ci`
4. **Pull Request**:
   - Title: `<type>: <short summary>`
   - Description:
     - Task reference (e.g., `Closes phase2/task 2.1`).
     - Summary of changes.
     - Testing instructions.
5. **Validation**:
   - Confirm branch builds (`./gradlew build`).
   - Run tests (`./gradlew test`).
   - Ensure no lint errors (if applicable).
6. **Review**:
   - Agents should auto-assign reviewers if configured.
   - Tag `@maintainers` for final approval.

---

## 3. Coding Conventions

- **Kotlin Style**: Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- **Package Structure**:
  - `tech.softwareologists.qa.core` (core SPI & executor).
  - `tech.softwareologists.qa.http` (HTTP emulator).
  - `tech.softwareologists.qa.fileio` (File I/O emulator).
  - `tech.softwareologists.qa.app` (CLI & UI).
- **Testing**:
  - Use JUnit 5 with Kotlin Test (`kotlin.test`).
  - Mocks can use MockK or Mockito-Kotlin.
  - Name tests `XxxTest.kt`.
- **Dependencies**:
  - Prefer stable versions listed in `libs.versions.toml`.
  - Do not introduce new repositories without approval.

---

## 4. Issue & Task Tracking

- **Implementation Plan** tasks map to GitHub issues.
- Agents should reference issue numbers in PR descriptions.
- Close issues automatically by including `Closes #<issue>` in commits.

---

## 5. Reporting & Metrics

- **CI Status**: Agents should monitor build badges in `README.md`.
- **Test Coverage**: Generate coverage reports (Jacoco) and publish as artifact.
- **Quality Gates**: Enforce no-critical warnings.

---

## 6. Agent Configuration

- **Environment**:
  - JDK 17+, Gradle 7+, Kotlin 1.8.
  - Access to GitHub repository with write permissions.
- **Tools**:
  - OpenAI or equivalent LLM with code-generation capabilities.
  - GitHub CLI for PR creation: `gh pr create --fill`.
- **Secrets**:
  - Store tokens securely; do not hard-code.

---

*End of AGENTS.md*

