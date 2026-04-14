# Dual-Target Micro-Compiler & Execution Benchmarker

This is a Java desktop application that parses a small, custom math-heavy language (Micro-Language) and automatically compiles and benchmarks it using both the JVM and native code (via GCC).

## Requirements
* Ubuntu Linux (or similar Linux environment)
* JDK 17+
* GCC (`sudo apt-get install gcc`)
* `/usr/bin/time` (`sudo apt-get install time`)
* Maven

## Build & Run

1. **Build the project**
   ```bash
   mvn clean compile
   ```

2. **Run the RMI Server (Backend)**
   ```bash
   mvn exec:java -Dexec.mainClass=com.microbench.server.CompilerServer
   ```
   The server will start on `localhost:1099` by default. It must be running before you try to compile from the GUI.

3. **Run the GUI Client (Frontend)**
   ```bash
   mvn exec:java -Dexec.mainClass=com.microbench.ui.GuiMain
   ```
   If the server is offline when a benchmark is submitted, an error message will show up in the application's console output.

## Features
- **Distributed Architecture**: Client/Server separation via Java RMI.
- **Micro-Language**: Supports `int` and `double` declarations, `for` and `while` loops, math operations (`+`, `-`, `*`, `/`, `%`), bitwise operators, and `print()` statements.
- **Dual Compilation**:
  - Compiles to Java using `javac`.
  - Compiles to Native C using `gcc`.
- **Benchmarking**: Measures precise execution time (via monotonic clocks) and Peak Memory (Resident Set Size via `/usr/bin/time`).
- **Telemetry Persistence**: Automatically saves program source code and execution benchmark history to an embedded SQLite database (`~/.microbench/benchmarks.db`).
- **JavaFX UI**: Visual code editor, chart-based telemetry comparison, run history, and optimization configurations.
