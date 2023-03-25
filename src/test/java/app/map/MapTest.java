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

    @Test
    public void checkFindPathDistOpt() {
        try {
            Station s1 = map.parseStation("Lourmel","2.2822419598550767, 48.83866086365992");
            Station s2 = map.parseStation("Commerce","2.293796842192864, 48.84461151236847");
            assertEquals(3,map.findPathDistOpt(s1,s2).size(),
            "check the output of findPathDistOpt");
        } catch (Map.PathNotFoundException e) {
            assertEquals(10,11,"hahahah");
            System.out.println(e.getMessage());
        } 
    }
    
    // @Test
    // public void checkPathNotFoundException() {
    //     Station s1 = map.parseStation("Commerce","2.2822419598550767, 48.83866086365992");
    //     Station s2 = map.parseStation("Lourmel","2.293796842192864, 48.84461151236847");
    //     assertThrows(Map.PathNotFoundException.class, () -> map.findPathDistOpt(s1,s2));
    //     //assertNull(map.findPathDistOpt(s1,s2));
    // }

    @Test
    public void checkParseStation() {
        Station s = map.parseStation("Lourmel","2.2822419598550767, 48.83866086365992");
        Connection c = map.getMap().get(s);
        assertEquals(2,c.getSections().size(),"verifier checkParseStation");
    }
}
