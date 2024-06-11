package fi.tuni.prog3.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A class for managing favourite weather locations.
 * 
 * This class manages a list of favourite weather locations. It allows adding,
 * removing, checking, retrieving, and clearing favourite locations.
 *
 * @author Roosa
 */
public class FavouritesManager {

    private ArrayList<String> favourites;

    /**
     * Constructor for FavouritesManager class. Initializes an empty list of
     * favourites.
     */
    public FavouritesManager() {
        this.favourites = new ArrayList<>();
    }

    /**
     * Adds a location to the list of favourites if it doesn't exist already.
     *
     * @param location The location to be added to the favourites list.
     * @return true if the location was added, false otherwise.
     */
    public boolean addFavourite(String location) {
        if (!favourites.contains(location)) {
            favourites.add(location);
            return true;
        }
        return false;
    }

    /**
     * Removes a location from the list of favourites.
     *
     * @param location The location to be removed from the favourites list.
     * @return true if the location was removed, false if it did not exist.
     */
    public boolean removeFavourite(String location) {
        return favourites.remove(location);
    }

    /**
     * Checks if a location is in the list of favourites.
     *
     * @param location The location to be checked.
     * @return true if the location is a favourite, false otherwise.
     */
    public boolean isFavourite(String location) {
        return favourites.contains(location);
    }

    /**
     * Retrieves a copy of the list of favourite locations.
     *
     * @return ArrayList containing favourite locations.
     */
    public ArrayList<String> getFavourites() {
        return new ArrayList<>(favourites);
    }

    /**
     * Clears the list of favourite locations.
     */
    public void clearFavourites() {
        favourites.clear();
    }

    /**
     * Sets favourites from JSON format.
     *
     * @param json The JSON string containing favourite locations.
     */
    public void setFavouritesFromJSON(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        try {
            ArrayList<String> loadedFavourites = gson.fromJson(json, type);
            if (loadedFavourites != null) {
                favourites.clear();
                favourites.addAll(loadedFavourites);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
}