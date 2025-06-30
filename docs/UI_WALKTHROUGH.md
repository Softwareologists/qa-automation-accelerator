# UI Walkthrough

The desktop interface is built with **Kotlin Compose** and allows recording and managing flows without using the command line.

## Launching the UI

After running the installation steps from [Installation Guide](INSTALLATION.md), start the UI with:

```bash
app/build/install/app/bin/app
```

On Windows use `app\build\install\app\bin\app.bat`.

## Main Screen

The main window offers two panels:

1. **Application JAR/DLL** – select the executable you want to test.
2. **Flow YAML** – open an existing flow to view or branch.

### Recording

- Choose an application JAR/DLL and click **Start Recording**.
- Interact with your application. When done, click **Stop**.

### Branching

- Load a flow YAML file.
- Right-click any step and select **Create Branch** to generate a new YAML file starting at that step.

The UI uses the same plugins as the CLI and stores output in the directory where it is run.
