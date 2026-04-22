# HRMS Performance Management System

A Java-based Performance Management Subsystem for HRMS (Human Resource Management System).

## Overview

This project provides a comprehensive performance management solution including:
- Employee appraisals and evaluations
- Goal setting and tracking
- KPI (Key Performance Indicator) management
- Feedback collection and management
- Skill gap analysis
- Performance reporting
- Integration with customization subsystems

## Project Structure

```
src/
├── impl/           # Repository implementations
├── interfaces/     # Interface definitions
├── models/         # Data models
├── service/        # Business logic services
└── ui/             # Swing-based UI components
```

## Features

- **Appraisal Management**: Create and manage employee appraisals
- **Goal Tracking**: Set and track employee goals
- **KPI Management**: Define and monitor key performance indicators
- **Feedback System**: Collect 360-degree feedback
- **Skill Gap Analysis**: Identify and address skill gaps
- **Performance Reports**: Generate comprehensive reports
- **Notifications**: Automated reminders and alerts

## Requirements

- Java Development Kit (JDK) 8 or higher
- SQLite JDBC Driver

## Running the Application

### Windows
```bash
run.bat
```
or
```powershell
.\run.ps1
```

### Manual Compilation
```bash
javac -d bin -cp "lib/*" src/**/*.java
java -cp "bin;lib/*" src.ui.PerformanceManagementUI
```

## Database

The application uses SQLite for data persistence. Database files:
- `hrms.db` - Main database
- `hrms (1).db` - Backup database

## License

This project is for educational/demonstration purposes.