package ams;

/**
 * Represents a rotary-wing helicopter within the Aircraft Management System (AMS).
 *
 * Extends {@link Aircraft} with rotorcraft-specific attributes—including main rotor
 * diameter and medical evacuation (MedEvac) designation—and implements {@link IRefuelable}
 * to manage onboard fuel levels and refueling operations.
 */
public class Helicopter extends Aircraft implements IRefuelable {

    /**
     * The diameter of the helicopter's main rotor blades, measured in meters.
     */
    private final double mainRotorDiameterMeters;

    /**
     * Indicates whether this helicopter is designated for medical evacuation (MedEvac) missions.
     */
    private final boolean isMedicalEvac;

    /**
     * The maximum fuel tank capacity of the helicopter, measured in liters.
     */
    private final double maxFuelCapacityLiters;

    /**
     * The current volume of fuel onboard the helicopter, measured in liters.
     */
    private double currentFuelLiters;

    /**
     * Constructs a new {@code Helicopter} with full rotorcraft, mission, and fuel
     * specifications.
     *
     * If {@code initialFuelLiters} exceeds {@code maxFuelCapacityLiters}, the initial
     * fuel level is capped at {@code maxFuelCapacityLiters}.
     *
     * @param tailNumber              the unique tail number identifying the helicopter; must
     *                                not be {@code null} or blank
     * @param modelName               the model name of the helicopter; must not be
     *                                {@code null} or blank
     * @param maxTakeoffWeightKg      the maximum takeoff weight in kilograms; must be
     *                                greater than {@code 0}
     * @param mainRotorDiameterMeters the main rotor diameter in meters
     * @param isMedicalEvac           {@code true} if the helicopter operates as a MedEvac
     *                                aircraft; {@code false} otherwise
     * @param maxFuelCapacityLiters   the maximum fuel tank capacity in liters
     * @param initialFuelLiters       the starting fuel volume in liters
     * @throws IllegalArgumentException if {@code tailNumber}, {@code modelName}, or
     *                                  {@code maxTakeoffWeightKg} fails validation in
     *                                  {@link Aircraft#Aircraft(String, String, double)}
     */
    //Full Ctor
    public Helicopter(String tailNumber, String modelName, double maxTakeoffWeightKg,
                      double mainRotorDiameterMeters, boolean isMedicalEvac,
                      double maxFuelCapacityLiters, double initialFuelLiters) {
        super(tailNumber, modelName, maxTakeoffWeightKg);
        this.mainRotorDiameterMeters = mainRotorDiameterMeters;
        this.isMedicalEvac = isMedicalEvac;
        this.maxFuelCapacityLiters = maxFuelCapacityLiters;
        this.currentFuelLiters = Math.min(initialFuelLiters, maxFuelCapacityLiters);
    }

    /**
     * Constructs a standard non-MedEvac {@code Helicopter} initialized with a full fuel tank.
     *
     * Delegates to
     * {@link #Helicopter(String, String, double, double, boolean, double, double)} with
     * {@code isMedicalEvac} set to {@code false} and {@code initialFuelLiters} set equal to
     * {@code maxFuelCapacityLiters}.
     *
     * @param tailNumber              the unique tail number identifying the helicopter; must
     *                                not be {@code null} or blank
     * @param modelName               the model name of the helicopter; must not be
     *                                {@code null} or blank
     * @param maxTakeoffWeightKg      the maximum takeoff weight in kilograms; must be
     *                                greater than {@code 0}
     * @param mainRotorDiameterMeters the main rotor diameter in meters
     * @param maxFuelCapacityLiters   the maximum fuel tank capacity in liters
     * @throws IllegalArgumentException if {@code tailNumber}, {@code modelName}, or
     *                                  {@code maxTakeoffWeightKg} fails validation in
     *                                  {@link Aircraft#Aircraft(String, String, double)}
     */
    //Standard civilian helicopter with full tank
    public Helicopter(String tailNumber, String modelName, double maxTakeoffWeightKg,
                      double mainRotorDiameterMeters, double maxFuelCapacityLiters) {
        this(tailNumber, modelName, maxTakeoffWeightKg, mainRotorDiameterMeters,
                false, maxFuelCapacityLiters, maxFuelCapacityLiters);
    }

    /**
     * Evaluates whether the helicopter is cleared for flight under the given weather
     * conditions.
     *
     * Clearance requires all of the following conditions to be met:
     * <ul>
     *   <li>The helicopter status is {@link AircraftStatus#READY} and {@code currentWeather}
     *       is not {@code null}.</li>
     *   <li>Wind speed does not exceed {@code 40.0} knots for MedEvac helicopters, or
     *       {@code 30.0} knots for standard helicopters.</li>
     *   <li>Current fuel reserves are at least 15% of {@link #maxFuelCapacityLiters}.</li>
     * </ul>
     *
     * @param currentWeather the current weather report used to assess flight safety
     * @return {@code true} if the helicopter passes all status, wind, and fuel checks;
     *         {@code false} otherwise
     */
    @Override
    public boolean performPreFlightCheck(WeatherReport currentWeather) {
        if (getStatus() != AircraftStatus.READY || currentWeather == null) {
            return false;
        }
        double maxAllowedWind = isMedicalEvac ? 40.0 : 30.0;
        boolean weatherSafe = currentWeather.getWindSpeedKnots() <= maxAllowedWind;
        boolean enoughFuel = this.currentFuelLiters >= (this.maxFuelCapacityLiters * 0.15);
        return weatherSafe && enoughFuel;
    }

    /**
     * Adds the specified volume of fuel to the helicopter's tanks.
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
     * Returns the current volume of fuel onboard the helicopter in liters.
     *
     * @return the current fuel level in liters
     */
    @Override
    public double getCurrentFuelLevel() {
        return this.currentFuelLiters;
    }

    /**
     * Returns the maximum fuel tank capacity of the helicopter in liters.
     *
     * @return the maximum fuel capacity in liters
     */
    @Override
    public double getMaxFuelCapacity() {
        return this.maxFuelCapacityLiters;
    }

    /**
     * Indicates whether this helicopter is entitled to emergency landing and runway
     * preemption priority.
     *
     * @return {@code true} if this helicopter is a MedEvac aircraft; {@code false} otherwise
     */
    public boolean hasEmergencyLandingPriority() {
        return this.isMedicalEvac;
    }

    /**
     * Returns the main rotor diameter of the helicopter in meters.
     *
     * @return the main rotor diameter in meters
     */
    public double getMainRotorDiameterMeters() {
        return mainRotorDiameterMeters;
    }

    /**
     * Indicates whether this helicopter is designated for medical evacuation missions.
     *
     * @return {@code true} if this is a MedEvac helicopter; {@code false} otherwise
     */
    public boolean isMedicalEvac() {
        return isMedicalEvac;
    }

    /**
     * Returns a formatted string representation of this helicopter, including its tail
     * number, model name, MedEvac status, fuel state, and operational status.
     *
     * @return a descriptive string summarizing this helicopter
     */
    @Override
    public String toString() {
        return "Helicopter [Tail: " + getTailNumber()
                + ", Model: " + getModelName()
                + ", MedEvac: " + (isMedicalEvac ? "Yes" : "No")
                + ", Fuel: " + currentFuelLiters + "/" + maxFuelCapacityLiters + "L"
                + ", Status: " + getStatus() + "]";
    }
}