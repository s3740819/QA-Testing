package main;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.IceStorm.TopicPrx;
import helper.ContextManagerWorker;
import helper.MonitorPrx;
import helper.SensorData;
import org.junit.jupiter.api.*;
import support.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestAllSensorsVsContextManagerP1 {
    /* To perform this test case:
    * The IceBox server must be turned on.
    * LocationServer must be turned on
    * PreferenceRepository must be turned on
    *
    * Some new variables (with "to do" tage) is added inside ContextManager for testing automation
    * */
    private final CountDownLatch waiter = new CountDownLatch(1);
    private static String username;

    private static WeatherAlarms weatherAlarms = new WeatherAlarms();


    /*** Code for test section ***/
    @BeforeAll
    public static void setUpBeforeClass() {
        username = "Jack";
        Handler.initialize(weatherAlarms);

        ContextManager.currentWeather = 0; // init to normal weather
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username, new Current());
    }
    @AfterAll
    public static void reset(){
        weatherAlarms.communicator.shutdown();
    }
    @AfterEach
    public void cleanUpAfterAll(){
        Handler.reset();
        setUpBeforeClass();
    }



    @BeforeEach
    public void setUpBeforeEach(){
        ContextManager.isTemperatureReached = false;
        ContextManager.isAPOReached = false;
        ContextManager.isWeatherReached = false;
        ContextManager.countTemperatureAlert = 0;
    }

    @Test
    @DisplayName("TC: Check ContextManager receiving temperature from AllSensors")
    void testContextManagerReceivesTemperatureFromAllSensors(){
        int testTemperatureData = 30;

        // create test SensorData
        SensorData data = new SensorData(username, "C", testTemperatureData, 25);
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(testTemperatureData, ContextManager.receivedSensorDataFromAllSensors.temperature);
    }

    @Test
    @DisplayName("TC: Check ContextManager receiving AQI from AllSensors")
    void testContextManagerReceivesAQIFromAllSensors(){
        int testAQIData = 150;

        // create test SensorData
        SensorData data = new SensorData(username, "C", 10, testAQIData);

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(testAQIData, ContextManager.receivedSensorDataFromAllSensors.aqi);
    }


    @Test
    @DisplayName("TC: Check ContextManager evaluation when current temperature is smaller than user temperature threshold")
    void testContextManagerEvaluationWhenCurrentTemperatureSmallerThanThreshold(){

        int testTemperatureData = 15; // temperature smaller threshold of Jack user (=30)

        // create test SensorData
        SensorData data = new SensorData(username, "C", testTemperatureData, 25);

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isTemperatureReached); // temperature threshold should NOT be triggered
        assertEquals(0, ContextManager.countTemperatureAlert); // no alertion should be made
    }

    @Test
    @DisplayName("TC: Check ContextManager evaluation when current temperature is higher than user temperature threshold")
    void testContextManagerEvaluationWhenCurrentTemperatureHigherThanThreshold(){

        int testTemperatureData = 45; // temperature smaller threshold of Jack user (=30)

        // create test SensorData
        SensorData data = new SensorData(username, "C", testTemperatureData, 25);

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager
        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isTemperatureReached); // temperature threshold should NOT be triggered
        assertEquals(0, ContextManager.countTemperatureAlert); // no alertion should be made
    }

    @Test
    @DisplayName("TC: Check ContextManager evaluation when current temperature is equal to user temperature threshold")
    void testContextManagerEvaluationWhenCurrentTemperatureEqualToThreshold(){

        int testTemperatureData = 30; // temperature smaller threshold of Jack user (=30)

        // create test SensorData
        SensorData data = new SensorData(username, "C", testTemperatureData, 25);

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager
        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertTrue(ContextManager.isTemperatureReached); // temperature threshold should reach
        assertNotEquals(0, ContextManager.countTemperatureAlert); // alertion should be made (no longer 0)
    }

    @Test
    @DisplayName("TC: Check ContextManager evaluation when temperature does not reach threshold and unchanged")
    void testContextManagerEvaluationWhenCurrentTemperatureNotReachThresholdAndUnchanged(){

        int testTemperatureData = 15; // temperature smaller threshold of Jack user (=30)

        // create test SensorData
        SensorData data = new SensorData(username, "C", testTemperatureData, 25);
        // send data to ContextManager the first time
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current());
        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isTemperatureReached); // temperature threshold should not be reached
        assertEquals(0, ContextManager.countTemperatureAlert); // no alertion

        SensorData unchangedTemperatureData = new SensorData(username, "A", testTemperatureData, 55);
        monitorI.report(unchangedTemperatureData, new Current()); // send data AGAIN with the temperature unchanged
        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isTemperatureReached); // temperature threshold still not be reached
        assertEquals(0, ContextManager.countTemperatureAlert); // no alertion should be made
    }

    @Test
    @DisplayName("TC: Check ContextManager evaluation when temperature already reached threshold and unchanged")
    void testContextManagerEvaluationWhenCurrentTemperatureReachThresholdAndUnchanged(){

        int testTemperatureData = 30; // temperature smaller threshold of Jack user (=30)

        // create test SensorData
        SensorData data = new SensorData(username, "C", testTemperatureData, 25);
        // send data to ContextManager the first time
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current());
        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertTrue(ContextManager.isTemperatureReached); // temperature threshold is reached
        assertEquals(1, ContextManager.countTemperatureAlert); // ContextManager alert 1 time

        SensorData unchangedTemperatureData = new SensorData(username, "A", testTemperatureData, 55);
        monitorI.report(unchangedTemperatureData, new Current()); // send data AGAIN with the temperature unchanged
        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertTrue(ContextManager.isTemperatureReached); // temperature threshold is still reached
        assertEquals(1, ContextManager.countTemperatureAlert); // but no more alertion should be made, alertion should be still 1
    }

    @Test
    @DisplayName("TC: Check ContextManager timer reset when user changes from outdoor to indoor")
    void testContextManagerTimerResetWhenLocationChangesFromOutdoorToIndoor(){
        String previousLocation = "C"; // outdoor
        String currentLocation = "A"; // indoor


        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, 25); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username, previousLocation, 15, 35); // previous location is INDOOR
        ContextManager.users.get(username).clock = 45; // assuming thing the clock is already running for 45s
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send current location to ContextManager

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0, ContextManager.users.get(username).clock); // expect clock to be reset to 0
    }

    @Test
    @DisplayName("TC: Check ContextManager timer not count when current location is indoor")
    void testContextManagerTimerNotCountWhenCurrentLocationIsIndoor(){
        String currentLocation = "A"; // indoor

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, 25); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send current location to ContextManager

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0, ContextManager.users.get(username).clock); // expect clock to be still 0

        monitorI.report(data, new Current()); // continue to send data

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0, ContextManager.users.get(username).clock); // expect clock to be still 0
    }
}
