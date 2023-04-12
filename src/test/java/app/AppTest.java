/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AppTest {

    private static final long TIMEOUT = 3;

    @Test
    @Timeout(value = TIMEOUT)
    public void testArgOk(){
        String[] strings = {"ROUTE", "JAURES", "OURQ"};
        assertTrue(App.argsIsOk(strings));
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testNotArgOk(){
        String[] strings = {"ROUTE", "JAURES", "PARIS", "MARSEILLE"};
        assertFalse(App.argsIsOk(strings));
    }

    @Test
    @Timeout(value = TIMEOUT)
    public void testNotArgOkNoArgument(){
        String[] strings = {};
        assertFalse(App.argsIsOk(strings));
    }


}
