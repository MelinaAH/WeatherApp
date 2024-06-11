
package fi.tuni.prog3.weatherapp;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jasper
 */
public class WeatherForecastTest {
    
    private WeatherForecast instance;
    
    public WeatherForecastTest() {
        this.instance = new WeatherForecast();
    }
    

    
    @Test
    public void testGetDailyForecast() {
        
        assertTrue(instance.getDailyForecast().isEmpty());
        instance.fetchDailyData("Tampere");
        assertFalse(instance.getDailyForecast().isEmpty());
        
    }
    @Test
    public void testFetchDailyData() { 
        instance.fetchDailyData("Tampere");
        
        for(WeatherData wd : instance.getDailyForecast()) {
            assertEquals("Tampere",wd.getLocationName());
            assertTrue(wd.getDate() != null);
            assertFalse(wd.getIconPath().equals(""));
            assertTrue(wd.getMinTemp() <= wd.getMaxTemp());
        }
    }
    
 
    
}
