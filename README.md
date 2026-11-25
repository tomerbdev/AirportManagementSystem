# Airport Management System (AMS)

An object-oriented Java console application simulating airside and ground operations for an airport control tower (`LLBG`).

## Overview

The system models the core operational entities of an airport (aircraft, runways, helipads, pilots, passengers, scheduled flights, and meteorological conditions), coordinated through a central `ControlTower`.

## Key Features

* **Flight Scheduling & Automated Infrastructure Allocation:** Validates pilot licensing (`LicenseType`), aircraft readiness, and weather limits before assigning a physically compatible `Runway` or helipad.
* **Emergency MedEvac Priority Preemption:** Medical evacuation helicopters (`Helicopter` with `isMedicalEvac = true`) automatically preempt occupied helipads from routine flights during emergencies.
* **Passenger Boarding & Payload Calculation:** Enforces security screening (`securityCleared`), aircraft seating capacity, and calculates total passenger and luggage weight (`calculateTotalPayload()`).
* **Selective Weather Grounding:** Updating `WeatherReport` conditions runs a polymorphic `performPreFlightCheck()` across all active flights, grounding weather-restricted aircraft and releasing their runways.
* **Refueling Contract (`IRefuelable`):** Enforces fuel tank capacity limits and prevents overflow via exception handling.
* **Flight Completion & Preventive Maintenance:** Logs flight hours for both aircraft and pilot upon completion, automatically transitioning aircraft to `MAINTENANCE` when cumulative hours reach the 500-hour threshold.

## Object-Oriented Architecture

* **Abstract Base Class:** `Aircraft` defines shared telemetry and enforces `performPreFlightCheck(WeatherReport)` across concrete subclasses (`CommercialPlane`, `Helicopter`).
* **Inheritance:** `Pilot` and `Passenger` extend `Person`, inheriting core identity fields while adding role-specific attributes and behaviors.
* **Interfaces:** `IRefuelable` decouples fuel management from the aircraft inheritance hierarchy.
* **Encapsulation & Defensive Copying:** Collection getters in `Flight` and `ControlTower` return defensive copies (`new ArrayList<>(...)`) to protect internal state.

## Project Structure

```text
src/ams/
├── Aircraft.java              # Abstract base class for all aircraft
├── AircraftStatus.java        # Enum: READY, IN_FLIGHT, GROUNDED, MAINTENANCE
├── CommercialPlane.java       # Fixed-wing passenger aircraft (extends Aircraft, implements IRefuelable)
├── ControlTower.java          # Central coordination and safety rule engine
├── Flight.java                # Links aircraft, pilot, runway, schedule, and passengers
├── Helicopter.java            # Rotary-wing aircraft with MedEvac support (extends Aircraft, implements IRefuelable)
├── InfrastructureStatus.java  # Enum: AVAILABLE, OCCUPIED, MAINTENANCE
├── IRefuelable.java           # Interface defining refueling behavior
├── LicenseType.java           # Enum: COMMERCIAL_PLANE, HELICOPTER
├── Main.java                  # Interactive CLI menu and seed data
├── Passenger.java             # Customer entity with passport, luggage, and security state (extends Person)
├── Person.java                # Base class for human entities
├── Pilot.java                 # Flight crew with certifications and logged hours (extends Person)
├── Runway.java                # Physical runway or helipad with weight/length constraints
└── WeatherReport.java         # Immutable meteorological snapshot