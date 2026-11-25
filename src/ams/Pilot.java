package ams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a certified aircraft pilot within the Aircraft Management System (AMS).
 *
 * Extends {@link Person} with aviation credentials, aircraft type certifications,
 * cumulative logged flight hours, and command rank status.
 */
public class Pilot extends Person {

    /**
     * The unique aviation license number issued to the pilot.
     */
    private final String pilotLicenseNumber;

    /**
     * The set of aircraft {@link LicenseType} certifications currently held by the pilot.
     */
    private final Set<LicenseType> certifiedAircraftTypes;

    /**
     * The cumulative flight time logged by the pilot, measured in hours.
     */
    private double loggedFlightHours;

    /**
     * Indicates whether the pilot holds the rank of captain.
     */
    private boolean isCaptain;

    /**
     * Constructs a new {@code Pilot} with personal details, license credentials,
     * aircraft certifications, and flight experience.
     *
     * A defensive copy of {@code certifiedAircraftTypes} is stored internally; if
     * {@code null} is supplied, an empty certification set is initialized. Negative
     * values for {@code loggedFlightHours} are clamped to {@code 0.0}.
     *
     * @param personId               the unique identifier for the person; must not be
     *                               {@code null} or blank
     * @param firstName              the pilot's given name; must not be {@code null} or blank
     * @param lastName               the pilot's family name; must not be {@code null} or blank
     * @param phoneNumber            the pilot's contact phone number
     * @param pilotLicenseNumber     the unique pilot license number; must not be
     *                               {@code null} or blank
     * @param certifiedAircraftTypes the initial set of {@link LicenseType} certifications,
     *                               or {@code null} for none
     * @param loggedFlightHours      the initial logged flight hours
     * @param isCaptain              {@code true} if the pilot holds captain rank;
     *                               {@code false} otherwise
     * @throws IllegalArgumentException if {@code pilotLicenseNumber} is {@code null} or blank,
     *                                  or if person attributes fail validation in
     *                                  {@link Person#Person(String, String, String, String)}
     */
    //Full ctor
    public Pilot(String personId, String firstName, String lastName, String phoneNumber,
                 String pilotLicenseNumber, Set<LicenseType> certifiedAircraftTypes,
                 double loggedFlightHours, boolean isCaptain) {
        super(personId, firstName, lastName, phoneNumber);
        if (pilotLicenseNumber == null || pilotLicenseNumber.isBlank()) {
            throw new IllegalArgumentException("Pilot license number cannot be empty.");
        }
        this.pilotLicenseNumber = pilotLicenseNumber;
        this.certifiedAircraftTypes = certifiedAircraftTypes != null
                ? new HashSet<>(certifiedAircraftTypes)
                : new HashSet<>();
        this.loggedFlightHours = Math.max(0.0, loggedFlightHours);
        this.isCaptain = isCaptain;
    }

    /**
     * Checks whether this pilot is certified to operate aircraft requiring the specified
     * {@link LicenseType}.
     *
     * @param requiredType the {@link LicenseType} to verify
     * @return {@code true} if {@code requiredType} is non-null and present in this pilot's
     *         certifications; {@code false} otherwise
     */
    public boolean isCertifiedFor(LicenseType requiredType) {
        return requiredType != null && certifiedAircraftTypes.contains(requiredType);
    }

    /**
     * Grants an additional aircraft {@link LicenseType} certification to this pilot.
     *
     * If {@code type} is {@code null} or already held, this method has no effect.
     *
     * @param type the {@link LicenseType} certification to add
     */
    public void addCertification(LicenseType type) {
        if (type != null) {
            certifiedAircraftTypes.add(type);
        }
    }

    /**
     * Adds completed flight hours to this pilot's cumulative flight log.
     *
     * @param durationHours the duration of the completed flight in hours; must be
     *                      non-negative
     * @throws IllegalArgumentException if {@code durationHours} is negative
     */
    public void logCompletedFlight(double durationHours) {
        if (durationHours < 0) {
            throw new IllegalArgumentException("Flight duration cannot be negative.");
        }
        this.loggedFlightHours += durationHours;
    }

    /**
     * Returns the pilot's unique license number.
     *
     * @return the pilot license number
     */
    public String getPilotLicenseNumber() {
        return pilotLicenseNumber;
    }

    /**
     * Returns an unmodifiable view of the aircraft certifications held by this pilot.
     *
     * @return an unmodifiable {@link Set} of {@link LicenseType} values
     */
    public Set<LicenseType> getCertifiedAircraftTypes() {
        return Collections.unmodifiableSet(certifiedAircraftTypes);
    }

    /**
     * Returns the cumulative flight hours logged by this pilot.
     *
     * @return the total logged flight hours
     */
    public double getLoggedFlightHours() {
        return loggedFlightHours;
    }

    /**
     * Indicates whether this pilot holds the rank of captain.
     *
     * @return {@code true} if the pilot is a captain; {@code false} otherwise
     */
    public boolean isCaptain() {
        return isCaptain;
    }

    /**
     * Updates the captain rank status of this pilot.
     *
     * @param captain {@code true} to designate the pilot as a captain; {@code false} otherwise
     */
    public void setCaptain(boolean captain) {
        isCaptain = captain;
    }

    /**
     * Returns a formatted string representation of this pilot, including their full name,
     * license number, held certifications, logged flight hours, and captain status.
     *
     * @return a descriptive string summarizing this pilot
     */
    @Override
    public String toString() {
        return "Pilot [" + getFullName()
                + ", License: " + pilotLicenseNumber
                + ", Certified: " + certifiedAircraftTypes
                + ", Hours: " + loggedFlightHours
                + ", Captain: " + (isCaptain ? "Yes" : "No") + "]";
    }
}