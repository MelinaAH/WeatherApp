
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
public class DataFetcherTest {
    
    private DataFetcher df;
    static final double LAT_TAMPERE = 61.4980214;
    static final double LON_TAMPERE = 23.7603118;
    
    public DataFetcherTest() {
        this.df = new DataFetcher();
    }

 
    @Test
    public void testLookUpLocation() {
        String result = df.lookUpLocation("Tampere");
        assertEquals(LAT_TAMPERE+","+LON_TAMPERE+","+"Tampere",result);
        System.out.println("Coords: " + result);
    }
    
    @Test
    public void testGetCurrentWeather() {
        String result = df.getCurrentWeather(LAT_TAMPERE, LON_TAMPERE);
        assertNotNull(result);
        System.out.println("Current Weather: " + result);
    }
    
    @Test
    public void testGetForecast() {
        String result = df.getForecast(LAT_TAMPERE, LON_TAMPERE);
        assertNotNull(result);
        System.out.println("Forecast: " + result);
    }
    
    @Test
    public void testGetHourlyForecast() {
        String result = df.getHourlyForecast(LAT_TAMPERE, LON_TAMPERE);
        assertNotNull(result);
        System.out.println("Hourly Forecast: " + result);
    }
    
}
