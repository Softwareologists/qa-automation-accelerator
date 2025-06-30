# Installation Guide

This guide explains how to install **QA Automation Accelerator** on your system.

## Prerequisites

- **JDK 21+**
- **Gradle 7.5+** (wrapper included)
- **Kotlin 2.2+** (handled by Gradle)
- **Windows 10/11**, **macOS 11+**, or **Linux**

## Cloning the Repository

```bash
git clone git@github.com:your-org/qa-automation-accelerator.git
cd qa-automation-accelerator
```

## Building the Project

Use the Gradle wrapper to compile the entire project:

```bash
./gradlew clean assemble
```

This command fetches dependencies and creates artifacts under `build/`.

## Running the Application

To run the desktop UI and CLI, install the distribution:

```bash
./gradlew :app:installDist
```

The launch scripts are placed under `app/build/install/app/bin/`.

- **macOS/Linux**: `app/build/install/app/bin/app`
- **Windows**: `app\build\install\app\bin\app.bat`

You are now ready to launch the UI or run commands via the CLI.
