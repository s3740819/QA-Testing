package main;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import helper.ContextManagerWorker;
import helper.WeatherAlarmWorker;
import helper.WeatherAlarmWorkerPrx;
import org.junit.jupiter.api.*;
import support.LocationDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestWeatherAlarmVsItsFile {
    /* You have to start IceBox server before doing this integration test
    * */
    private static Communicator communicator; // need a commutator to initialize a proxy
    private static WeatherAlarms.WeatherAlarmWorkerI weatherAlarmWorker; // need proxy to talk with WeatherAlarmWorker
    private static WeatherAlarms weatherAlarms; // main object of test

    @BeforeEach
    public void setUpBeforeEachTest() {
        // Set up Alarm Worker channel of the WeatherAlarms
        weatherAlarms = new WeatherAlarms();
        weatherAlarms.setupWeatherAlarmWorker(new String[0]);

        // InitWorker - that connects to the previously set up
        weatherAlarmWorker = weatherAlarms.new WeatherAlarmWorkerI();
    }

    @AfterEach
    public void stopCommunicator(){
        weatherAlarms.communicator.shutdown();
    }

    @Test
    @DisplayName("TC: WeatherAlarm reads weather event correctly")
    void testWeatherAlarmReadCorrectly(){
        int count = 0;

        // read from the test file - the test file contains following weather events: 0, 1, 3, 1, 2
        File file = new File("test_resources/weather_alarms_correct_output_test.txt");
        List<Integer> result = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                count++;
                result.add(Integer.parseInt(sc.nextLine()));
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //System.out.println("Successfully read the file with the result: " + result);

        // add the read result into weather alarms
        weatherAlarms.weatherConditions = result;
        weatherAlarms.iterator = weatherAlarms.weatherConditions.iterator();

        // if the worker get the correct orders of weather events in the test file
        assertEquals(0, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(1, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(3, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(1, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(2, weatherAlarmWorker.getWeather(new Current()));
    }

    @Test
    @DisplayName("TC: WeatherAlarm repeat when end of file is reached")
    void testWeatherAlarmReadIteration(){
        int count = 0;

        // read from the test file - the test file contains following weather events: 0, 1, 3
        File file = new File("test_resources/weather_alarms_iteration_test.txt");
        List<Integer> result = new ArrayList();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                count++;
                result.add(Integer.parseInt(sc.nextLine()));
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //System.out.println("Successfully read the file with the result: " + result);

        // add the read result into weather alarms
        weatherAlarms.weatherConditions = result;
        weatherAlarms.iterator = weatherAlarms.weatherConditions.iterator();

        // if the worker get the correct orders of weather events in the test file
        assertEquals(0, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(1, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(3, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(0, weatherAlarmWorker.getWeather(new Current()));
        assertEquals(1, weatherAlarmWorker.getWeather(new Current()));
    }
}
