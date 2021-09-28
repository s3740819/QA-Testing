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

public class IntegrationTestAllSensorsVsContextManagerP3 {
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
        ContextManager.countTemperatureAlert = 0;
        ContextManager.countAPOAlert = 0;
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 1 in Good Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType1InGoodAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int previousAQIValue = 25;
        int currentAQIValue = 30;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 2 in Good Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType2InGoodAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int previousAQIValue = 25;
        int currentAQIValue = 30;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 3 in Good Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType3InGoodAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int previousAQIValue = 25;
        int currentAQIValue = 30;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 1 in Moderate Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType1InModerateAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int previousAQIValue = 75;
        int currentAQIValue = 80;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 2 in Moderate Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType2InModerateAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int previousAQIValue = 75;
        int currentAQIValue = 80;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current());// send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 3 in Moderate Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType3InModerateAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int previousAQIValue = 75;
        int currentAQIValue = 80;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 1 in UnHealthy For Sensitive Group Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType1InUnhealthyForSensitiveGroupAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int previousAQIValue = 125;
        int currentAQIValue = 130;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 2 in UnHealthy For Sensitive Group Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType2InUnhealthyForSensitiveGroupAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int previousAQIValue = 125;
        int currentAQIValue = 130;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 3 in UnHealthy For Sensitive Group Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType3InUnhealthyForSensitiveGroupAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 3;
        int previousAQIValue = 125;
        int currentAQIValue = 130;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 1 in UnHealthy Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType1InUnhealthyAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int previousAQIValue = 175;
        int currentAQIValue = 180;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 2 in UnHealthy Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType2InUnhealthyAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 2;
        int previousAQIValue = 175;
        int currentAQIValue = 180;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }

    @Test
    @DisplayName("TC: ContextManager timer reset with AQI change for medical condition type 3 in UnHealthy Air Quality")
    void testContextManagerTimerResetWithAQIChangeForMedicalType3InUnhealthyAirQuality(){
        String currentLocation = "C"; // outdoor
        int testMedicalConditionType = 1;
        int previousAQIValue = 175;
        int currentAQIValue = 180;

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, currentAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = new SensorData(username,currentLocation,12, previousAQIValue); // current location is OUTDOOR
        ContextManager.users.get(username).clock = 37; // assuming the clock is counting with some value (example. 37)

        ContextManager.users.get(username).medicalConditionType = testMedicalConditionType;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals(0,ContextManager.users.get(username).clock-1); // -1 for additional clock tick
    }
}
