
package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class FileManagerTest {
    
    
    private final String testFileName = "testFile.json";
    // Test file:
    //{"favourites":"[\"Tampere\",\"Turku\",\"Helsinki\"]","location":"Turku"}
    
    public FileManagerTest() {
    }
    
    //Delete writeTest.json file after testing
    @AfterAll
    public static void tearDown() {
        try {
            Files.deleteIfExists(Paths.get("writeTest.json"));
        } catch (Exception e) {
            fail("Exception occurred during tearDown: " + e.getMessage());
        }
    }

    @Test
    public void testReadFromFile() {
        try {
            FavouritesManager fm = new FavouritesManager();
            ProgramStatus status = new ProgramStatus();
            FileManager fileManager = new FileManager(testFileName, fm, status);
            
            assertTrue(fileManager.readFromFile(testFileName));
            assertEquals("Turku", status.getCurrentLocation());
            assertTrue(fm.getFavourites().contains("Tampere"));
            assertTrue(fm.getFavourites().contains("Helsinki"));
            assertFalse(fm.getFavourites().contains("Oulu"));

        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testWriteToFile() {
        try {
            Files.createFile(Paths.get("writeTest.json"));
            FavouritesManager fm = new FavouritesManager();
            ProgramStatus status = new ProgramStatus();
            FileManager fileManager = new FileManager("writeTest.json", fm, status);
            
            fm.addFavourite("Lappi");
            status.setCurrentLocation("Joensuu");
           
            assertTrue(fileManager.writeToFile("writeTest.json"));

        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

   
}
