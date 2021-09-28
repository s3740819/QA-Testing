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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTestAllSensorsAndLocationServerAndContextManager {
    /* To perform this test case:
     * The IceBox server must be turned on.
     * PreferenceRepository
     * LocationServer
     *
     * */
    private final CountDownLatch waiter = new CountDownLatch(1);
    private static String username;

    /**** Create Test Monitor Proxy to send custom sensor data to ContextManger - acting up as an AllSensors****/
    /**** AllSensors send data directly to ContextManager without request ****/



    private static WeatherAlarms weatherAlarms = new WeatherAlarms();
    /*** Code for test section ***/
    @BeforeAll
    public static void setUpBeforeClass() {
        username = "Jack";
        Handler.initialize(weatherAlarms);

        ContextManager.currentWeather = 0; // init to normal weather
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username, new Current());
        // not running weather alarm - we determine the weather condition in the test by ourselves
//        ContextManager.iniWeatherAlarmWorker();
//        ContextManager.runWeatherAlarm();

        // Set up monitor that acts like an AllSensors

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

    @Test
    @DisplayName("Check LocationServer evaluation when receiving indoor location")
    void testLocationServerEvaluationReceivingIndoorLocation(){
        String currentLocation = "A"; // indoor

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, 25); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = data;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals("Indoor", ContextManager.locationWorker.locationMapping(currentLocation));
    }

    @Test
    @DisplayName("Check LocationServer evaluation when receiving outdoor location")
    void testLocationServerEvaluationReceivingOutdoorLocation(){
        String currentLocation = "C"; // indoor

        // create test SensorData
        SensorData data = new SensorData(username, currentLocation, 10, 25); // current location is OUTDOOR
        ContextManager.users.get(username).sensorData = data;

        ContextManager.MonitorI monitorI = new ContextManager.MonitorI();
        monitorI.report(data, new Current()); // send data with the changed AQI

        // wait a bit for the report() function to finish
        try {waiter.await(1, TimeUnit.SECONDS);} catch (Exception e){System.out.println(e.getMessage()); }

        assertEquals("Outdoor", ContextManager.locationWorker.locationMapping(currentLocation));
    }

}
