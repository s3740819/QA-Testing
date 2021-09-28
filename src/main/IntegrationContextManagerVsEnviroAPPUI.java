package main;

import helper.SensorData;
import org.junit.jupiter.api.*;
import support.HandleUserInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

public class IntegrationContextManagerVsEnviroAPPUI {
    /* You need to run the following models to test this intergrationPreference
     * LocationServer.java
     * WeatherAlarms.java
     * PreferenceRepository.java
     *
     *
     * IN THIS ONE: You need to run EACH test case manually
     */
    static String username;


    //EnviroAPPUI
    private static EnviroAPPUI enviroAPPUI;

    //Testing AllSensors configuration
    private static AllSensors allSensors;

    //Configuration for testing through I/O stream
    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;
    private static final int OFFSET = 100; //processing time for program to change value
    private final CountDownLatch waiter = new CountDownLatch(1);
    private static WeatherAlarms weatherAlarms = new WeatherAlarms();
    //Environment configuration before integrating CM to Pref Repo
    @BeforeAll
    static void setupBeforeClass(){
        //Context Manager setup
        Handler.initialize(weatherAlarms);
        //EnviroAPPUI setup
        EnviroAPPUI.communicator = com.zeroc.Ice.Util.initialize(new String[0]);
        EnviroAPPUI.iniContextManagerWorker(new String[0]);
        EnviroAPPUI.handleUserInput = new HandleUserInput();
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
        EnviroAPPUI.communicator.shutdown();
        Handler.reset();
        setupBeforeClass();
        System.setOut(systemOut);
    }

    @Test
    @DisplayName("Check ContextManager user name update when receiving username from EnviroAPPUI")
    public void   testUsernameCMReceivedFromUI(){
        username = "Jack";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);
        assertEquals(EnviroAPPUI.username,ContextManager.testCurrentUsername);
    }

    @Test
    @DisplayName("Check ContextManager user login status when receiving invalid username from EnviroAPPUI")
    public void testInvalidUsernameCMReceivedFromUI(){
        username = "Harry Potter";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);
        assertEquals("Invalid user",testOut.toString());
    }

    @Test
    @DisplayName("Check ContextManager user login process when receiving valid username from EnviroAPPUI")
    public void testUserLogInProcessWhenCMReceiveValidUsernamFromUI(){
        username = "Jack";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);
        assertTrue(testOut.toString().contains("Jack added!"));
    }

    @Test
    @DisplayName("Check menu functionality for option 1 when the entered item of interest is valid.")
    public void testInformationOfValidItemOfInterest(){
        //Add user
        username = "Jack";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);

        //Select option 1
        input = "1\nVivo City Shopping Centre";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        EnviroAPPUI.handleUserInput.run();
        String expected = "Vivo City Shopping Centre is a major regional shopping centre in the southern suburb of Ho Chi Minh City, Vietnam. It is the second largest shopping centre in the southern suburbs of Ho Chi Minh City, by gross area, and contains the only H&M store in that region.".replaceAll("\n", "").replaceAll("\r", "");
        assertEquals(expected,testOut.toString().split("\\n")[2].replaceAll("\n", "").replaceAll("\r", ""));
    }

    @Test
    @DisplayName("Check menu functionality for option 1 when the entered item of interest is invalid.")
    public void testInformationOfInvalidItemOfInterest(){
        //Add user
        username = "Jack";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);

        //Select option 1
        input = "1\nKachiusa";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        EnviroAPPUI.handleUserInput.run();
        String expected = ("Not match found for item of interest").replaceAll("\n", "").replaceAll("\r", "");
        assertEquals(expected,testOut.toString().split("\\n")[2].replaceAll("\n", "").replaceAll("\r", ""));
    }

    public String arrayToString(String[] array){
        String string = "";
        for(int i=0;i<array.length;i++){
            string+=array[i];
            if(array.length>1) string+=", ";
        }
        return string;
    }

    @Test
    @DisplayName("Check menu functionality for option 2 when there are items of interest in current location")
    public void testItemOfInterestsInCurrentLocation() throws InterruptedException {
        //Add user
        username = "Jack";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);

        //AllSensors setup
        allSensors = new AllSensors(ContextManager.testCurrentUsername);
        SensorData sensorData = new SensorData("Jack", "A", 0, 0);
        allSensors.monitor.report(sensorData);
        waiter.await(1, TimeUnit.SECONDS); // wait 1 second (slightly over a bit)

        //Select option 1
        input = "2";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        EnviroAPPUI.handleUserInput.run();
        assertEquals("Vivo City Shopping Centre",arrayToString(ContextManager.searchItems));
    }

    @Test
    @DisplayName("Check menu functionality for option 2 when there are no items of interest in current location")
    public void testNoItemOfInterestsInCurrentLocation() throws InterruptedException {
        //Add user
        username = "Jack";
        String input = username;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner reader = new Scanner(System.in);
        EnviroAPPUI.username = reader.nextLine();
        EnviroAPPUI.contextManagerWorker.addUser(EnviroAPPUI.username);

        //AllSensors setup
        allSensors = new AllSensors(ContextManager.testCurrentUsername);
        SensorData sensorData = new SensorData("Jack", "E", 0, 0);
        allSensors.monitor.report(sensorData);
        waiter.await(1, TimeUnit.SECONDS); // wait 1 second (slightly over a bit)

        //Select option 1
        input = "2";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        EnviroAPPUI.handleUserInput.run();

        assertTrue(HandleUserInput.noItemOfInterestInCurrentLocation);
    }


}
