# arc42-Architekturdokumentation
## Thesis-Matching-Plattform

Version: 1.0  
Status: Final  
Autoren: Team Thesis-Matching

---

# Inhaltsverzeichnis

1. [Einleitung und Ziele](#1-einleitung-und-ziele)
2. [Randbedingungen](#2-randbedingungen)
3. [Kontextabgrenzung](#3-kontextabgrenzung)
4. [Lösungsstrategie](#4-lösungsstrategie)
5. [Bausteinsicht (Level 1)](#5-bausteinsicht-level-1)
6. [Laufzeitsicht](#6-laufzeitsicht)
7. [Verteilungssicht](#7-verteilungssicht)
8. [Querschnittliche Konzepte](#8-querschnittliche-konzepte)
9. [Architekturentscheidungen](#9-architekturentscheidungen)
10. [Qualitätsanforderungen](#10-qualitätsanforderungen)
11. [Risiken und technische Schulden](#11-risiken-und-technische-schulden)
12. [Glossar](#12-glossar)

---

# 1. Einleitung und Ziele

## 1.1 Aufgabenstellung

Die Thesis-Matching-Plattform unterstützt Studierende bei der Suche nach einer geeigneten Betreuungsperson und einem passenden Thema für ihre Abschlussarbeit.

Zentrale Funktionen:

- Browsing und Filtern von Betreuerprofilen
- Verwaltung von Themen
- Matching basierend auf Interessen und bestandenen Veranstaltungen
- Rollenbasierte Zugriffskontrolle über GitHub OAuth

Die Anwendung ist als serverseitige Webanwendung mit Java und Spring Boot umgesetzt.

---

## 1.2 Qualitätsziele

| Priorität | Qualitätsziel | Beschreibung                    |
|------------|--------------|---------------------------------|
| Hoch | Sicherheit | Kein unautorisierter Zugriff    |
| Hoch | Konsistenz | Keine Lost Updates              |
| Hoch | Wartbarkeit | Klare Schichtenarchitektur      |
| Mittel | Testbarkeit | 80-90 % Testabdeckung           |
| Mittel | Erweiterbarkeit | Matching und Rollen erweiterbar |
| Mittel | Accessibility | Tastaturbedienbarkeit           |

---

# 2. Randbedingungen

- Programmiersprache: Java
- Framework: Spring Boot
- Authentifizierung: GitHub OAuth2
- Rollen: Admin, Betreuer, Studierende
- Admin-Rollen aus Konfigurationsdatei
- Deployment über Docker Compose
- Dateien im Dateisystem
- Datenbank (z. B. PostgreSQL)
- Architekturprüfung mit ArchUnit
- Codequalität mit Checkstyle und SpotBugs

---

# 3. Kontextabgrenzung

## 3.1 Fachlicher Kontext

Externe Akteure:

- Studierende
- Betreuende
- Administratoren

Externe Systeme:

- GitHub OAuth
- Datenbank
- Dateisystem

---

## 3.2 Kontextdiagramm


Siehe Datei:

`docs/diagrams/context.puml`


---

## 3.3 Technische Abgrenzung

Nicht Teil des Systems:

- Hosting-Infrastruktur
- GitHub als Identitätsprovider
- Manuelle Datenbankadministration

---

# 4. Lösungsstrategie

Die Anwendung folgt einer **Onion-Architektur**, um die Geschäftslogik von Infrastruktur und Frameworks zu trennen.

## 4.1 Architekturprinzipien

- Domain-Driven Design
- Klare Aggregate
- Repository Pattern
- Method-Level Security
- DTO-Trennung von Domain und Persistenz

---

## 4.2 Zentrale Designentscheidungen

### Betreuer als Aggregate Root

`Betreuer` ist das zentrale Aggregat.  
`Thema` gehört zum Betreuer-Aggregat.

Begründung:  
Themen existieren nur im Kontext eines Betreuers und werden ausschließlich über ihn verändert.

---

### DTO-basierte Persistenz

Domain-Objekte sind nicht direkt JPA-Entities.  
Stattdessen werden DTOs verwendet (z. B. `BetreuerDTO`, `ThemaDTO`).  
Die Klasse `BetreuerRepositoryImpl` übernimmt das Mapping.

Vorteil:  
Die Domäne bleibt frameworkfrei und gut testbar.

---

### Matching als Domain-Logik

Matching wird über `Matching`, `MatchingService` und `MatchingResult` implementiert.  
Die Logik liegt vollständig im Domain-/Application-Layer.

---

# 5. Bausteinsicht (Level 1)

## 5.1 Überblick

Die Anwendung besteht aus fünf Hauptbausteinen:

1. Web Layer
2. Security Layer
3. Application Layer
4. Domain Layer
5. Persistence Adapter

---

## 5.2 Bausteindiagramm

Siehe Datei:

`docs/diagrams/building-blocks-l1.puml`


---

## 5.3 Erläuterung der Bausteine

### Web Layer
Verarbeitet HTTP-Anfragen.  
Enthält keine Geschäftslogik.

### Security Layer
Konfiguriert OAuth2 und Rollen.  
Zugriff wird über Annotationen wie `@AdminOnly` geregelt.

### Application Layer
Orchestriert Anwendungsfälle wie:

- Betreuer anlegen
- Thema hinzufügen
- Matching durchführen

### Domain Layer
Enthält fachliche Logik:

- Betreuer (Aggregate Root)
- Thema
- Matching
- TagCatalog
- VeranstaltungCatalog

Diese Schicht ist unabhängig von Spring.

### Persistence Adapter
Implementiert Repository-Interfaces.  
Verwendet DTOs zur Datenbankabbildung.

---

# 6. Laufzeitsicht

## Szenario: Matching durchführen

1. Student gibt Module und Interessen an
2. `MatchingService` wird aufgerufen
3. `Matching` berechnet Übereinstimmungswert
4. Ergebnisse werden sortiert zurückgegeben

---

## Szenario: Thema hinzufügen

1. Betreuer authentifiziert sich
2. `ThemaService` fügt Thema zum Betreuer-Aggregat hinzu
3. Repository speichert das Aggregat

---

# 7. Verteilungssicht

Deployment erfolgt über Docker Compose.

Container:

- Spring Boot Anwendung
- PostgreSQL

Start:
./gradlew bootJar
docker compose up

Die Anwendung läuft serverseitig und wird über HTTP aufgerufen.

---

# 8. Querschnittliche Konzepte

## 8.1 Sicherheit

- GitHub OAuth2
- Rollenbasierte Zugriffskontrolle
- Method-Level Security
- Keine Secrets im Repository
- Validierung aller Eingaben

---

## 8.2 Nebenläufigkeit

- Schreibzugriffe erfolgen über Services
- Konsistenz über Aggregate Root
- Empfehlung: Optimistic Locking
- Konflikte werden sichtbar gemacht

---

## 8.3 Validierung

- Titel eines Themas darf nicht leer sein
- Tags und Veranstaltungen über Kataloge validiert

---

## 8.4 Testkonzept

- Unit-Tests für Domain-Logik
- Integrationstests für Repository & Security
- AAA-Struktur
- Tests prüfen Verhalten, keine Implementierungsdetails
- Ziel: 80–90 % Testabdeckung

---

# 9. Architekturentscheidungen

| Entscheidung | Begründung |
|--------------|------------|
| Onion-Architektur | Hohe Wartbarkeit |
| DTO-Trennung | Frameworkunabhängige Domäne |
| Betreuer als Root | Klare Konsistenzgrenze |
| Method Security | Schutz vor unautorisiertem Zugriff |
| Matching in Domain | Saubere Trennung von Web und Logik |

---

# 10. Qualitätsanforderungen

| Kategorie | Umsetzung |
|------------|-----------|
| Sicherheit | OAuth + Rollenprüfung |
| Konsistenz | Aggregate Root |
| Wartbarkeit | Schichtentrennung |
| Erweiterbarkeit | Modularer Service-Aufbau |
| Testbarkeit | Frameworkfreie Domain |

---

# 11. Risiken und technische Schulden

| Risiko | Maßnahme |
|--------|----------|
| Lost Updates | Versionierung |
| Schichtenverletzung | ArchUnit |
| Zu große Aggregate | Regelmäßige DDD-Überprüfung |

---

# 12. Glossar

| Begriff | Bedeutung |
|----------|-----------|
| Betreuer | Betreuungsperson |
| Thema | Abschlussarbeitsthema |
| Matching | Berechnung der Übereinstimmung |
| Aggregate | Konsistenzgrenze |
| Onion-Architektur | Schichtenmodell mit Domänenkern |



