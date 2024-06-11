
package fi.tuni.prog3.weatherapp;

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
public class WeatherDataTest {
    
    private WeatherData wd;
    private DataFetcher df;
    
    public WeatherDataTest() {
        this.wd = new WeatherData();
        this.df = new DataFetcher();
    }
    
    
    @Test
    public void testFetchData() {
        
        assertFalse(wd.getLocationName().equals("Tampere"));
        assertTrue(wd.getLatitude() == 0);
        assertTrue(wd.getLongitude() == 0);
        assertTrue(wd.getWindSpeed() == 0);
        assertTrue(wd.getWeatherId() == 0);
        assertTrue(wd.getIconPath().equals(""));
        
        wd.fetchData(df, "Tampere");

        assertTrue(wd.getLocationName().equals("Tampere"));
        assertTrue(wd.getLatitude() != 0);
        assertTrue(wd.getLongitude() != 0);
        assertTrue(wd.getWindSpeed() != 0);
        assertTrue(wd.getWeatherId() != 0);
        assertFalse(wd.getIconPath().equals(""));
    }
    
}
