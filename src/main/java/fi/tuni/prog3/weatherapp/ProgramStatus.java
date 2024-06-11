package fi.tuni.prog3.weatherapp;

/**
 * ProgramStatus class represents the status of the WeatherApp program. It keeps
 * track of the current location set by the user within the program.
 *
 * @author Melina
 */
public class ProgramStatus {

    private String currentLocation;

    /**
     * Constructs a ProgramStatus object with an initial empty current location.
     */
    public ProgramStatus() {
        this.currentLocation = "";
    }

    /**
     * Retrieves the current location within the WeatherApp program.
     *
     * @return A String representing the current location
     */
    public String getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Sets the current location within the WeatherApp program and prints the
     * updated location for testing purposes, if location is invalid it is set to
     * default value of "Helsinki".
     *
     * @param location A String representing the new location to be set
     */
    public void setCurrentLocation(String location) {
        WeatherForecast wf = new WeatherForecast();
        if(wf.fetchDailyData(location)) {
            this.currentLocation = location;
        } else {
            this.currentLocation = "Helsinki";
        }
        System.out.println("ProgramStatus currentLocation: " + location);
    }

}
