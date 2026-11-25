package ams;

/**
 * Represents a base individual within the Aircraft Management System (AMS).
 *
 * Encapsulates core identity and contact details shared across specialized human
 * roles in the system, such as {@link Passenger} and {@link Pilot}.
 */
public class Person {

    /**
     * The unique system identifier assigned to the person.
     */
    private final String personId;

    /**
     * The person's given (first) name.
     */
    private final String firstName;

    /**
     * The person's family (last) name.
     */
    private final String lastName;

    /**
     * The person's contact telephone number.
     */
    private String phoneNumber;

    /**
     * Constructs a new {@code Person} with the specified identifier, name, and contact
     * phone number.
     *
     * @param personId    the unique identifier for the person; must not be {@code null}
     *                    or blank
     * @param firstName   the person's given name; must not be {@code null} or blank
     * @param lastName    the person's family name; must not be {@code null} or blank
     * @param phoneNumber the person's contact phone number
     * @throws IllegalArgumentException if {@code personId}, {@code firstName}, or
     *                                  {@code lastName} is {@code null} or blank
     */
    //Full ctor
    public Person(String personId, String firstName, String lastName, String phoneNumber) {
        if (personId == null || personId.isBlank()) {
            throw new IllegalArgumentException("Person ID cannot be empty.");
        }
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("First and last name cannot be empty.");
        }
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the person's full name formatted as {@code "<firstName> <lastName>"}.
     *
     * @return the combined first and last name separated by a single space
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Updates the person's contact phone number.
     *
     * @param newPhone the new phone number to assign; must not be {@code null} or blank
     * @throws IllegalArgumentException if {@code newPhone} is {@code null} or blank
     */
    public void updatePhoneNumber(String newPhone) {
        if (newPhone == null || newPhone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }
        this.phoneNumber = newPhone;
    }

    /**
     * Returns the unique identifier of the person.
     *
     * @return the person's ID
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * Returns the person's given name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the person's family name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the person's current contact phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns a formatted string representation of this person, including their ID,
     * full name, and phone number.
     *
     * @return a descriptive string summarizing this person
     */
    @Override
    public String toString() {
        return "Person [ID: " + personId + ", Name: " + getFullName() + ", Phone: " + phoneNumber + "]";
    }
}