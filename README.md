# Energy Community Project

This project demonstrates a simple simulation of an energy community system, focusing on:
- A REST API using Spring Boot
- A GUI using JavaFX
- Simulated usage and production data for current and historical energy status

## ✅ Components

### 🔌 REST API (Spring Boot)
- Endpoint `/energy/current`: Returns current percentage of community vs grid energy usage.
- Endpoint `/energy/historical?start=...&end=...`: Returns historical energy data in a given time range.

**Tech stack:** Java 17, Spring Boot, Maven

### 🖥️ GUI (JavaFX)
- Displays current usage percentages
- Allows input of time range and fetches historical data
- Fully decoupled from backend (uses HTTP calls)

## 🚀 How to run

### Run the REST API

```bash
cd energy-rest-api
mvn spring-boot:run
```

> Runs a Spring Boot web server on `http://localhost:8080`

### Run the GUI

```bash
cd energy-gui
mvn javafx:run
```

> Launches a JavaFX window that interacts with the backend

## 📁 Structure

```
energyproject/
├── energy-rest-api/
│   └── src/main/java/at/uastw/energy/...  # Spring Boot backend
├── energy-gui/
│   └── src/main/java/at/uastw/energy/...  # JavaFX frontend
```

## 📋 Milestone Requirements

- [x] Every component can be started independently
- [x] System builds and runs with no errors
- [x] Spring Boot used for REST API
- [x] JavaFX used for GUI
- [x] Example data used (no persistent DB)
