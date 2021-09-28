package main;

import com.zeroc.Ice.Current;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

public class IntegrationTestContextManagerVsWeatherAlarm {
    /* You need to run the following models to test this intergrationPreference
     * LocationServer.java
     * PreferenceRepository.java
     */

    private static WeatherAlarms weatherAlarms = new WeatherAlarms();

    //Setting the environment for integrating the CM to the Weather Alarm
    @BeforeAll
    static void setUpBeforeClass() {
        //Weather Alarm setup
        Handler.initialize(weatherAlarms);

        //Add user
        ContextManager.currentWeather = 0;
        String username = "Jack";
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(username,new Current());
    }
    @AfterAll
    static void reset(){
        weatherAlarms.communicator.shutdown();
    }


    @Test
    @DisplayName("Check ContextManager weather status update when receiving normal weather condition, heavy rain, hail storm, strong wind from weather alarm.")
    public void testWeatherStatusUpdateByCMWhenReceiveNormalWeather() throws InterruptedException {
        assertAll(()->{
            //Heavy Rain
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(1, (long) ContextManager.currentWeather);

            //Hail Storm
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(2, (long) ContextManager.currentWeather);

            //Strong wind
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(3, (long) ContextManager.currentWeather);

            //Normal Condition
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(0, (long) ContextManager.currentWeather);
        });
    }

    @Test
    @DisplayName("Check ContextManager behaviour when receiving normal weather condition, heavy rain, hail storm, strong wind from weather alarm.")
    public void testRequestsCreatedByCMWhenReceiveNormalWeather() throws InterruptedException {
        assertAll(()->{
            //Normal Condition
            ContextManager.checkWeather(ContextManager.currentWeather);
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(0,ContextManager.testRequest.weather);

            //Heavy Rain
            ContextManager.checkWeather(ContextManager.currentWeather);
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(1,ContextManager.testRequest.weather);

            //Hail Storm
            ContextManager.checkWeather(ContextManager.currentWeather);
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(2,ContextManager.testRequest.weather);

            //Strong wind
            ContextManager.checkWeather(ContextManager.currentWeather);
            ContextManager.currentWeather = ContextManager.weatherAlarmWorker.getWeather();
            assertEquals(3,ContextManager.testRequest.weather);
        });
    }
}
