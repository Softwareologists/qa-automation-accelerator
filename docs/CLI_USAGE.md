# CLI Usage

The command line interface is built with [Clikt](https://github.com/ajalt/clikt) and provides access to all core features.

## Listing Commands

```bash
./gradlew run --args="--help"
```

## Recording a Flow

Capture HTTP and file interactions while running your application:

```bash
./gradlew run --args="record path/to/app.jar"
```

## Replaying a Flow

Execute a previously recorded flow and output a report directory:

```bash
./gradlew run --args="replay --flow examples/sample-flow.yaml --reportDir reports/"
```

## Branching

Create a branch starting at a specific step in a flow:

```bash
./gradlew run --args="branch create --base examples/sample-flow.yaml --at 1 --name variant"
```

The resulting YAML file can then be replayed or further modified.
