# Contributing to QA Helper

Thank you for your interest in contributing to the QA Helper project! Your feedback, bug reports, and code contributions help us improve the tool and better support our QA engineers.

## Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [How to Report Bugs](#how-to-report-bugs)
4. [Feature Requests](#feature-requests)
5. [Development Workflow](#development-workflow)
   - [Branching Strategy](#branching-strategy)
   - [Commit Messages](#commit-messages)
   - [Pull Requests](#pull-requests)
6. [Testing](#testing)
7. [Style Guidelines](#style-guidelines)
8. [Documentation](#documentation)
9. [Release Process](#release-process)
10. [Contact & Support](#contact--support)

---

## Code of Conduct
Please read our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before contributing. We expect all contributors to adhere to the guidelines and foster an inclusive community.

---

## Getting Started
1. Fork the repository and clone your fork:
   ```bash
   git clone git@github.com:your-org/qa-helper.git
   cd qa-helper
   ```
2. Install prerequisites:
   - JDK 17+
   - Gradle 7+
   - Kotlin 1.8 (via Gradle toolchain)
3. Open the project in your IDE (IntelliJ IDEA recommended) or VS Code with the provided devcontainer.

---

## How to Report Bugs
If you encounter any bugs or unexpected behavior:
1. Check existing issues to see if it’s already reported.
2. If not, open a new GitHub issue and include:
   - Steps to reproduce
   - Expected vs. actual behavior
   - Logs or stack traces (if applicable)
   - Platform (OS, Java version)

---

## Feature Requests
To suggest new features or improvements:
1. Search existing issues to avoid duplicates.
2. Open a new issue labeled **enhancement**.
3. Describe your use case, proposed API or UI changes, and any alternatives you’ve considered.

---

## Development Workflow
We follow the [Implementation Plan & Guidelines](/docs/QA%20Helper%20Implementation%20Plan%20&%20Guidelines.md) to break work into discrete tasks.

### Branching Strategy
- Create a topic branch off `main` for each task:  
  ```
  git checkout main
  git pull
  git checkout -b phaseX/task-description
  ```
- Branch names must follow: `phase{N}/{short-task}` (e.g., `core/spi-interfaces`).

### Commit Messages
- Use Conventional Commits format:  
  ```
  <type>(<scope>): <short summary>
  ```
- Types:
  - `feat`: new feature
  - `fix`: bug fix
  - `chore`: build or tooling changes
  - `docs`: documentation only
  - `test`: tests only
  - `ci`: CI configuration
- Include issue or task reference in the footer, e.g.:  
  ```
  Closes #123
  ```

### Pull Requests
1. Push your branch:  
   ```
   git push origin phaseX/task-description
   ```
2. Open a PR against `main` with:
   - Title: `<type>: <short summary>`
   - Description:
     - Task reference (e.g., `Closes phase2/task 2.1`).
     - Summary of changes.
     - Testing instructions.
3. Ensure CI passes and all tests are green.
4. Request reviews from maintainers (`@maintainers`).

---

## Testing
- Run all tests locally before submitting:  
  ```bash
  ./gradlew clean test
  ```
- Aim for high coverage; write unit and integration tests for new modules.
- Linting and static analysis run automatically in CI.

---

## Style Guidelines
- Follow the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Adhere to formatting rules enforced by `ktlint` and `detekt`.
- Keep methods short and focused; write clear, descriptive names.

---

## Documentation
- Update or add examples in `README.md` or `docs/` when introducing new features.
- Document extension points and SPIs in `docs/DEVELOPER.md`.

---

## Release Process
See [RELEASE.md](docs/RELEASE.md) for the full release checklist, including versioning, changelog updates, and publishing artifacts.

---

## Contact & Support
For questions or assistance, reach out to the project maintainers or open an issue on GitHub.

Thank you for helping us build a robust QA Helper! 🚀

