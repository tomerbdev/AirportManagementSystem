package ams;

public interface IRefuelable {
    void refuel(double liters);
    double getCurrentFuelLevel();
    double getMaxFuelCapacity();
}
