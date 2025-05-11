# Quellcode zur Bachelorarbeit

Dieses Repository enthält den vollständigen Quellcode zur im Rahmen meiner Bachelorarbeit entwickelten Middleware.
# Thema 
Kosteneffiziente Optimierung der Datenverfügbarkeit in ressour-cenarmen Umgebungen: Entwicklung einer REST-basierten Midd-leware zur priorisierten Übertragung medizinischer Daten aus OpenMRS in eine Cloud-Datenbank



## Inhalt

- `middleware/`  
  Enthält die Java-basierte Middleware zur Synchronisation von Gesundheitsdaten zwischen OpenMRS und einer zentralen Datenbank in der Cloud. 
  Die Middleware implementiert eine zweistufige Übertragung:  
  - Notfalldaten: alle 60 Minuten  
  - Gesamtdatenbestand: täglich in der Nacht  
  Die Datenübertragung erfolgt über REST-APIs und berücksichtigt Kriterien wie Datenschutz, Datenvalidierung und Systemverfügbarkeit.

## Technologie-Stack

- Java (Spring Boot)
- PostgreSQL
- RESTful APIs (JSON)
- Docker
- TLS/SSL-gesicherte Verbindungen

##  Ausführung der Middleware

### Voraussetzungen

- Java 17+
- Maven 3.6+
- Docker + Docker Compose
- PostgreSQL-Datenbank
- OpenMRS Platform 2.5.0 (https://openmrs.org/download/)

### Middleware starten

cd middleware <br>
mvn clean install <br>
mvn spring-boot:run <br>

### Docker Datenbank (Cloud) starten

docker-compose up --build

