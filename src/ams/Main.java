package ams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;

/**
 * Entry point and interactive console interface for the Airport Management System (AMS).
 *
 * <p>Initializes a {@link ControlTower} with realistic seed data and runs a continuous
 * menu-driven loop allowing operators to register aircraft, schedule flights, board
 * passengers, refuel aircraft, update weather conditions, complete flights, and inspect
 * airport status.</p>
 */
public class Main {

    /**
     * Date-time format expected when operators enter flight departure and arrival timestamps.
     */
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Launches the Airport Management System console application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        ControlTower tower = new ControlTower("LLBG", new WeatherReport(12.0, 10.0, false));
        seedAirportData(tower);

        try (Scanner scanner = new Scanner(System.in)) {
            runMenu(tower, scanner);
        }
    }

    /**
     * Populates the control tower with initial runways, helipads, pilots, and aircraft
     * so all operational use cases can be tested immediately at startup.
     *
     * @param tower the {@link ControlTower} to populate with seed data
     */
    private static void seedAirportData(ControlTower tower) {
        // Runways & Helipads
        tower.addRunway(new Runway("RWY-08", 3657.0, 400000.0, false));
        tower.addRunway(new Runway("RWY-12", 2700.0, 250000.0, false));
        tower.addRunway(new Runway("PAD-H1", 40.0, 15000.0, true));

        // Pilots
        tower.registerPilot(new Pilot(
                "206364846", "Tomer", "Berkovich", "050-1111111",
                "PIL-101", Set.of(LicenseType.COMMERCIAL_PLANE, LicenseType.HELICOPTER),
                1250.0, true));
        tower.registerPilot(new Pilot(
                "301122334", "Noa", "Levi", "052-2222222",
                "PIL-102", Set.of(LicenseType.COMMERCIAL_PLANE),
                640.0, false));
        tower.registerPilot(new Pilot(
                "309988776", "Avi", "Cohen", "054-3333333",
                "PIL-103", Set.of(LicenseType.HELICOPTER),
                820.0, true));

        // Aircraft
        tower.registerAircraft(new CommercialPlane(
                "4X-EKA", "Boeing 787-9", 254000.0,
                60.1, 2800.0, 282, 126000.0, 90000.0));
        tower.registerAircraft(new CommercialPlane(
                "4X-EKB", "Boeing 737-800", 79000.0,
                35.8, 2100.0, 189, 26000.0));
        tower.registerAircraft(new Helicopter(
                "4X-H01", "Bell 429", 3175.0,
                11.0, false, 820.0, 600.0));
        tower.registerAircraft(new Helicopter(
                "4X-MED", "UH-60 Black Hawk MedEvac", 9979.0,
                16.36, true, 1360.0, 1100.0));
    }

    /**
     * Runs the main interactive menu loop until the operator chooses to exit.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} reading user input from standard input
     */
    private static void runMenu(ControlTower tower, Scanner scanner) {
        boolean running = true;
        while (running) {
            printMenu(tower);
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> handleRegisterAircraft(tower, scanner);
                    case "2" -> handleScheduleFlight(tower, scanner);
                    case "3" -> handleBoardPassenger(tower, scanner);
                    case "4" -> handleRefuelAircraft(tower, scanner);
                    case "5" -> handleUpdateWeather(tower, scanner);
                    case "6" -> handleCompleteFlight(tower, scanner);
                    case "7" -> printAirportStatus(tower);
                    case "0" -> {
                        System.out.println("Shutting down Airport Management System. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("[ERROR] Invalid choice. Please enter a number from 0 to 7.");
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("[INPUT ERROR] Invalid number or date format: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e) {
                System.out.println("[OPERATION DENIED] " + e.getMessage());
            }
        }
    }

    /**
     * Displays the control tower's primary operator menu.
     *
     * @param tower the active {@link ControlTower}
     */
    private static void printMenu(ControlTower tower) {
        System.out.println("\n==========================================");
        System.out.println("   AMS CONTROL TOWER - " + tower.getAirportIcaoCode());
        System.out.println("   " + tower.getCurrentWeather());
        System.out.println("==========================================");
        System.out.println("1. Register New Aircraft");
        System.out.println("2. Schedule New Flight (Auto-Assign Runway/Helipad)");
        System.out.println("3. Register & Board Passenger onto Flight");
        System.out.println("4. Refuel Aircraft");
        System.out.println("5. Update Weather Conditions");
        System.out.println("6. Complete Flight (Log Hours & Maintenance Check)");
        System.out.println("7. Display Airport Status Dashboard");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    /**
     * Prompts the operator for aircraft details and registers either a {@link CommercialPlane}
     * or a {@link Helicopter} with the control tower.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} for reading user input
     */
    private static void handleRegisterAircraft(ControlTower tower, Scanner scanner) {
        System.out.print("Select aircraft type (1 = Commercial Plane, 2 = Helicopter): ");
        String type = scanner.nextLine().trim();

        System.out.print("Tail Number (e.g., 4X-ABC): ");
        String tail = scanner.nextLine().trim();
        System.out.print("Model Name: ");
        String model = scanner.nextLine().trim();
        System.out.print("Max Takeoff Weight (kg): ");
        double maxWeight = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Max Fuel Capacity (liters): ");
        double maxFuel = Double.parseDouble(scanner.nextLine().trim());

        if ("1".equals(type)) {
            System.out.print("Wingspan (meters): ");
            double wingspan = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Minimum Runway Length (meters): ");
            double minRunway = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Passenger Capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine().trim());

            CommercialPlane plane = new CommercialPlane(
                    tail, model, maxWeight, wingspan, minRunway, capacity, maxFuel);
            tower.registerAircraft(plane);
            System.out.println("[SUCCESS] Registered " + plane);
        } else if ("2".equals(type)) {
            System.out.print("Main Rotor Diameter (meters): ");
            double rotor = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Is this a Medical Evacuation (MedEvac) helicopter? (true/false): ");
            boolean medEvac = Boolean.parseBoolean(scanner.nextLine().trim());

            Helicopter heli = new Helicopter(tail, model, maxWeight, rotor, medEvac, maxFuel, maxFuel);
            tower.registerAircraft(heli);
            System.out.println("[SUCCESS] Registered " + heli);
        } else {
            System.out.println("[ERROR] Unknown aircraft type selection.");
        }
    }

    /**
     * Prompts the operator for flight parameters, validates aircraft and pilot eligibility,
     * and schedules the flight through the control tower.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} for reading user input
     */
    private static void handleScheduleFlight(ControlTower tower, Scanner scanner) {
        System.out.println("\nAvailable Aircraft:");
        for (Aircraft a : tower.getAllAircrafts()) {
            System.out.println("  - " + a);
        }
        System.out.println("Available Pilots:");
        for (Pilot p : tower.getPilots()) {
            System.out.println("  - " + p);
        }

        System.out.print("\nFlight Number (e.g., LY315): ");
        String flightNum = scanner.nextLine().trim();
        System.out.print("Aircraft Tail Number: ");
        String tail = scanner.nextLine().trim();
        System.out.print("Pilot License Number: ");
        String license = scanner.nextLine().trim();

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        System.out.println("Suggested Departure: " + now.format(DATE_TIME_FORMAT));
        System.out.print("Departure (yyyy-MM-dd HH:mm, or press Enter for suggested): ");
        String depInput = scanner.nextLine().trim();
        LocalDateTime departure = depInput.isEmpty() ? now : LocalDateTime.parse(depInput, DATE_TIME_FORMAT);

        LocalDateTime defaultArr = departure.plusHours(2);
        System.out.print("Arrival (yyyy-MM-dd HH:mm, or press Enter for +2h): ");
        String arrInput = scanner.nextLine().trim();
        LocalDateTime arrival = arrInput.isEmpty() ? defaultArr : LocalDateTime.parse(arrInput, DATE_TIME_FORMAT);

        Flight flight = tower.scheduleFlight(flightNum, tail, license, departure, arrival);
        System.out.println("[SUCCESS] Scheduled " + flight);
    }

    /**
     * Registers a passenger, processes luggage and security clearance, and boards them
     * onto an existing scheduled flight.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} for reading user input
     */
    private static void handleBoardPassenger(ControlTower tower, Scanner scanner) {
        System.out.print("Target Flight Number: ");
        String flightNum = scanner.nextLine().trim();
        Flight flight = tower.findFlightByNumber(flightNum);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found: " + flightNum);
        }

        System.out.print("Passenger ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("First Name: ");
        String first = scanner.nextLine().trim();
        System.out.print("Last Name: ");
        String last = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Passport Number: ");
        String passport = scanner.nextLine().trim();
        System.out.print("Nationality: ");
        String nationality = scanner.nextLine().trim();

        Passenger passenger = new Passenger(id, first, last, phone, passport, nationality);

        System.out.print("Luggage Weight (kg, 0 if none): ");
        double luggage = Double.parseDouble(scanner.nextLine().trim());
        if (luggage > 0) {
            passenger.addLuggageWeight(luggage);
        }

        System.out.print("Did passenger pass security screening? (yes/no): ");
        String cleared = scanner.nextLine().trim();
        if ("yes".equalsIgnoreCase(cleared) || "y".equalsIgnoreCase(cleared)) {
            passenger.passSecurityCheck();
        }

        if (flight.boardPassenger(passenger)) {
            System.out.println("[SUCCESS] Boarded " + passenger.getFullName()
                    + ". Updated flight payload: " + flight.calculateTotalPayload() + "kg");
        } else {
            System.out.println("[BOARDING DENIED] Passenger failed security clearance, is already boarded, or flight is full.");
        }
    }

    /**
     * Prompts for an aircraft tail number and fuel quantity to add, delegating to
     * {@link ControlTower#refuelAircraft(String, double)}.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} for reading user input
     */
    private static void handleRefuelAircraft(ControlTower tower, Scanner scanner) {
        System.out.print("Aircraft Tail Number to refuel: ");
        String tail = scanner.nextLine().trim();
        System.out.print("Liters to add: ");
        double liters = Double.parseDouble(scanner.nextLine().trim());

        tower.refuelAircraft(tail, liters);
    }

    /**
     * Prompts for updated meteorological parameters and applies them to the control tower,
     * triggering selective grounding of weather-restricted flights.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} for reading user input
     */
    private static void handleUpdateWeather(ControlTower tower, Scanner scanner) {
        System.out.print("Wind Speed (knots): ");
        double wind = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Visibility (km): ");
        double visibility = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Is it stormy? (true/false): ");
        boolean stormy = Boolean.parseBoolean(scanner.nextLine().trim());

        tower.updateWeatherConditions(new WeatherReport(wind, visibility, stormy));
    }

    /**
     * Prompts for an active flight number and marks it completed in the control tower.
     *
     * @param tower   the active {@link ControlTower}
     * @param scanner the {@link Scanner} for reading user input
     */
    private static void handleCompleteFlight(ControlTower tower, Scanner scanner) {
        System.out.print("Flight Number to complete: ");
        String flightNum = scanner.nextLine().trim();
        tower.completeFlight(flightNum);
    }

    /**
     * Prints a complete operational snapshot of runways, aircraft, pilots, and flights.
     *
     * @param tower the active {@link ControlTower}
     */
    private static void printAirportStatus(ControlTower tower) {
        System.out.println("\n--- RUNWAYS & HELIPADS ---");
        for (Runway r : tower.getRunways()) {
            System.out.println("  " + r);
        }

        System.out.println("\n--- REGISTERED AIRCRAFT ---");
        for (Aircraft a : tower.getAllAircrafts()) {
            System.out.println("  " + a);
        }

        System.out.println("\n--- REGISTERED PILOTS ---");
        for (Pilot p : tower.getPilots()) {
            System.out.println("  " + p);
        }

        System.out.println("\n--- SCHEDULED & COMPLETED FLIGHTS ---");
        if (tower.getScheduledFlights().isEmpty()) {
            System.out.println("  (No flights scheduled yet)");
        } else {
            for (Flight f : tower.getScheduledFlights()) {
                System.out.println("  " + f);
                for (Passenger pax : f.getPassengers()) {
                    System.out.println("      * " + pax);
                }
            }
        }
    }
}