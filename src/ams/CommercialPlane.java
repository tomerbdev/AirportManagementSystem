package ams;

/**
 * Represents a commercial passenger aircraft within the Aircraft Management System (AMS).
 *
 * Extends {@link Aircraft} with fixed-wing specifications—including wingspan, minimum
 * runway length, and passenger capacity—and implements {@link IRefuelable} to manage
 * onboard fuel levels and refueling operations.
 */
public class CommercialPlane extends Aircraft implements IRefuelable {

    /**
     * The tip-to-tip wingspan of the aircraft, measured in meters.
     */
    private final double wingspanMeters;

    /**
     * The minimum runway length required for safe takeoff and landing, measured in meters.
     */
    private final double minRunwayLengthMeters;

    /**
     * The maximum number of passengers the aircraft is configured to carry.
     */
    private final int passengerCapacity;

    /**
     * The maximum fuel tank capacity of the aircraft, measured in liters.
     */
    private final double maxFuelCapacityLiters;

    /**
     * The current volume of fuel onboard the aircraft, measured in liters.
     */
    private double currentFuelLiters;

    /**
     * Constructs a new {@code CommercialPlane} with full physical, passenger, and fuel
     * specifications.
     *
     * If {@code initialFuelLiters} exceeds {@code maxFuelCapacityLiters}, the initial
     * fuel level is capped at {@code maxFuelCapacityLiters}.
     *
     * @param tailNumber            the unique tail number identifying the aircraft; must
     *                              not be {@code null} or blank
     * @param modelName             the model name of the aircraft; must not be
     *                              {@code null} or blank
     * @param maxTakeoffWeightKg    the maximum takeoff weight in kilograms; must be
     *                              greater than {@code 0}
     * @param wingspanMeters        the wingspan of the aircraft in meters
     * @param minRunwayLengthMeters the minimum required runway length in meters
     * @param passengerCapacity     the maximum passenger seating capacity
     * @param maxFuelCapacityLiters the maximum fuel tank capacity in liters
     * @param initialFuelLiters     the starting fuel volume in liters
     * @throws IllegalArgumentException if {@code tailNumber}, {@code modelName}, or
     *                                  {@code maxTakeoffWeightKg} fails validation in
     *                                  {@link Aircraft#Aircraft(String, String, double)}
     */
    //Full constructor
    public CommercialPlane(String tailNumber, String modelName, double maxTakeoffWeightKg,
                           double wingspanMeters, double minRunwayLengthMeters,
                           int passengerCapacity, double maxFuelCapacityLiters,
                           double initialFuelLiters) {
        super(tailNumber, modelName, maxTakeoffWeightKg);
        this.wingspanMeters = wingspanMeters;
        this.minRunwayLengthMeters = minRunwayLengthMeters;
        this.passengerCapacity = passengerCapacity;
        this.maxFuelCapacityLiters = maxFuelCapacityLiters;
        this.currentFuelLiters = Math.min(initialFuelLiters, maxFuelCapacityLiters);
    }

    /**
     * Constructs a new {@code CommercialPlane} initialized with a full fuel tank.
     *
     * Delegates to
     * {@link #CommercialPlane(String, String, double, double, double, int, double, double)}
     * with {@code initialFuelLiters} set equal to {@code maxFuelCapacityLiters}.
     *
     * @param tailNumber            the unique tail number identifying the aircraft; must
     *                              not be {@code null} or blank
     * @param modelName             the model name of the aircraft; must not be
     *                              {@code null} or blank
     * @param maxTakeoffWeightKg    the maximum takeoff weight in kilograms; must be
     *                              greater than {@code 0}
     * @param wingspanMeters        the wingspan of the aircraft in meters
     * @param minRunwayLengthMeters the minimum required runway length in meters
     * @param passengerCapacity     the maximum passenger seating capacity
     * @param maxFuelCapacityLiters the maximum fuel tank capacity in liters
     * @throws IllegalArgumentException if {@code tailNumber}, {@code modelName}, or
     *                                  {@code maxTakeoffWeightKg} fails validation in
     *                                  {@link Aircraft#Aircraft(String, String, double)}
     */
    //Commercial plane initialized with a full tank
    public CommercialPlane(String tailNumber, String modelName, double maxTakeoffWeightKg,
                           double wingspanMeters, double minRunwayLengthMeters,
                           int passengerCapacity, double maxFuelCapacityLiters) {
        this(tailNumber, modelName, maxTakeoffWeightKg, wingspanMeters,
                minRunwayLengthMeters, passengerCapacity, maxFuelCapacityLiters, maxFuelCapacityLiters);
    }

    /**
     * Determines whether this commercial plane can safely operate on the given runway.
     *
     * A runway is compatible if it is non-null, not designated exclusively for
     * helicopters, and at least as long as {@link #minRunwayLengthMeters}.
     *
     * @param runway the {@link Runway} to evaluate for compatibility
     * @return {@code true} if the runway meets all length and type requirements;
     *         {@code false} otherwise
     */
    public boolean canUseRunway(Runway runway) {
        return runway != null && !runway.isHelipadOnly()
                && runway.getLengthMeters() >= this.minRunwayLengthMeters;
    }

    /**
     * Evaluates whether the commercial plane is cleared for departure under the given
     * weather conditions.
     *
     * Clearance requires all of the following conditions to be met:
     * <ul>
     *   <li>The aircraft status is {@link AircraftStatus#READY} and {@code currentWeather}
     *       is not {@code null}.</li>
     *   <li>Wind speed is at most {@code 45.0} knots, visibility is at least {@code 1.5} km,
     *       and conditions are not stormy.</li>
     *   <li>Current fuel reserves are at least 20% of {@link #maxFuelCapacityLiters}.</li>
     * </ul>
     *
     * @param currentWeather the current weather report used to assess flight safety
     * @return {@code true} if the aircraft passes all status, weather, and fuel checks;
     *         {@code false} otherwise
     */
    @Override
    public boolean performPreFlightCheck(WeatherReport currentWeather) {
        if (getStatus() != AircraftStatus.READY || currentWeather == null) {
            return false;
        }
        boolean weatherSafe = currentWeather.getWindSpeedKnots() <= 45.0
                && currentWeather.getVisibilityKm() >= 1.5
                && !currentWeather.isStormy();
        boolean enoughFuel = this.currentFuelLiters >= (this.maxFuelCapacityLiters * 0.20);
        return weatherSafe && enoughFuel;
    }

    /**
     * Adds the specified volume of fuel to the aircraft's tanks.
     *
     * @param liters the amount of fuel to add in liters; must be greater than {@code 0}
     * @throws IllegalArgumentException if {@code liters} is less than or equal to {@code 0},
     *                                  or if adding {@code liters} would exceed
     *                                  {@link #maxFuelCapacityLiters}
     */
    @Override
    public void refuel(double liters) {
        if (liters <= 0) {
            throw new IllegalArgumentException("Fuel value must be positive.");
        }
        if (this.currentFuelLiters + liters > this.maxFuelCapacityLiters) {
            throw new IllegalArgumentException("Fuel capacity overflow.");
        }
        this.currentFuelLiters += liters;
    }

    /**
     * Returns the current volume of fuel onboard the aircraft in liters.
     *
     * @return the current fuel level in liters
     */
    @Override
    public double getCurrentFuelLevel() {
        return this.currentFuelLiters;
    }

    /**
     * Returns the maximum fuel tank capacity of the aircraft in liters.
     *
     * @return the maximum fuel capacity in liters
     */
    @Override
    public double getMaxFuelCapacity() {
        return this.maxFuelCapacityLiters;
    }

    /**
     * Returns the wingspan of the aircraft in meters.
     *
     * @return the wingspan in meters
     */
    public double getWingspanMeters() {
        return wingspanMeters;
    }

    /**
     * Returns the minimum runway length required by the aircraft in meters.
     *
     * @return the minimum runway length in meters
     */
    public double getMinRunwayLengthMeters() {
        return minRunwayLengthMeters;
    }

    /**
     * Returns the maximum passenger seating capacity of the aircraft.
     *
     * @return the passenger capacity
     */
    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    /**
     * Returns a formatted string representation of this commercial plane, including its
     * tail number, model name, passenger capacity, fuel state, and operational status.
     *
     * @return a descriptive string summarizing this commercial plane
     */
    @Override
    public String toString() {
        return "CommercialPlane [Tail: " + getTailNumber()
                + ", Model: " + getModelName()
                + ", Capacity: " + passengerCapacity + " pax"
                + ", Fuel: " + currentFuelLiters + "/" + maxFuelCapacityLiters + "L"
                + ", Status: " + getStatus() + "]";
    }
}