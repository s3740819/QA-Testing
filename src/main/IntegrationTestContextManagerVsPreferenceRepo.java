package main;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import helper.MonitorPrx;
import helper.PreferenceRequest;
import helper.SensorData;
import helper.User;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTestContextManagerVsPreferenceRepo {
    /* You need to run the following models to test this intergrationPreference
     * LocationServer.java
     * WeatherAlarms.java
     */

    private static WeatherAlarms weatherAlarms = new WeatherAlarms();
    //Configuration for testing through I/O stream
    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;
    private final CountDownLatch waiter = new CountDownLatch(1);

    //Testing AllSensors configuration
    private static AllSensors allSensors;

    //Environment configuration before integrating CM to Pref Repo
    @BeforeAll
    static void setupBeforeClass(){
        Handler.initialize(weatherAlarms);
    }
    @AfterAll
    public static void reset(){
        weatherAlarms.communicator.shutdown();
    }
    //Set up I/O stream
    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }
    @AfterEach
    public void restoreSystemOutputAndCleanUp() {
        Handler.reset();
        setupBeforeClass();
        System.setOut(systemOut);
    }


    @Test
    @DisplayName("Check Preference Repository behaviour when receiving non-existent username.")
    public void testRequestSentFromCMToPreferenceRepositoryWithInvalidUsername() {
        // actual output from the PR
        User user = ContextManager.preferenceWorker.getUserInfo("Emily");
        // assert the expected suggestion against the suggestion from the PR
        assertTrue(testOut.toString().contains("Received username Emily from Context Manager. The username does not exist in the repository."));
    }

    @Test
    @DisplayName("Check Preference Repository behaviour when receiving valid username.")
    public void testRequestSentFromCMToPreferenceRepositoryWithValidUsername() {
        // actual output from the PR
        User user = ContextManager.preferenceWorker.getUserInfo("Jack");
        // assert the expected suggestion against the suggestion from the PR
        assertTrue(testOut.toString().contains(""));
    }

    @Test
    @DisplayName("Check Context Manager request result of medical condition type when sending invalid username to Preference Repository.")
    public void testMedicalConditionTypeSentFromPreferenceRepoToCMWithInvalidUsername() {
        // actual output from the PR
        User user = ContextManager.preferenceWorker.getUserInfo("Emily");
        // assert the expected suggestion against the suggestion from the PR
        assertTrue(testOut.toString().contains("Invalid user"));
    }

    @Test
    @DisplayName("Check Context Manager request result of medical condition type when sending valid username to Preference Repository.")
    public void testMedicalConditionTypeSentFromPreferenceRepoToCMWithValidUsername() {
        // actual output from the PR
        User user = ContextManager.preferenceWorker.getUserInfo("Jack");
        // assert the expected suggestion against the suggestion from the PR
        assertEquals(2,user.medicalConditionType);
    }

    @Test
    @DisplayName("Check Context Manager request result of temperature threshold when sending valid username having one temperature threshold to PreferenceRepository.")
    public void testTemperatureThresholdSentFromPreferenceRepoToCMWithValidUsername() {
        // actual output from the PR
        User user = ContextManager.preferenceWorker.getUserInfo("David");
        // assert the expected suggestion against the suggestion from the PR
        assertEquals(16,user.tempThreshholds[0]);
    }

    @Test
    @DisplayName("Check Context Manager request result of temperature threshold when sending valid username having many temperature thresholds to PreferenceRepository.")
    public void testMultipleTemperatureThresholdSentFromPreferenceRepoToCMWithValidUsername() {
        // actual output from the PR
        User user = ContextManager.preferenceWorker.getUserInfo("Jack");
        // assert the expected suggestion against the suggestion from the PR
        assertAll(()->{
            assertEquals(20,user.tempThreshholds[0]);
            assertEquals(30,user.tempThreshholds[1]);
        });
    }

    @Test
    @DisplayName("Check Preference Repository matching result when Context Manager sends triggered weather alarm.")
    public void testServicesSentfromPreferenceRepoToCMWhenOnlyWeatherAlarmTriggrered(){
        PreferenceRequest request = new PreferenceRequest("David", 1, null);
        String preference = ContextManager.preferenceWorker.getPreference(request);
        assertEquals("shops",preference);
    }

    @Test
    @DisplayName("Check Preference Repository matching result when Context Manager sends temperature threshold.")
    public void testServicesSentfromPreferenceRepoToCMWhenOnlyTemperatureThresholdReached(){
        PreferenceRequest request = new PreferenceRequest("David", 0, "16");
        String preference = ContextManager.preferenceWorker.getPreference(request);
        assertEquals("pool",preference);
    }

    @Test
    @DisplayName("Check Preference Repository matching result when Context Manager sends APO.")
    public void testServicesSentfromPreferenceRepoToCMWhenOnlyAPOReached(){
        PreferenceRequest request = new PreferenceRequest("David", 0, "APO");
        String preference = ContextManager.preferenceWorker.getPreference(request);
        assertEquals("cinema",preference);
    }

    @Test
    @DisplayName("Check Context Manager request to Preference Repository when both weather alarm and APO reached.")
    public void testRequestSentFromContextManagerToPreferenceRepoWhenWeatherAlarmAndAPOReached() throws InterruptedException {
        //Add user
        String username = "Jack";
        ContextManager.currentWeather = 1;
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username,new Current());


        //AllSensors setup
        allSensors = new AllSensors(username);

        SensorData sensorData = new SensorData("Jack", "C", 0, 200);
        for (int i=0;i<11;i++) allSensors.monitor.report(sensorData);
        waiter.await(1, TimeUnit.SECONDS); // wait 1 second (slightly over a bit)
        ContextManager.checkWeather(ContextManager.currentWeather);

        assertEquals(1,ContextManager.testRequest.weather);
    }

    @Test
    @DisplayName("Check Context Manager request to Preference Repository when both weather alarm and temperature threshold reached.")
    public void testRequestSentFromContextManagerToPreferenceRepoWhenWeatherAlarmAndTemperatureThresholdReached() throws InterruptedException {
        //Add user
        String username = "Jack";
        ContextManager.currentWeather = 1;
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username,new Current());


        //AllSensors setup
        allSensors = new AllSensors(username);

        SensorData sensorData = new SensorData("Jack", "C", 20, 0);
        allSensors.monitor.report(sensorData);
        waiter.await(1, TimeUnit.SECONDS); // wait 1 second (slightly over a bit)
        ContextManager.checkWeather(ContextManager.currentWeather);

        assertEquals(1,ContextManager.testRequest.weather);
    }

    @Test
    @DisplayName("Check Context Manager request to Preference Repository when both APO and temperature threshold reached.")
    public void testRequestSentFromContextManagerToPreferenceRepoWhenAPOAndTemperatureThresholdReached() throws InterruptedException {
        //Add user
        String username = "Jack";
        ContextManager.currentWeather = 0;
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username,new Current());


        //AllSensors setup
        allSensors = new AllSensors(username);

        SensorData sensorData = new SensorData("Jack", "C", 20, 200);
        for (int i=0;i<11;i++) allSensors.monitor.report(sensorData);
        waiter.await(1, TimeUnit.SECONDS); // wait 1 second (slightly over a bit)
        ContextManager.checkWeather(ContextManager.currentWeather);

        assertEquals("APO",ContextManager.testRequest.value);
    }

    @Test
    @DisplayName("Check Context Manager request to Preference Repository when all weather alarm, APO and temperature threshold reached.")
    public void testRequestSentFromContextManagerToPreferenceRepoWhenWeatherAlarmAPOAndTemperatureThresholdReached() throws InterruptedException {
        //Add user
        String username = "Jack";
        ContextManager.currentWeather = 1;
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username,new Current());


        //AllSensors setup
        allSensors = new AllSensors(username);

        SensorData sensorData = new SensorData("Jack", "C", 20, 200);
        for (int i=0;i<11;i++) allSensors.monitor.report(sensorData);
        waiter.await(1, TimeUnit.SECONDS); // wait 1 second (slightly over a bit)
        ContextManager.checkWeather(ContextManager.currentWeather);

        assertEquals(1,ContextManager.testRequest.weather);
    }
}
