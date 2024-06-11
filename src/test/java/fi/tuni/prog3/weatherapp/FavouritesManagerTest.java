
package fi.tuni.prog3.weatherapp;

import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jasper
 */
public class FavouritesManagerTest {
    
    private FavouritesManager fm;
    
    public FavouritesManagerTest() {
        this.fm = new FavouritesManager();
    }
    
    
    @Test
    public void testAddFavourite() {
        String location = "Helsinki";
        assertTrue(fm.addFavourite(location));
        assertFalse(fm.addFavourite(location));
    }

    @Test
    public void testRemoveFavourite() {
        String location = "Tampere";
        fm.addFavourite(location);
        assertTrue(fm.removeFavourite(location));
        assertFalse(fm.removeFavourite(location));
    }

    @Test
    public void testIsFavourite() {
        String location = "Turku";
        assertFalse(fm.isFavourite(location));
        fm.addFavourite(location);
        assertTrue(fm.isFavourite(location));
    }

    @Test
    public void testClearFavourites() {
        String location = "Espoo";
        fm.addFavourite(location);
        fm.clearFavourites();
        assertFalse(fm.isFavourite(location));
    }

    @Test
    public void testSetFavouritesFromJSON() {
        String json = "[\"Rovaniemi\",\"Vaasa\",\"Kuopio\"]";
        fm.setFavouritesFromJSON(json);

        assertTrue(fm.isFavourite("Rovaniemi"));
        assertTrue(fm.isFavourite("Vaasa"));
        assertTrue(fm.isFavourite("Kuopio"));
        assertFalse(fm.isFavourite("Tampere"));
    }
    
}
