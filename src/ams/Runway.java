package ams;

/**
 * Represents a takeoff and landing surface (runway or helipad) within the Aircraft
 * Management System (AMS).
 *
 * Encapsulates physical constraints—such as usable length, maximum load capacity,
 * and rotorcraft-only designation—along with current operational availability via
 * {@link InfrastructureStatus}.
 */
public class Runway {

    /**
     * The unique identifier or designator of the runway or helipad (e.g., {@code "09L/27R"} or {@code "H1"}).
     */
    private final String runwayId;

    /**
     * The usable length of the surface, measured in meters.
     */
    private final double lengthMeters;

    /**
     * The maximum certified aircraft weight supported by this surface, measured in kilograms.
     */
    private final double maxWeightLimitKg;

    /**
     * Indicates whether this surface is restricted exclusively to helicopters.
     */
    private final boolean isHelipadOnly;

    /**
     * The current operational availability status of this runway or helipad.
     */
    private InfrastructureStatus status;

    /**
     * Constructs a new {@code Runway} with the specified identifier, physical limits,
     * and surface type.
     *
     * Newly initialized runways and helipads start with an initial status of
     * {@link InfrastructureStatus#AVAILABLE}.
     *
     * @param runwayId         the unique identifier for the runway or helipad; must not be
     *                         {@code null} or blank
     * @param lengthMeters     the usable length in meters; must be greater than {@code 0}
     * @param maxWeightLimitKg the maximum supported weight in kilograms; must be greater
     *                         than {@code 0}
     * @param isHelipadOnly    {@code true} if the surface is designated exclusively for
     *                         helicopters; {@code false} for fixed-wing runways
     * @throws IllegalArgumentException if {@code runwayId} is {@code null} or blank, or if
     *                                  {@code lengthMeters} or {@code maxWeightLimitKg} is
     *                                  less than or equal to {@code 0}
     */
    //Full Ctor
    public Runway(String runwayId, double lengthMeters, double maxWeightLimitKg, boolean isHelipadOnly) {
        if (runwayId == null || runwayId.isBlank()) {
            throw new IllegalArgumentException("Runway ID cannot be empty.");
        }
        if (lengthMeters <= 0 || maxWeightLimitKg <= 0) {
            throw new IllegalArgumentException("Length and max weight must be positive.");
        }
        this.runwayId = runwayId;
        this.lengthMeters = lengthMeters;
        this.maxWeightLimitKg = maxWeightLimitKg;
        this.isHelipadOnly = isHelipadOnly;
        this.status = InfrastructureStatus.AVAILABLE;
    }

    /**
     * Evaluates whether this runway or helipad is physically compatible with the given
     * aircraft.
     *
     * Compatibility requires that:
     * <ul>
     *   <li>{@code aircraft} is non-null and its {@link Aircraft#getMaxTakeoffWeightKg()}
     *       does not exceed {@link #maxWeightLimitKg}.</li>
     *   <li>For a {@link Helicopter}, this surface is designated as helipad-only.</li>
     *   <li>For a {@link CommercialPlane}, this surface is not helipad-only and satisfies
     *       {@link CommercialPlane#canUseRunway(Runway)}.</li>
     *   <li>For any other {@link Aircraft} subtype, this surface is not helipad-only.</li>
     * </ul>
     *
     * @param aircraft the {@link Aircraft} to check for compatibility
     * @return {@code true} if the aircraft can safely use this surface; {@code false} otherwise
     */
    public boolean isSuitableFor(Aircraft aircraft) {
        if (aircraft == null) {
            return false;
        }
        if (aircraft.getMaxTakeoffWeightKg() > this.maxWeightLimitKg) {
            return false;
        }
        if (aircraft instanceof Helicopter) {
            return this.isHelipadOnly;
        }
        if (aircraft instanceof CommercialPlane plane) {
            return !this.isHelipadOnly && plane.canUseRunway(this);
        }
        return !this.isHelipadOnly;
    }

    /**
     * Returns the unique identifier of this runway or helipad.
     *
     * @return the runway or helipad ID
     */
    public String getRunwayId() {
        return runwayId;
    }

    /**
     * Returns the usable length of this surface in meters.
     *
     * @return the length in meters
     */
    public double getLengthMeters() {
        return lengthMeters;
    }

    /**
     * Returns the maximum aircraft weight limit supported by this surface in kilograms.
     *
     * @return the maximum weight limit in kilograms
     */
    public double getMaxWeightLimitKg() {
        return maxWeightLimitKg;
    }

    /**
     * Indicates whether this surface is restricted exclusively to helicopters.
     *
     * @return {@code true} if this is a helipad-only surface; {@code false} otherwise
     */
    public boolean isHelipadOnly() {
        return isHelipadOnly;
    }

    /**
     * Returns the current operational status of this runway or helipad.
     *
     * @return the current {@link InfrastructureStatus}
     */
    public InfrastructureStatus getStatus() {
        return status;
    }

    /**
     * Updates the operational status of this runway or helipad.
     *
     * @param status the new {@link InfrastructureStatus} to assign
     */
    public void setStatus(InfrastructureStatus status) {
        this.status = status;
    }

    /**
     * Returns a formatted string representation of this surface, labeled as either
     * {@code "Helipad"} or {@code "Runway"} along with its ID, length, weight limit,
     * and current status.
     *
     * @return a descriptive string summarizing this runway or helipad
     */
    @Override
    public String toString() {
        String type = isHelipadOnly ? "Helipad" : "Runway";
        return type + " [" + runwayId + ", Length: " + lengthMeters + "m, MaxWeight: "
                + maxWeightLimitKg + "kg, Status: " + status + "]";
    }
}