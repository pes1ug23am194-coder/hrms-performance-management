# Performance Pulse — HRMS Performance Management Subsystem

A Java desktop application that implements the **Performance Management subsystem** of a Human Resource Management System (HRMS). It uses a Swing-based UI and an embedded **SQLite** database, and can be launched on Windows with a single script.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Repository Contents](#repository-contents)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Running the Application](#running-the-application)
- [How the Run Script Works](#how-the-run-script-works)
- [Database](#database)
- [Developer Utilities](#developer-utilities)
- [Troubleshooting](#troubleshooting)
- [Related Repository](#related-repository)
- [Roadmap](#roadmap)
- [Author](#author)

---

## Overview

Performance Pulse covers the performance side of the employee lifecycle: setting goals, tracking KPIs, running appraisals, collecting feedback, and spotting skill gaps. Data is stored in a local SQLite file, so there is no server to install or configure.

It was built as a subsystem of a larger HRMS, with hooks for integration with the other subsystems (such as customization).

<!-- VERIFY: confirm the feature list below matches what the UI in this repo's build actually exposes. -->

---

## Features

| Area | Description |
|------|-------------|
| **Appraisal management** | Create and manage employee appraisals and evaluations |
| **Goal tracking** | Set goals for employees and track progress against them |
| **KPI management** | Define and monitor key performance indicators |
| **Feedback system** | Collect 360-degree feedback |
| **Skill gap analysis** | Identify gaps between required and current skills |
| **Performance reports** | Generate summary reports for employees and teams |
| **Notifications** | Automated reminders and alerts |
| **Integration** | Designed to plug into other HRMS subsystems |

---

## Tech Stack

- **Language:** Java (JDK 8 or higher)
- **UI:** Java Swing
- **Database:** SQLite via the SQLite JDBC driver
- **Scripts:** Windows Batch (`run.bat`) and PowerShell (`run.ps1`)

---

## Repository Contents

| File | Purpose |
|------|---------|
| `run.bat` | Windows batch launcher: compile and start the app |
| `run.ps1` | PowerShell launcher: compiles all sources under `src/` into `bin/`, then starts the UI |
| `hrms.db` | Main SQLite database |
| `hrms (1).db` | Second copy of the database (acts as a backup) |
| `DumpSchema.class` | Compiled helper that prints the database schema |
| `TestIntegration.class` | Compiled helper for integration testing against the database |

The launch scripts expect two folders next to them:

```
.
├── src/     # Java sources (packages such as ui, service, models, interfaces, impl)
├── lib/     # Third-party JARs, including the SQLite JDBC driver
└── bin/     # Compiled output (created automatically by the run script)
```

<!-- VERIFY: src/ and lib/ were not visible in this repo's file list. Add them to the repo (or point to the repo that has them) so the app builds out of the box. -->

---

## Prerequisites

- **JDK 8 or newer** (`javac -version` and `java -version` should both work)
- **SQLite JDBC driver** JAR placed in `lib/`
- **Windows** with Command Prompt or PowerShell for the provided scripts (Linux and macOS users can use the manual commands below)

---

## Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/pes1ug23am194-coder/Performance-Management-Subsystem.git
   cd Performance-Management-Subsystem
   ```

2. **Add the SQLite JDBC driver**

   Download the `sqlite-jdbc` JAR from [Maven Central](https://central.sonatype.com/artifact/org.xerial/sqlite-jdbc) (the Xerial driver) and place it in a `lib/` folder at the repository root. Newer driver releases may also require the `slf4j-api` JAR in `lib/`.

3. **Confirm the sources are present**

   The `src/` folder must contain the application's `.java` files. The run script stops with an error if none are found.

---

## Running the Application

### Windows (recommended)

Command Prompt:

```bat
run.bat
```

PowerShell:

```powershell
.\run.ps1
```

If PowerShell blocks the script, allow it for this session only:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

### Manual compile and run (Windows)

```powershell
mkdir bin
$files = Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName
javac -cp ".;lib/*" -d bin $files
java -cp "bin;lib/*" ui.PerformanceManagementUI
```

### Manual compile and run (Linux / macOS)

```bash
mkdir -p bin
javac -cp ".:lib/*" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" ui.PerformanceManagementUI
```

Note the classpath separator: `;` on Windows, `:` on Linux and macOS.

---

## How the Run Script Works

`run.ps1` performs these steps:

1. Creates the `bin/` directory if it does not exist.
2. Recursively collects every `.java` file under `src/`. If none are found, it prints an error and exits.
3. Compiles them with `javac -cp ".;lib/*" -d bin`. If compilation fails, it shows the errors and pauses.
4. Launches the application's entry point, **`ui.PerformanceManagementUI`**, with `bin` and `lib/*` on the classpath.

---

## Database

The app persists everything in SQLite:

| File | Role |
|------|------|
| `hrms.db` | Primary database used by the application |
| `hrms (1).db` | Backup copy (can be restored by replacing `hrms.db`) |

Tips:

- Close the app before copying or replacing a database file.
- Inspect data with any SQLite browser (for example [DB Browser for SQLite](https://sqlitebrowser.org/)) or the `sqlite3` CLI.
- To start from a clean state, restore a copy of the original `hrms.db`.

---

## Developer Utilities

Both helpers are compiled classes in the repository root (default package), so run them from the root with `.` on the classpath:

```powershell
# Print the database schema
java -cp ".;lib/*" DumpSchema

# Run the integration check
java -cp ".;lib/*" TestIntegration
```

<!-- VERIFY: confirm whether these classes take arguments and which database path they read. -->

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `No .java source files found in src directory!` | `src/` is missing or empty | Add the source folder next to the run script |
| `package org.sqlite does not exist` / `ClassNotFoundException: org.sqlite.JDBC` | SQLite JDBC JAR not on the classpath | Place the JAR in `lib/` and re-run |
| `Could not find or load main class ui.PerformanceManagementUI` | Compile step failed, or class run from the wrong folder | Run from the repository root and check the compile output |
| `running scripts is disabled on this system` | PowerShell execution policy | Use `powershell -ExecutionPolicy Bypass -File .\run.ps1` or `run.bat` |
| `database is locked` | Another process has the `.db` file open | Close other apps or DB browsers using the file |
| `javac` is not recognized | JDK not installed or not on `PATH` | Install a JDK and add its `bin` folder to `PATH` |

---

## Related Repository

The same project, with the full source layout (`src/impl`, `src/interfaces`, `src/models`, `src/service`, `src/ui`), is also published as [hrms-performance-management](https://github.com/pes1ug23am194-coder/hrms-performance-management).

---

## Roadmap

- Include `src/` and `lib/` (or a dependency manager such as Maven or Gradle) so the project builds from a fresh clone
- Remove committed `.class` files and add a `.gitignore` for `bin/` and `*.class`
- Keep a single database file plus a clearly named seed/backup copy
- Add role-based login (employee, manager, HR)
- Export reports to PDF or CSV

---

## Author

**Nirupam Gogoi** — PES1UG23AM194, PES University
GitHub: [@pes1ug23am194-coder](https://github.com/pes1ug23am194-coder)

> Developed for educational and demonstration purposes.
