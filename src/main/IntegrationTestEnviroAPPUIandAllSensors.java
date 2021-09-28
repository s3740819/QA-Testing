package main;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class IntegrationTestEnviroAPPUIandAllSensors {
    //EnviroAPPUI
    private static EnviroAPPUI enviroAPPUI;

    //Configuration for testing through I/O stream
    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;

    //Set up I/O stream
    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }
    @AfterEach
    public void restoreSystemOutputAndCleanUp() {
        System.setOut(systemOut);
    }

    @Test
    @DisplayName("Check AllSensors username update when receiving username from EnviroAPPUI")
    public void testUsernameSentFromUIToAllSensors(){
        String input = "Jack";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.allSensors = new AllSensors(EnviroAPPUI.username);
        assertEquals("--- From AllSensors --- Successfully received and updated username Jack.",testOut.toString().split("\\n")[testOut.toString().split("\\n").length-1]);
    }
}
