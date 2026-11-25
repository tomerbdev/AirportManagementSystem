package ams;

/**
 * Represents an immutable snapshot of meteorological conditions within the Aircraft
 * Management System (AMS).
 *
 * Used by {@link Aircraft#performPreFlightCheck(WeatherReport)} and
 * {@link ControlTower#updateWeatherConditions(WeatherReport)} to evaluate flight
 * safety and determine whether active aircraft must be grounded.
 */
public class WeatherReport {

    /**
     * The measured surface wind speed, in knots.
     */
    private final double windSpeedKnots;

    /**
     * The horizontal atmospheric visibility distance, in kilometers.
     */
    private final double visibilityKm;

    /**
     * Indicates whether severe or stormy weather conditions are present.
     */
    private final boolean isStormy;

    /**
     * Constructs a new {@code WeatherReport} with the specified wind speed, visibility,
     * and storm condition flag.
     *
     * @param windSpeedKnots the wind speed measured in knots
     * @param visibilityKm   the visibility distance measured in kilometers
     * @param isStormy       {@code true} if stormy conditions are active; {@code false}
     *                       otherwise
     */
    public WeatherReport(double windSpeedKnots, double visibilityKm, boolean isStormy) {
        this.windSpeedKnots = windSpeedKnots;
        this.visibilityKm = visibilityKm;
        this.isStormy = isStormy;
    }

    /**
     * Returns the reported wind speed in knots.
     *
     * @return the wind speed in knots
     */
    public double getWindSpeedKnots() {
        return windSpeedKnots;
    }

    /**
     * Returns the reported horizontal visibility in kilometers.
     *
     * @return the visibility in kilometers
     */
    public double getVisibilityKm() {
        return visibilityKm;
    }

    /**
     * Indicates whether stormy weather conditions are reported.
     *
     * @return {@code true} if stormy; {@code false} otherwise
     */
    public boolean isStormy() {
        return isStormy;
    }

    /**
     * Returns a formatted string representation of this weather report, including wind
     * speed, visibility, and storm status.
     *
     * @return a descriptive string summarizing this weather report
     */
    @Override
    public String toString() {
        return "WeatherReport [Wind: " + windSpeedKnots + " knots, Visibility: " + visibilityKm + " km, Stormy: " + isStormy + "]";
    }
}