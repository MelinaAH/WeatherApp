package fi.tuni.prog3.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages file input/output for the WeatherApp. Handles reading from and
 * writing to a file.
 *
 * @author Melina
 */
public class FileManager implements iReadAndWriteToFile {

    private String fileName;
    private FavouritesManager favouritesManager;
    private ProgramStatus programStatus;

    /**
     * Constructor for FileManager class.
     *
     * @param fileName The name of the file for file operations.
     * @param favouritesManager Manages the favourite locations.
     * @param programStatus Manages the program's status, including the current
     * location.
     */
    public FileManager(String fileName, FavouritesManager favouritesManager, ProgramStatus programStatus) {
        this.fileName = fileName;
        this.favouritesManager = favouritesManager;
        this.programStatus = programStatus;
    }

    /**
     * Reads JSON data from the specified file and updates the favourites and
     * program status.
     *
     * @param fileName The name of the file to read from.
     * @return true if the read operation was successful, otherwise false.
     * @throws Exception if there's an issue reading the file.
     */
    @Override
    public boolean readFromFile(String fileName) throws Exception {
        try {
            Path path = Paths.get(fileName);
            String jsonData = Files.readString(path);

            if (jsonData != null && !jsonData.isEmpty()) {
                JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

                String favouritesJson = jsonObject.get("favourites").getAsString();
                System.out.println("readFromFile favouritesJson: " + favouritesJson);
                favouritesManager.setFavouritesFromJSON(favouritesJson);

                String location = jsonObject.get("location").getAsString();
                System.out.println("readFromFile readLocation: " + location);
                programStatus.setCurrentLocation(location);

                return true;
            } else {
                System.out.println("The file is empty or its content couln't be read.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Writes the favourites and program status (current location and favourites)
     * as JSON into the specified file.
     *
     * @param fileName The name of the file to write to.
     * @return true if the write operation was successful, otherwise false.
     * @throws Exception if there's an issue writing to the file.
     */
    @Override
    public boolean writeToFile(String fileName) throws Exception {
        try {
            Path path = Paths.get(fileName);
            // Before writing the data, it is ensured that there is no wrong 
            // data left in the file by clearing the contents of the file by 
            // writing an empty string
            Files.writeString(path, "");

            JsonObject jsonObject = new JsonObject();

            String favouritesAsJson = convertFavouritesToJSON();
            jsonObject.addProperty("favourites", favouritesAsJson);

            String currentLocation = programStatus.getCurrentLocation();
            jsonObject.addProperty("location", currentLocation);

            String jsonData = jsonObject.toString();
            System.out.println("writeToFile jsonData: " + jsonData);
            Files.writeString(path, jsonData);
            return true;
        } catch (IOException e) {
            throw new Exception("Error writing file: " + e.getMessage());
        }
    }

    /**
     * Converts the list of favourites to JSON format using Gson.
     *
     * @return A string containing the favourites in JSON format.
     */
    public String convertFavouritesToJSON() {
        Gson gson = new Gson();
        return gson.toJson(favouritesManager.getFavourites());
    }
}
