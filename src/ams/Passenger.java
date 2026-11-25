package ams;

/**
 * Represents a flight passenger within the Aircraft Management System (AMS).
 *
 * Extends {@link Person} with travel documentation, security clearance status,
 * and checked luggage weight tracking used during flight boarding and payload
 * calculations.
 */
public class Passenger extends Person {

    /**
     * The unique passport identifier issued to the passenger.
     */
    private final String passportNumber;

    /**
     * The passenger's nationality or country of citizenship.
     */
    private final String nationality;

    /**
     * Indicates whether the passenger has cleared airport security screening.
     */
    private boolean securityCleared;

    /**
     * The cumulative weight of luggage checked by the passenger, measured in kilograms.
     */
    private double luggageWeightKg;

    /**
     * Constructs a new {@code Passenger} with personal details and travel identification.
     *
     * Newly initialized passengers start with {@code securityCleared} set to
     * {@code false} and {@code luggageWeightKg} set to {@code 0.0}.
     *
     * @param personId       the unique identifier for the person; must not be
     *                       {@code null} or blank
     * @param firstName      the passenger's given name; must not be {@code null} or blank
     * @param lastName       the passenger's family name; must not be {@code null} or blank
     * @param phoneNumber    the passenger's contact phone number
     * @param passportNumber the passenger's passport number; must not be {@code null}
     *                       or blank
     * @param nationality    the passenger's nationality
     * @throws IllegalArgumentException if {@code passportNumber} is {@code null} or blank,
     *                                  or if person attributes fail validation in
     *                                  {@link Person#Person(String, String, String, String)}
     */
    public Passenger(String personId, String firstName, String lastName, String phoneNumber,
                     String passportNumber, String nationality) {
        super(personId, firstName, lastName, phoneNumber);
        if (passportNumber == null || passportNumber.isBlank()) {
            throw new IllegalArgumentException("Passport number cannot be empty.");
        }
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.securityCleared = false;
        this.luggageWeightKg = 0.0;
    }

    /**
     * Marks this passenger as having passed security screening, making them eligible
     * for flight boarding.
     */
    public void passSecurityCheck() {
        this.securityCleared = true;
    }

    /**
     * Adds luggage weight to this passenger's cumulative total.
     *
     * @param weight the weight of the luggage item to add in kilograms; must be
     *               greater than {@code 0}
     * @throws IllegalArgumentException if {@code weight} is less than or equal to {@code 0}
     */
    public void addLuggageWeight(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Luggage weight must be positive.");
        }
        this.luggageWeightKg += weight;
    }

    /**
     * Returns the passenger's passport number.
     *
     * @return the passport number
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * Returns the passenger's nationality.
     *
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Indicates whether the passenger has passed security screening.
     *
     * @return {@code true} if the passenger is security-cleared; {@code false} otherwise
     */
    public boolean isSecurityCleared() {
        return securityCleared;
    }

    /**
     * Returns the cumulative weight of the passenger's luggage in kilograms.
     *
     * @return the total luggage weight in kilograms
     */
    public double getLuggageWeightKg() {
        return luggageWeightKg;
    }

    /**
     * Returns a formatted string representation of this passenger, including their full
     * name, passport number, nationality, security clearance state, and luggage weight.
     *
     * @return a descriptive string summarizing this passenger
     */
    @Override
    public String toString() {
        return "Passenger [" + getFullName()
                + ", Passport: " + passportNumber
                + ", Nationality: " + nationality
                + ", Cleared: " + (securityCleared ? "Yes" : "No")
                + ", Luggage: " + luggageWeightKg + "kg]";
    }
}