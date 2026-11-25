package ams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scheduled flight within the Aircraft Management System (AMS).
 *
 * A {@code Flight} links an {@link Aircraft} and a {@link Pilot} to a scheduled
 * departure and arrival window, tracks the assigned {@link Runway}, manages security-cleared
 * {@link Passenger} boarding, and calculates total passenger and luggage payload weight.
 */
public class Flight {

    /**
     * The standard assumed body weight per passenger used in payload calculations,
     * measured in kilograms.
     */
    private static final double AVERAGE_PASSENGER_WEIGHT_KG = 80.0;

    /**
     * The unique flight identifier (e.g., airline code and flight number).
     */
    private final String flightNumber;

    /**
     * The aircraft assigned to operate this flight.
     */
    private final Aircraft assignedAircraft;

    /**
     * The pilot in command assigned to operate this flight.
     */
    private final Pilot assignedPilot;

    /**
     * The runway or helipad currently allocated to this flight, or {@code null} if unassigned.
     */
    private Runway assignedRunway;

    /**
     * The scheduled departure date and time.
     */
    private final LocalDateTime scheduledDeparture;

    /**
     * The scheduled arrival date and time.
     */
    private final LocalDateTime scheduledArrival;

    /**
     * The list of passengers currently boarded onto this flight.
     */
    private final List<Passenger> passengers;

    /**
     * Indicates whether this flight has been completed.
     */
    private boolean completed;

    /**
     * Constructs a new {@code Flight} with the specified flight number, crew, aircraft,
     * and schedule.
     *
     * Newly created flights begin with an empty passenger manifest, no assigned runway,
     * and {@code completed} set to {@code false}.
     *
     * @param flightNumber       the unique identifier for the flight; must not be
     *                           {@code null} or blank
     * @param assignedAircraft   the {@link Aircraft} assigned to the flight; must not be
     *                           {@code null}
     * @param assignedPilot      the {@link Pilot} assigned to the flight; must not be
     *                           {@code null}
     * @param scheduledDeparture the scheduled departure timestamp; must not be {@code null}
     * @param scheduledArrival   the scheduled arrival timestamp; must not be {@code null}
     *                           and must be strictly after {@code scheduledDeparture}
     * @throws IllegalArgumentException if {@code flightNumber} is {@code null} or blank,
     *                                  either {@code assignedAircraft} or {@code assignedPilot}
     *                                  is {@code null}, or {@code scheduledArrival} is not
     *                                  after {@code scheduledDeparture}
     */
    //Full ctor
    public Flight(String flightNumber, Aircraft assignedAircraft, Pilot assignedPilot,
                  LocalDateTime scheduledDeparture, LocalDateTime scheduledArrival) {
        if (flightNumber == null || flightNumber.isBlank()) {
            throw new IllegalArgumentException("Flight number cannot be empty.");
        }
        if (assignedAircraft == null || assignedPilot == null) {
            throw new IllegalArgumentException("Flight requires both an aircraft and a pilot.");
        }
        if (scheduledDeparture == null || scheduledArrival == null || !scheduledArrival.isAfter(scheduledDeparture)) {
            throw new IllegalArgumentException("Arrival time must be after departure time.");
        }
        this.flightNumber = flightNumber;
        this.assignedAircraft = assignedAircraft;
        this.assignedPilot = assignedPilot;
        this.scheduledDeparture = scheduledDeparture;
        this.scheduledArrival = scheduledArrival;
        this.passengers = new ArrayList<>();
        this.completed = false;
    }

    /**
     * Attempts to board a passenger onto this flight.
     *
     * Boarding succeeds only if the passenger is non-null, has passed security screening
     * ({@link Passenger#isSecurityCleared()}), is not already boarded on this flight, and
     * the aircraft has remaining seating capacity.
     *
     * @param passenger the {@link Passenger} attempting to board
     * @return {@code true} if the passenger was successfully added to the manifest;
     *         {@code false} otherwise
     */
    public boolean boardPassenger(Passenger passenger) {
        if (passenger == null || !passenger.isSecurityCleared()) {
            return false;
        }
        if (passengers.contains(passenger)) {
            return false;
        }
        int maxCapacity = getAircraftPassengerCapacity();
        if (passengers.size() >= maxCapacity) {
            return false;
        }
        passengers.add(passenger);
        return true;
    }

    /**
     * Determines the maximum passenger seating capacity for {@link #assignedAircraft}.
     *
     * @return {@link CommercialPlane#getPassengerCapacity()} if the aircraft is a
     *         {@link CommercialPlane}, or {@code 6} for other aircraft types
     */
    private int getAircraftPassengerCapacity() {
        if (assignedAircraft instanceof CommercialPlane plane) {
            return plane.getPassengerCapacity();
        }
        // Standard capacity for a helicopter
        return 6;
    }

    /**
     * Calculates the total passenger and luggage payload weight currently onboard,
     * measured in kilograms.
     *
     * Each boarded passenger contributes {@value #AVERAGE_PASSENGER_WEIGHT_KG} kg plus
     * their individual {@link Passenger#getLuggageWeightKg()}.
     *
     * @return the cumulative payload weight in kilograms
     */
    public double calculateTotalPayload() {
        double total = 0.0;
        for (Passenger p : passengers) {
            total += AVERAGE_PASSENGER_WEIGHT_KG + p.getLuggageWeightKg();
        }
        return total;
    }

    /**
     * Returns the unique flight number.
     *
     * @return the flight number
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * Returns the aircraft assigned to this flight.
     *
     * @return the assigned {@link Aircraft}
     */
    public Aircraft getAssignedAircraft() {
        return assignedAircraft;
    }

    /**
     * Returns the pilot assigned to this flight.
     *
     * @return the assigned {@link Pilot}
     */
    public Pilot getAssignedPilot() {
        return assignedPilot;
    }

    /**
     * Returns the runway or helipad currently assigned to this flight.
     *
     * @return the assigned {@link Runway}, or {@code null} if none is assigned
     */
    public Runway getAssignedRunway() {
        return assignedRunway;
    }

    /**
     * Assigns or clears the runway or helipad allocated to this flight.
     *
     * @param assignedRunway the {@link Runway} to assign, or {@code null} to unassign
     */
    public void setAssignedRunway(Runway assignedRunway) {
        this.assignedRunway = assignedRunway;
    }

    /**
     * Returns the scheduled departure date and time.
     *
     * @return the scheduled departure {@link LocalDateTime}
     */
    public LocalDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    /**
     * Returns the scheduled arrival date and time.
     *
     * @return the scheduled arrival {@link LocalDateTime}
     */
    public LocalDateTime getScheduledArrival() {
        return scheduledArrival;
    }

    /**
     * Returns a defensive copy of the passengers currently boarded on this flight.
     *
     * @return a new {@link List} containing all boarded {@link Passenger} instances
     */
    // Defensive copying to protect encapsulation
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    /**
     * Indicates whether this flight has been completed.
     *
     * @return {@code true} if the flight is completed; {@code false} otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Updates the completion state of this flight.
     *
     * @param completed {@code true} to mark the flight as completed; {@code false} otherwise
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Returns a formatted single-line summary of the flight, including flight number,
     * assigned aircraft, pilot, runway, schedule timestamps, passenger count, payload
     * weight, and status.
     *
     * @return a formatted string describing this flight
     */
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String runwayInfo = (assignedRunway != null) ? assignedRunway.getRunwayId() : "Unassigned";
        return "Flight [" + flightNumber
                + " | Aircraft: " + assignedAircraft.getTailNumber()
                + " | Pilot: " + assignedPilot.getFullName()
                + " | Runway: " + runwayInfo
                + " | Dep: " + scheduledDeparture.format(fmt)
                + " | Arr: " + scheduledArrival.format(fmt)
                + " | Pax: " + passengers.size() + "/" + getAircraftPassengerCapacity()
                + " | Payload: " + calculateTotalPayload() + "kg"
                + " | Status: " + (completed ? "COMPLETED" : "ACTIVE") + "]";
    }
}