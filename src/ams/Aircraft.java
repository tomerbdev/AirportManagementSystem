package ams;

/**
 * Represents an abstract aircraft within the Aircraft Management System (AMS).
 *
 * This base class encapsulates core aircraft metadata, operational flight hours,
 * and readiness status. Subclasses must define aircraft-specific pre-flight
 * inspection logic via {@link #performPreFlightCheck(WeatherReport)}.
 */
public abstract class Aircraft {

    /**
     * The cumulative flight hour threshold at which an aircraft automatically
     * transitions to {@link AircraftStatus#MAINTENANCE}.
     */
    private static final double MAINTENANCE_THRESHOLD_HOURS = 500.0;

    /**
     * The unique registration identifier (tail number) assigned to the aircraft.
     */
    private final String tailNumber;

    /**
     * The manufacturer model designation of the aircraft.
     */
    private final String modelName;

    /**
     * The maximum certified takeoff weight of the aircraft, measured in kilograms.
     */
    private final double maxTakeoffWeightKg;

    /**
     * The cumulative flight time logged by the aircraft, measured in hours.
     */
    private double totalFlightHours;

    /**
     * The current operational status of the aircraft.
     */
    private AircraftStatus status;

    /**
     * Constructs a new {@code Aircraft} with the specified registration details and
     * weight limit.
     *
     * Newly initialized aircraft start with {@code 0.0} total flight hours and an
     * initial status of {@link AircraftStatus#READY}.
     *
     * @param tailNumber         the unique tail number identifying the aircraft; must
     *                           not be {@code null} or blank
     * @param modelName          the model name of the aircraft; must not be
     *                           {@code null} or blank
     * @param maxTakeoffWeightKg the maximum takeoff weight in kilograms; must be
     *                           greater than {@code 0}
     * @throws IllegalArgumentException if {@code tailNumber} is {@code null} or blank,
     *                                  {@code modelName} is {@code null} or blank, or
     *                                  {@code maxTakeoffWeightKg} is less than or equal to {@code 0}
     */
    public Aircraft(String tailNumber, String modelName, double maxTakeoffWeightKg) {
        if (tailNumber == null || tailNumber.isBlank()) {
            throw new IllegalArgumentException("Tail number cannot be empty.");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("Model name cannot be empty.");
        }
        if (maxTakeoffWeightKg <= 0) {
            throw new IllegalArgumentException("Max takeoff weight must be positive.");
        }
        this.tailNumber = tailNumber;
        this.modelName = modelName;
        this.maxTakeoffWeightKg = maxTakeoffWeightKg;
        this.totalFlightHours = 0.0;
        this.status = AircraftStatus.READY;
    }

    /**
     * Evaluates whether the aircraft is safe and cleared for departure under the
     * given meteorological conditions.
     *
     * @param currentWeather the current weather report used to assess flight safety
     * @return {@code true} if the aircraft passes all pre-flight checks; {@code false}
     *         otherwise
     */
    public abstract boolean performPreFlightCheck(WeatherReport currentWeather);

    /**
     * Logs additional flight hours to this aircraft's cumulative total and updates
     * its operational status accordingly.
     *
     * If the resulting total flight hours reach or exceed
     * {@value #MAINTENANCE_THRESHOLD_HOURS} hours, the aircraft's status is set to
     * {@link AircraftStatus#MAINTENANCE}; otherwise, it is set to
     * {@link AircraftStatus#READY}.
     *
     * @param hours the number of flight hours to add; must be non-negative
     * @throws IllegalArgumentException if {@code hours} is negative
     */
    public void addFlightHours(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Flight hours cannot be negative.");
        }
        this.totalFlightHours += hours;
        if (this.totalFlightHours >= MAINTENANCE_THRESHOLD_HOURS) {
            this.status = AircraftStatus.MAINTENANCE;
        } else {
            this.status = AircraftStatus.READY;
        }
    }

    /**
     * Returns the unique tail number of the aircraft.
     *
     * @return the aircraft's tail number
     */
    public String getTailNumber() {
        return tailNumber;
    }

    /**
     * Returns the model name of the aircraft.
     *
     * @return the aircraft's model name
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Returns the maximum certified takeoff weight of the aircraft in kilograms.
     *
     * @return the maximum takeoff weight in kilograms
     */
    public double getMaxTakeoffWeightKg() {
        return maxTakeoffWeightKg;
    }

    /**
     * Returns the cumulative flight hours logged by the aircraft.
     *
     * @return the total flight hours
     */
    public double getTotalFlightHours() {
        return totalFlightHours;
    }

    /**
     * Returns the current operational status of the aircraft.
     *
     * @return the current {@link AircraftStatus}
     */
    public AircraftStatus getStatus() {
        return status;
    }

    /**
     * Updates the operational status of the aircraft.
     *
     * @param status the new {@link AircraftStatus} to assign
     */
    public void setStatus(AircraftStatus status) {
        this.status = status;
    }

    /**
     * Returns a concise string representation of the aircraft, including its tail
     * number, model name, accumulated flight hours, and current status.
     *
     * @return a formatted string describing this aircraft
     */
    @Override
    public String toString() {
        return "Aircraft [Tail: " + tailNumber + ", Model: " + modelName
                + ", Hours: " + totalFlightHours + ", Status: " + status + "]";
    }
}