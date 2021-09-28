package main;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.IceStorm.TopicPrx;
import helper.ContextManagerWorker;
import helper.Monitor;
import helper.MonitorPrx;
import helper.SensorData;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestContextManagerVsLocationServer {
    /* To perform this test case:
    * The IceBox server must be turned on.
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
    public static void restart(){
        weatherAlarms.communicator.shutdown();
    }

    @AfterEach
    public void reset(){
        Handler.reset();
        setUpBeforeClass();
    }
    @BeforeEach
    public void setUpBeforeEach(){
        ContextManager.isTemperatureReached = false;
        ContextManager.isAPOReached = false;
        ContextManager.isWeatherReached = false;
    }

    @Test
    @DisplayName("TC: Check ContextManager sending request when weather warning is triggered")
    void testContextManagerSendRequestWhenWeatherWarning(){
        // create test SensorData
        SensorData data = new SensorData(username, "C", 10, 25); // temperature not reached
        ContextManager.users.get(username).sensorData = data;
        ContextManager.users.get(username).apoThreshhold = 60;
        ContextManager.users.get(username).clock = 0; // reset clock so that APO is not reached
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager for checking warning - supposed NO temperature and APO reached

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        // create test weather data
        int currentWeatherTestData = 2; //extreme weather
        ContextManager.checkWeather(currentWeatherTestData);

        // Temperature threshold should not be reached
        assertFalse(ContextManager.isTemperatureReached);
        // APO should not be reached
        assertFalse(ContextManager.isAPOReached);
        // Extreme weather should be detected
        assertTrue(ContextManager.isWeatherReached);
        // ContextManager should send locations list to LocationServer when extreme weather occurs
        assertTrue(ContextManager.isSendingLocationList);
    }

    @Test
    @DisplayName("TC: Check ContextManager sending request when APO is reached")
    void testContextManagerSendRequestWhenAPO(){
        // create test SensorData
        SensorData data = new SensorData(username, "C", 10, 25); // current location is OUTSIDE, temperature not reached
        ContextManager.users.get(username).sensorData = data;
        ContextManager.users.get(username).apoThreshhold = 60;
        ContextManager.users.get(username).clock = 59; // set clock to trigger APO (= 2*30-1 for 1 more tick of clock)
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager for checking warning - supposed NO temperature and APO reached

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        // create test weather data
        int currentWeatherTestData = 0; //normal weather
        ContextManager.checkWeather(currentWeatherTestData);

        // Temperature threshold should not be reached
        assertFalse(ContextManager.isTemperatureReached);
        // Extreme weather should not occur
        assertFalse(ContextManager.isWeatherReached);
        // APO should be reached
        assertTrue(ContextManager.isAPOReached);
        // ContextManager should send locations list to LocationServer when APO is reached
        assertTrue(ContextManager.isSendingLocationList);
    }

    @Test
    @DisplayName("TC: Check ContextManager sending request when Temperature threshold is reached")
    void testContextManagerSendRequestWhenTempReached(){
        // create test SensorData
        SensorData data = new SensorData(username, "C", 20, 25); // current location is OUTSIDE, temperature is reached with temperature 20
        ContextManager.users.get(username).sensorData = data;
        ContextManager.users.get(username).apoThreshhold = 60;
        ContextManager.users.get(username).clock = 0; // reset clock so APO does not occur
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager for checking warning - supposed temperature reached and APO NOT reached

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        // create test weather data
        int currentWeatherTestData = 0; //normal weather
        ContextManager.checkWeather(currentWeatherTestData);

        // Extreme weather should not occur
        assertFalse(ContextManager.isWeatherReached);
        // APO should be reached
        assertFalse(ContextManager.isAPOReached);
        // Temperature threshold should not be reached
        assertTrue(ContextManager.isTemperatureReached);
        // ContextManager should NOT send locations list to LocationServer and temperature is reached
        assertFalse(ContextManager.isSendingLocationList);
    }

    @Test
    @DisplayName("TC: Check ContextManager sending correct suggestions when Weather warning occurs")
    void testContextManagerSendCorrectSuggestionsWhenExtremeWeather(){
        // create test SensorData
        SensorData data = new SensorData(username, "C", 10, 25); // temperature not reached
        ContextManager.users.get(username).sensorData = data;
        ContextManager.users.get(username).apoThreshhold = 60;
        ContextManager.users.get(username).clock = 0; // reset clock so that APO is not reached

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager for checking warning - supposed NO temperature and APO reached

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        // create test weather data
        int currentWeatherTestData = 2; //extreme weather
        ContextManager.checkWeather(currentWeatherTestData);


        // INDOOR locations are only Vivo City Shopping Centre and Crescent Mall and has "cinema"
        List<String> expected = new ArrayList<>();
        expected.add("Vivo City Shopping Centre");
        expected.add("Crescent Mall");

        assertEquals(expected, ContextManager.suggestedLocations);
    }

    @Test
    @DisplayName("TC: Check ContextManager sending correct suggestions when APO occurs")
    void testContextManagerSendCorrectSuggestionsWhenAPO(){
        // create test SensorData
        SensorData data = new SensorData(username, "C", 10, 25); // temperature not reached
        ContextManager.users.get(username).sensorData = data;
        ContextManager.users.get(username).apoThreshhold = 60;
        ContextManager.users.get(username).clock = 59; // set clock to trigger APO (= 2*30-1 for 1 more tick of clock)
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager for checking warning - supposed NO temperature and APO reached

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        // create test weather data
        int currentWeatherTestData = 0; //no extreme weather
        ContextManager.checkWeather(currentWeatherTestData);

        // INDOOR locations are only Vivo City Shopping Centre and has "bowling"
        List<String> expected = new ArrayList<>();
        expected.add("Vivo City Shopping Centre");

        assertEquals(expected, ContextManager.suggestedLocations);
    }

    @Test
    @DisplayName("TC: Check ContextManager sending correct suggestions when temperature threshold is reached")
    void testContextManagerSendCorrectSuggestionsWhenTempReached(){
        // create test SensorData
        SensorData data = new SensorData(username, "C", 20, 25); // current location is OUTSIDE, temperature is reached with temperature 20
        ContextManager.users.get(username).sensorData = data;
        ContextManager.users.get(username).apoThreshhold = 60;
        ContextManager.users.get(username).clock = 0; // reset clock so APO does not occur
        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data to ContextManager for checking warning - supposed temperature reached and APO NOT reached

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        // create test weather data
        int currentWeatherTestData = 0; //normal weather
        ContextManager.checkWeather(currentWeatherTestData);

        /* Both indoor and outdoor locations that has "shops should be suggested
        * which are Vivo City Shopping Centre, Crescent Mall, Dam Sen Parklands, Ho Chi Minh City, Downtown
        * */
        List<String> expected = new ArrayList<>();
        expected.add("Vivo City Shopping Centre");
        expected.add("Crescent Mall");
        expected.add("Dam Sen Parklands");
        expected.add("Ho Chi Minh City, Downtown");

        assertEquals(expected, ContextManager.suggestedLocations);
    }

}
