package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestAllSensorsVsPredefinedFilesP1 {
    /* To perform this test case:
    * The IceBox server must be turned on.
    *
    * The line of code to print out the file that the function readData() in Sensor class is added:
    *
    * System.out.println("Successfully read file " + this.username + this.type +".");
    *
    * Data resources for this test case are files (located in same directory):
    * JackLocation/Temperature/AQI
    * InvalidJackLocation/Temperature/AQI
    * OutOfBoundJackLocation/Temperature/AQI
    * */

    private final PrintStream systemOut = System.out;
    private final PrintStream systemErr = System.err;
    private ByteArrayOutputStream testOut;
    private ByteArrayOutputStream testErr;

    private static AllSensors allSensors;
    private static String username;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        testErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(systemErr));
    }

    @AfterEach
    public void restoreSystemOutputAndCleanUp() {
        System.setOut(systemOut);
        System.setErr(systemErr);

        // stop communicator of AllSensors - which is started in the constructor of AllSensors
        if (allSensors != null) {
            allSensors.stop();
        }
    }

    @Test
    @DisplayName("TC: Check AllSensors temperature file reading")
    void testAllSensorsTemperatureFileReading(){
        username = "Jack";
        allSensors = new AllSensors(username);

        assertTrue(testOut.toString().contains("Successfully read file JackTemperature."));
    }

    @Test
    @DisplayName("TC: Check AllSensors AQI file reading")
    void testAllSensorsAQIFileReading(){
        username = "Jack";
        allSensors = new AllSensors(username);

        assertTrue(testOut.toString().contains("Successfully read file JackAQI."));
    }

    @Test
    @DisplayName("TC: Check AllSensors Location file reading")
    void testAllSensorsLocationFileReading(){
        username = "Jack";
        allSensors = new AllSensors(username);

        assertTrue(testOut.toString().contains("Successfully read file JackLocation."));
    }

    @Test
    @DisplayName("TC: Check AllSensors INVALID files reading")
    void testAllSensorsInvalidFilesReading(){
        username = "InvalidJack";
        assertThrows(Exception.class, () -> new AllSensors(username));
    }

    @Test
    @DisplayName("TC: Check AllSensors files reading containing out of bound values")
    void testAllSensorsOutOfBoundFileReading(){
        username = "OutOfBoundJack";
        assertThrows(Exception.class, () -> new AllSensors(username));
    }


}
