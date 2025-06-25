# Energy Community Projekt

Verteilte Java-Microservice-Anwendung zur Simulation, Aggregation und Visualisierung des Energieflusses einer Solar-Community im Zusammenspiel mit dem öffentlichen Stromnetz.

## Inhaltsverzeichnis

* [Überblick](#überblick)
* [Module im Detail](#module-im-detail)

  * [common](#common)
  * [producer](#producer)
  * [user](#user)
  * [usage-service](#usage-service)
  * [percentage-service](#percentage-service)
  * [rest-api](#rest-api)
  * [gui](#gui)

---

## Überblick

Das Projekt besteht aus sieben Modulen, die als eigenständige Spring-Boot-Services (bzw. JavaFX-App) laufen und über RabbitMQ-Nachrichten sowie eine gemeinsame PostgreSQL-Datenbank miteinander kommunizieren.

---

## Module im Detail

### common

* **Paket:** `com.energy.common`
* **Inhalt:**

  * DTO-Klassen: `ProducerMessage`, `UserMessage`, `UsageUpdateMessage`, `PercentageUpdateMessage`
  * RabbitMQ-Konfiguration: Exchange- und Queue-Definitionen, Routing-Keys
  * Gemeinsame Utility-Klassen (z.B. Zeitstempel-Parser)

### producer

* **Paket:** `com.energy.producer`
* **Hauptkomponenten:**

  * `WeatherClient`: Ruft externe Wetter-API (z.B. OpenWeather) ab und liefert Cloud-Coverage
  * `SolarProductionCalculator`: Berechnet kWh basierend auf wetterabhängigen Parametern
  * `ProducerTask`: Mit `@Scheduled(fixedDelay)` definierter Task, der in regelmäßigen Abständen `ProducerMessage` erzeugt und an RabbitMQ sendet
* **Konfiguration:**

  * `application.properties` mit API-Key, RabbitMQ-Host, Scheduling-Interval

### user

* **Paket:** `com.energy.user`
* **Hauptkomponenten:**

  * `ConsumptionSimulator`: Modelliert stündliches Verbrauchsprofil (tag/nacht), erzeugt `UserMessage`
  * `UserTask`: Periodischer Task via `@Scheduled`, der Verbrauchswerte publiziert
* **Besonderheit:** Läuft in eigenem Thread-Pool, konfigurierbar via `ExecutorConfig`

### usage-service

* **Paket:** `com.energy.usage`
* **Hauptkomponenten:**

  * `UsageListener`: RabbitMQ-Listener (`@RabbitListener`) für Producer- und User-Queues
  * `UsageAggregator`: Aggregiert eingehende kWh-Werte stündlich (Minuten-genau)
  * `UsageRecord` Entity & `UsageRepository`: Speicherung in PostgreSQL-Tabelle `usage_record`
  * `UsagePublisher`: Sendet `UsageUpdateMessage` nach jeder Aggregation weiter
* **Datenbank:** Automatische Schema-Erzeugung durch Hibernate (`ddl-auto=update`)

### percentage-service

* **Paket:** `com.energy.percentage`
* **Hauptkomponenten:**

  * `PercentageListener`: Empfängt `UsageUpdateMessage`
  * `PercentageCalculator`: Berechnet Community-Pool- und Grid-Anteil in Prozent
  * `PercentageRecord` Entity & `PercentageRepository`: Persistiert Ergebnisse in Tabelle `percentage_record`
  * `PercentagePublisher`: Verteilt `PercentageUpdateMessage` für nachgelagerte Systeme
* **Logging:** Detaillierte Metrik-Logs pro Stunde

### rest-api

* **Paket:** `com.energy.api`
* **Hauptkomponenten:**

  * `EnergyController`: Endpoints:

    * `GET /energy/current` → Aktuelle kumulierte Prozentwerte über alle Stunden
    * `GET /energy/historical?start={ISO}&end={ISO}` → Liste aller `UsageRecord` im Zeitraum
  * `UsageService`, `PercentageService`: Business-Logik
  * `UsageRepository` & `PercentageRepository`: JPA-Repositories für Datenzugriff

### gui

* **Paket:** `com.energy.gui`
* **Inhalt:**

  * JavaFX-Applikation mit MVC-Ansatz
  * `MainApp`: Einstiegspunkt, setzt Scene und Stage
  * `DashboardController`: Lade und aktualisiere Live-Daten via REST-API
  * `HistoryController`: Zeigt historische Diagramme (z.B. Linien- und Balkendiagramme)
  * `FxmlLoader`: Utility zum Laden von FXML-Views
  * Ressourcen: `src/main/resources/views/*.fxml`, `styles.css`
* **Features:**

  * Live-Anzeige der Community- vs. Grid-Kennzahlen
  * Auswahl von Start- und Endzeit für historische Auswertung
  * Export von Charts als PNG
