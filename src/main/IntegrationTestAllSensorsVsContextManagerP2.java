package main;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.IceStorm.TopicPrx;
import helper.ContextManagerWorker;
import helper.MonitorPrx;
import helper.SensorData;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestAllSensorsVsContextManagerP2 {
    /* To perform this test case:
    * The IceBox server must be turned on.
    * LocationServer must be turned on
    * PreferenceRepository must be turned on
    *
    * NOTE: it takes some times to run each test case depending on the APO threshold
    *
    * Some new variables (with "to do" tage) is added inside ContextManager for testing automation
    * */
    private final CountDownLatch waiter = new CountDownLatch(1);
    private static String username;
    private static WeatherAlarms weatherAlarms = new WeatherAlarms();

    /*** Code for test section ***/
    @BeforeAll
    public static void setUpBeforeClass(){
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
        ContextManager.countAPOAlert = 0;
        ContextManager.users.get(username).sensorData = null;
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 1 in Good Air Quality")
    void testContextManagerAPOEvaluationForMedicalType1InGoodAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int testAQIValue = 25;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(30, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 30){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 2 in Good Air Quality")
    void testContextManagerAPOEvaluationForMedicalType2InGoodAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int testAQIValue = 25;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(60, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 60){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 3 in Good Air Quality")
    void testContextManagerAPOEvaluationForMedicalType3InGoodAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int testAQIValue = 25;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(90, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 90){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 1 in Moderate Air Quality")
    void testContextManagerAPOEvaluationForMedicalType1InModerateAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int testAQIValue = 75;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(15, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 15){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 2 in Moderate Air Quality")
    void testContextManagerAPOEvaluationForMedicalType2InModerateAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int testAQIValue = 75;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(30, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 30){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 3 in Moderate Air Quality")
    void testContextManagerAPOEvaluationForMedicalType3InModerateAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int testAQIValue = 75;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(45, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 45){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 1 in Unhealthy for Sensitive Group Air Quality")
    void testContextManagerAPOEvaluationForMedicalType1InUnhealthyForSensitiveGroupQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int testAQIValue = 125;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(10, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 10){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 2 in Unhealthy for Sensitive Group Air Quality")
    void testContextManagerAPOEvaluationForMedicalType2InUnhealthyForSensitiveGroupQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int testAQIValue = 125;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(20, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 20){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 3 in Unhealthy for Sensitive Group Air Quality")
    void testContextManagerAPOEvaluationForMedicalType3InUnhealthyForSensitiveGroupQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int testAQIValue = 125;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(30, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 30){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 1 in Unhealthy Air Quality")
    void testContextManagerAPOEvaluationForMedicalType1InUnhealthyAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int testAQIValue = 175;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(5, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 5){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 2 in Unhealthy Air Quality")
    void testContextManagerAPOEvaluationForMedicalType2InUnhealthyAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int testAQIValue = 175;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(10, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 10){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }

    @Test
    @DisplayName("TC: ContextManager APO evaluation for medical condition type 3 in Unhealthy Air Quality")
    void testContextManagerAPOEvaluationForMedicalType3InUnhealthyAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int testAQIValue = 175;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, testAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 0;
        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with AQI 25

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertFalse(ContextManager.isAPOReached); // APO is not yet reached
        assertEquals(0, ContextManager.countAPOAlert); // no alertion
        assertEquals(15, ContextManager.users.get(username).apoThreshhold); // calculated apoThreshold should be 30

        int i = 0;
        while (i < 15){ // stop after 30 seconds
            // send data with AQI 25
            monitorI.report(data, new Current());
            // wait for 1 second
            try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }
            // increment i
            i++;
        }

        assertTrue(ContextManager.isAPOReached); // now APO is reached
        assertNotEquals(0, ContextManager.countAPOAlert); // start to alert APO
    }
}
