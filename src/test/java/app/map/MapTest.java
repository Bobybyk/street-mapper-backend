package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class MapTest {
    private Map map;

    public MapTest() {
        try {
            map = new Map("src/test/resources/map_data.csv");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void checkNbStation() {
        assertEquals(38, map.getMap().size(), "Check the number of station");
    }

    @Test
    public void fileNotFound() {
        assertThrows(FileNotFoundException.class, () -> new Map(""));
    }
}
