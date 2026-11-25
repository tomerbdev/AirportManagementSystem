package ams;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates airport ground and flight operations within the Aircraft Management System (AMS).
 *
 * The {@code ControlTower} manages registered aircraft, runways, pilots, scheduled flights,
 * and current meteorological conditions. It enforces operational safety rules across flight
 * scheduling, runway/helipad allocation (including MedEvac emergency preemption), weather-based
 * selective grounding, aircraft refueling, and post-flight maintenance tracking.
 */
public class ControlTower {

    /**
     * The four-letter ICAO location indicator identifying the airport managed by this tower.
     */
    private final String airportIcaoCode;

    /**
     * Registry of all aircraft known to the tower, keyed by their unique tail number.
     */
    private final Map<String, Aircraft> registeredAircrafts;

    /**
     * List of physical runways and helipads operated under this control tower.
     */
    private final List<Runway> runways;

    /**
     * List of pilots registered to operate flights through this control tower.
     */
    private final List<Pilot> pilots;

    /**
     * Master list of all scheduled and completed flights managed by this control tower.
     */
    private final List<Flight> scheduledFlights;

    /**
     * The most recently reported weather conditions at the airport.
     */
    private WeatherReport currentWeather;

    /**
     * Constructs a new {@code ControlTower} for the specified airport and initial weather state.
     *
     * If {@code initialWeather} is {@code null}, a default clear-weather report
     * ({@code 10.0} knots wind, {@code 10.0} km visibility, non-stormy) is assigned.
     *
     * @param airportIcaoCode the ICAO code of the airport; must not be {@code null} or blank
     * @param initialWeather  the initial {@link WeatherReport}, or {@code null} to use defaults
     * @throws IllegalArgumentException if {@code airportIcaoCode} is {@code null} or blank
     */
    //Full ctor
    public ControlTower(String airportIcaoCode, WeatherReport initialWeather) {
        if (airportIcaoCode == null || airportIcaoCode.isBlank()) {
            throw new IllegalArgumentException("Airport ICAO code cannot be empty.");
        }
        this.airportIcaoCode = airportIcaoCode;
        this.registeredAircrafts = new HashMap<>();
        this.runways = new ArrayList<>();
        this.pilots = new ArrayList<>();
        this.scheduledFlights = new ArrayList<>();
        this.currentWeather = initialWeather != null
                ? initialWeather
                : new WeatherReport(10.0, 10.0, false);
    }

    // --- Registration Methods ---

    /**
     * Registers an aircraft with this control tower.
     *
     * @param aircraft the {@link Aircraft} to register; must not be {@code null}
     * @throws IllegalArgumentException if {@code aircraft} is {@code null} or an aircraft with
     *                                  the same tail number is already registered
     */
    public void registerAircraft(Aircraft aircraft) {
        if (aircraft == null) {
            throw new IllegalArgumentException("Aircraft cannot be null.");
        }
        if (registeredAircrafts.containsKey(aircraft.getTailNumber())) {
            throw new IllegalArgumentException("Aircraft with tail number " + aircraft.getTailNumber() + " already exists.");
        }
        registeredAircrafts.put(aircraft.getTailNumber(), aircraft);
    }

    /**
     * Adds a runway or helipad to this control tower's managed infrastructure.
     *
     * @param runway the {@link Runway} to add; must not be {@code null}
     * @throws IllegalArgumentException if {@code runway} is {@code null}
     */
    public void addRunway(Runway runway) {
        if (runway == null) {
            throw new IllegalArgumentException("Runway cannot be null.");
        }
        runways.add(runway);
    }

    /**
     * Registers a pilot with this control tower.
     *
     * @param pilot the {@link Pilot} to register; must not be {@code null}
     * @throws IllegalArgumentException if {@code pilot} is {@code null}
     */
    public void registerPilot(Pilot pilot) {
        if (pilot == null) {
            throw new IllegalArgumentException("Pilot cannot be null.");
        }
        pilots.add(pilot);
    }

    // --- Use Case 1 & 5: Flight Scheduling & Runway/Helipad Assignment ---

    /**
     * Validates operational requirements, assigns a suitable runway or helipad, and schedules
     * a new flight.
     *
     * Scheduling performs the following validations in order:
     * <ol>
     *   <li>Resolves the registered {@link Aircraft} and {@link Pilot}.</li>
     *   <li>Verifies that the pilot holds the required {@link LicenseType} for the aircraft.</li>
     *   <li>Runs {@link Aircraft#performPreFlightCheck(WeatherReport)} against {@link #currentWeather}.</li>
     *   <li>Allocates a compatible {@link Runway} via {@link #assignRunwayForFlight(Flight)} and
     *       marks it {@link InfrastructureStatus#OCCUPIED}.</li>
     * </ol>
     *
     * @param flightNumber the unique identifier for the flight
     * @param tailNumber   the tail number of the assigned aircraft
     * @param pilotLicense the license number of the assigned pilot
     * @param departure    the scheduled departure date and time
     * @param arrival      the scheduled arrival date and time
     * @return the newly scheduled and runway-assigned {@link Flight}
     * @throws IllegalArgumentException if no registered aircraft matches {@code tailNumber} or
     *                                  no registered pilot matches {@code pilotLicense}
     * @throws IllegalStateException    if the pilot lacks the required certification, the
     *                                  aircraft fails pre-flight checks, or no suitable runway/helipad
     *                                  is available
     */
    public Flight scheduleFlight(String flightNumber, String tailNumber, String pilotLicense,
                                 LocalDateTime departure, LocalDateTime arrival) {
        Aircraft aircraft = findAircraftByTail(tailNumber);
        if (aircraft == null) {
            throw new IllegalArgumentException("Aircraft not found: " + tailNumber);
        }

        Pilot pilot = findPilotByLicense(pilotLicense);
        if (pilot == null) {
            throw new IllegalArgumentException("Pilot not found with license: " + pilotLicense);
        }

        // Verify pilot certification for this aircraft type
        LicenseType requiredLicense = getRequiredLicenseType(aircraft);
        if (!pilot.isCertifiedFor(requiredLicense)) {
            throw new IllegalStateException("Pilot " + pilot.getFullName()
                    + " is not certified for " + requiredLicense);
        }

        // Verify aircraft readiness and current weather compatibility
        if (!aircraft.performPreFlightCheck(currentWeather)) {
            throw new IllegalStateException("Aircraft " + tailNumber
                    + " failed pre-flight check (check status, fuel, or weather).");
        }

        Flight flight = new Flight(flightNumber, aircraft, pilot, departure, arrival);
        Runway assigned = assignRunwayForFlight(flight);
        if (assigned == null) {
            throw new IllegalStateException("No available and physically suitable runway/helipad found for " + tailNumber);
        }

        flight.setAssignedRunway(assigned);
        assigned.setStatus(InfrastructureStatus.OCCUPIED);
        scheduledFlights.add(flight);
        return flight;
    }

    /**
     * Finds and returns a compatible runway or helipad for the specified flight.
     *
     * First searches for any {@link InfrastructureStatus#AVAILABLE} infrastructure suitable
     * for the flight's aircraft. If none is available and the aircraft is a {@link Helicopter}
     * with emergency landing priority, this method preempts an {@link InfrastructureStatus#OCCUPIED}
     * compatible runway/helipad from an active non-emergency flight, unassigning the routine
     * flight's runway and resetting its status to {@link InfrastructureStatus#AVAILABLE}.
     *
     * @param flight the {@link Flight} requiring runway or helipad allocation
     * @return a suitable {@link Runway} ready for assignment, or {@code null} if none can be
     *         allocated
     */
    public Runway assignRunwayForFlight(Flight flight) {
        Aircraft aircraft = flight.getAssignedAircraft();

        // 1. Standard search for an AVAILABLE and suitable runway/helipad
        for (Runway runway : runways) {
            if (runway.getStatus() == InfrastructureStatus.AVAILABLE && runway.isSuitableFor(aircraft)) {
                return runway;
            }
        }

        // 2. Use Case 5: MedEvac Emergency Priority Override
        if (aircraft instanceof Helicopter heli && heli.hasEmergencyLandingPriority()) {
            for (Flight existingFlight : scheduledFlights) {
                Runway r = existingFlight.getAssignedRunway();
                if (!existingFlight.isCompleted()
                        && r != null
                        && r.getStatus() == InfrastructureStatus.OCCUPIED
                        && r.isSuitableFor(heli)) {

                    // Check that the existing flight is not itself an emergency MedEvac helicopter
                    boolean existingIsEmergency = existingFlight.getAssignedAircraft() instanceof Helicopter otherHeli
                            && otherHeli.hasEmergencyLandingPriority();

                    if (!existingIsEmergency) {
                        System.out.println("[EMERGENCY OVERRIDE] Preempting helipad " + r.getRunwayId()
                                + " from routine flight " + existingFlight.getFlightNumber()
                                + " for MedEvac helicopter " + heli.getTailNumber() + "!");
                        existingFlight.setAssignedRunway(null);
                        r.setStatus(InfrastructureStatus.AVAILABLE);
                        return r;
                    }
                }
            }
        }

        return null;
    }

    // --- Use Case 3: Weather Update & Selective Grounding ---

    /**
     * Updates the airport's current weather report and selectively grounds active flights
     * whose aircraft no longer meet pre-flight safety limits.
     *
     * For each incomplete flight whose aircraft is currently {@link AircraftStatus#READY},
     * if {@link Aircraft#performPreFlightCheck(WeatherReport)} returns {@code false} under the
     * new conditions, the aircraft is set to {@link AircraftStatus#GROUNDED} and any assigned
     * runway is released back to {@link InfrastructureStatus#AVAILABLE}.
     *
     * @param report the new {@link WeatherReport}; must not be {@code null}
     * @throws IllegalArgumentException if {@code report} is {@code null}
     */
    public void updateWeatherConditions(WeatherReport report) {
        if (report == null) {
            throw new IllegalArgumentException("Weather report cannot be null.");
        }
        this.currentWeather = report;
        System.out.println("\n[WEATHER UPDATE] " + report);

        for (Flight flight : scheduledFlights) {
            if (flight.isCompleted()) {
                continue;
            }
            Aircraft aircraft = flight.getAssignedAircraft();
            if (aircraft.getStatus() == AircraftStatus.READY && !aircraft.performPreFlightCheck(currentWeather)) {
                aircraft.setStatus(AircraftStatus.GROUNDED);
                Runway assignedRunway = flight.getAssignedRunway();
                if (assignedRunway != null) {
                    assignedRunway.setStatus(InfrastructureStatus.AVAILABLE);
                    flight.setAssignedRunway(null);
                }
                System.out.println("[SAFETY ALERT] Flight " + flight.getFlightNumber()
                        + " (" + aircraft.getTailNumber() + ") GROUNDED due to weather limits! Runway released.");
            }
        }
    }

    // --- Use Case 4: Refueling ---

    /**
     * Refuels a registered aircraft by the specified volume in liters.
     *
     * @param tailNumber the tail number of the aircraft to refuel
     * @param liters     the volume of fuel to add in liters
     * @throws IllegalArgumentException      if no registered aircraft matches {@code tailNumber},
     *                                       or if {@link IRefuelable#refuel(double)} rejects
     *                                       the fuel amount
     * @throws UnsupportedOperationException if the target aircraft does not implement
     *                                       {@link IRefuelable}
     */
    public void refuelAircraft(String tailNumber, double liters) {
        Aircraft aircraft = findAircraftByTail(tailNumber);
        if (aircraft == null) {
            throw new IllegalArgumentException("Aircraft not found: " + tailNumber);
        }
        if (aircraft instanceof IRefuelable refuelable) {
            refuelable.refuel(liters);
            System.out.println("[REFUEL SUCCESS] " + tailNumber + " fuel level: "
                    + refuelable.getCurrentFuelLevel() + "/" + refuelable.getMaxFuelCapacity() + "L");
        } else {
            throw new UnsupportedOperationException("Aircraft " + tailNumber + " is not refuelable.");
        }
    }

    // --- Use Case 6: Complete Flight & Preventive Maintenance Check ---

    /**
     * Marks an active flight as completed, logs flight hours for both aircraft and pilot,
     * and releases any assigned runway.
     *
     * Flight duration is calculated from scheduled departure to scheduled arrival in hours,
     * defaulting to {@code 1.0} hour if the computed duration is non-positive. Adding hours to
     * the aircraft automatically triggers a preventive maintenance status check via
     * {@link Aircraft#addFlightHours(double)}.
     *
     * @param flightNumber the identifier of the flight to complete
     * @throws IllegalArgumentException if no scheduled flight matches {@code flightNumber}
     * @throws IllegalStateException    if the flight has already been marked completed
     */
    public void completeFlight(String flightNumber) {
        Flight flight = findFlightByNumber(flightNumber);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found: " + flightNumber);
        }
        if (flight.isCompleted()) {
            throw new IllegalStateException("Flight " + flightNumber + " is already completed.");
        }

        double durationHours = Duration.between(flight.getScheduledDeparture(), flight.getScheduledArrival()).toMinutes() / 60.0;
        if (durationHours <= 0) {
            durationHours = 1.0;
        }

        Aircraft aircraft = flight.getAssignedAircraft();
        aircraft.addFlightHours(durationHours);
        flight.getAssignedPilot().logCompletedFlight(durationHours);

        if (flight.getAssignedRunway() != null) {
            flight.getAssignedRunway().setStatus(InfrastructureStatus.AVAILABLE);
        }

        flight.setCompleted(true);
        System.out.println("[FLIGHT COMPLETED] " + flightNumber + " (" + durationHours + " hrs logged). "
                + "Aircraft " + aircraft.getTailNumber() + " status is now: " + aircraft.getStatus());
    }

    // --- Lookup & Getter Helpers ---

    /**
     * Determines the pilot {@link LicenseType} required to operate the given aircraft.
     *
     * @param aircraft the aircraft to evaluate
     * @return the required {@link LicenseType}
     * @throws IllegalArgumentException if the aircraft subtype is not recognized
     */
    private LicenseType getRequiredLicenseType(Aircraft aircraft) {
        if (aircraft instanceof CommercialPlane) {
            return LicenseType.COMMERCIAL_PLANE;
        }
        if (aircraft instanceof Helicopter) {
            return LicenseType.HELICOPTER;
        }
        throw new IllegalArgumentException("Unknown aircraft type.");
    }

    /**
     * Looks up a registered aircraft by its exact tail number.
     *
     * @param tailNumber the tail number to search for
     * @return the matching {@link Aircraft}, or {@code null} if not found
     */
    public Aircraft findAircraftByTail(String tailNumber) {
        return registeredAircrafts.get(tailNumber);
    }

    /**
     * Looks up a registered pilot by license number using case-insensitive matching.
     *
     * @param licenseNumber the pilot license number to search for
     * @return the matching {@link Pilot}, or {@code null} if not found
     */
    public Pilot findPilotByLicense(String licenseNumber) {
        for (Pilot p : pilots) {
            if (p.getPilotLicenseNumber().equalsIgnoreCase(licenseNumber)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Looks up a scheduled flight by flight number using case-insensitive matching.
     *
     * @param flightNumber the flight number to search for
     * @return the matching {@link Flight}, or {@code null} if not found
     */
    public Flight findFlightByNumber(String flightNumber) {
        for (Flight f : scheduledFlights) {
            if (f.getFlightNumber().equalsIgnoreCase(flightNumber)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Returns the ICAO code of the airport managed by this control tower.
     *
     * @return the airport ICAO code
     */
    public String getAirportIcaoCode() {
        return airportIcaoCode;
    }

    /**
     * Returns the current weather report recorded by this control tower.
     *
     * @return the current {@link WeatherReport}
     */
    public WeatherReport getCurrentWeather() {
        return currentWeather;
    }

    /**
     * Returns a defensive copy of all aircraft registered with this control tower.
     *
     * @return a new {@link List} containing all registered {@link Aircraft}
     */
    public List<Aircraft> getAllAircrafts() {
        return new ArrayList<>(registeredAircrafts.values());
    }

    /**
     * Returns a defensive copy of all runways and helipads managed by this control tower.
     *
     * @return a new {@link List} containing all {@link Runway} instances
     */
    public List<Runway> getRunways() {
        return new ArrayList<>(runways);
    }

    /**
     * Returns a defensive copy of all pilots registered with this control tower.
     *
     * @return a new {@link List} containing all registered {@link Pilot} instances
     */
    public List<Pilot> getPilots() {
        return new ArrayList<>(pilots);
    }

    /**
     * Returns a defensive copy of all flights scheduled through this control tower.
     *
     * @return a new {@link List} containing all {@link Flight} instances
     */
    public List<Flight> getScheduledFlights() {
        return new ArrayList<>(scheduledFlights);
    }
}