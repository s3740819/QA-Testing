/* Reference: https://stackoverflow.com/questions/15938538/how-can-i-make-a-junit-test-wait/35163873#35163873 */

package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestAllSensorsVsPredefinedFilesP2 {
    /* To perform this test case:
    * The IceBox server must be turned on.
    *
    * Data resources for this test case are files (located in same directory):
    * EverySecondJackTemperature/AQI/Location
    * LoopBackJackTemperature/AQI/Location
    * */
    private final PrintStream systemOut = System.out;
    private final PrintStream systemErr = System.err;
    private ByteArrayOutputStream testOut;
    private ByteArrayOutputStream testErr;

    private static AllSensors allSensors;
    private static String username;

    private final CountDownLatch waiter = new CountDownLatch(1);

    private static final int OFFSET = 100; //processing time for program to change value

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        testErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(systemErr));
    }

    @AfterEach
    public void cleanUp() {
        System.setOut(systemOut);
        System.setErr(systemErr);

        // stop communicator of AllSensors - which is started in the constructor of AllSensors
        if (allSensors != null) {
            allSensors.stop();
        }
    }

    @Test
    @DisplayName("TC: Check AllSensors temperature file reading when it reads new value every second")
    void testAllSensorsTemperatureFileEverySecond(){
        username = "EverySecondJack";
        allSensors = new AllSensors(username);

        allSensors.run();

        assertAll(() -> {
            assertEquals("12",allSensors.temperatureSensor.getValue());
            waiter.await(3*1000+OFFSET, TimeUnit.MILLISECONDS); // after 3 seconds (slightly over a bit)
            assertEquals("30",allSensors.temperatureSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("42",allSensors.temperatureSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("50",allSensors.temperatureSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertNotEquals("50",allSensors.temperatureSensor.getValue()); // after 1 second, value is no longer 50
        });
    }

    @Test
    @DisplayName("TC: Check AllSensors AQI file reading when it reads new value every second")
    void testAllSensorsAQIFileEverySecond(){
        username = "EverySecondJack";
        allSensors = new AllSensors(username);

        allSensors.run();

        assertAll(() -> {
            assertEquals("65",allSensors.aqiSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertEquals("142",allSensors.aqiSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("165",allSensors.aqiSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("40",allSensors.aqiSensor.getValue());
            waiter.await(4*1000+OFFSET, TimeUnit.MILLISECONDS); // after 4 seconds (slightly over a bit)
            assertNotEquals("40",allSensors.aqiSensor.getValue()); // the result must be no longer "40"
        });
    }

    @Test
    @DisplayName("TC: Check AllSensors Location file reading when it reads new value every second")
    void testAllSensorsLocationFileEverySecond(){
        username = "EverySecondJack";
        allSensors = new AllSensors(username);

        allSensors.run();

        assertAll(() -> {
            assertEquals("A",allSensors.locationSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("C",allSensors.locationSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertEquals("B",allSensors.locationSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("D",allSensors.locationSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertNotEquals("D",allSensors.locationSensor.getValue()); // the result must be no longer "D"
        });
    }

    @Test
    @DisplayName("TC: Check AllSensors temperature file reading whether it loops back")
    void testAllSensorsTemperatureFileLoopBack(){
        username = "LoopBackJack";
        allSensors = new AllSensors(username);

        allSensors.run();

        assertAll(() -> {
            assertEquals("12",allSensors.temperatureSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("30",allSensors.temperatureSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("12",allSensors.temperatureSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("30",allSensors.temperatureSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("12",allSensors.temperatureSensor.getValue());
        });
    }

    @Test
    @DisplayName("TC: Check AllSensors AQI file reading whether it loops back")
    void testAllSensorsAQIFileLoopBack(){
        username = "LoopBackJack";
        allSensors = new AllSensors(username);

        allSensors.run();

        assertAll(() -> {
            assertEquals("65",allSensors.aqiSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertEquals("142",allSensors.aqiSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("65",allSensors.aqiSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertEquals("142",allSensors.aqiSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("65",allSensors.aqiSensor.getValue());
        });
    }

    @Test
    @DisplayName("TC: Check AllSensors Location file reading whether it loops back")
    void testAllSensorsLocationFileLoopBack(){
        username = "LoopBackJack";
        allSensors = new AllSensors(username);

        allSensors.run();

        assertAll(() -> {
            assertEquals("A",allSensors.locationSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("C",allSensors.locationSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertEquals("A",allSensors.locationSensor.getValue());
            waiter.await(2*1000+OFFSET, TimeUnit.MILLISECONDS); // after 2 seconds (slightly over a bit)
            assertEquals("C",allSensors.locationSensor.getValue());
            waiter.await(1*1000+OFFSET, TimeUnit.MILLISECONDS); // after 1 second (slightly over a bit)
            assertEquals("A",allSensors.locationSensor.getValue());
        });
    }


}
