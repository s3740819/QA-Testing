package main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class AllSensorsUnitTest {

    @Test
    void stop() {

    }

    @ParameterizedTest
    @ValueSource(strings = {"David", "Jack"})
    @DisplayName("Test valid input")
    void testInput(String username){
        AllSensors allSensors= new AllSensors(username);
        assertEquals(username,allSensors.username);
        assertNotEquals(null,allSensors.signal);
        assertNotEquals(null,allSensors.communicator);
        assertNotEquals(null,allSensors.aqiSensor );
        assertNotEquals(null,allSensors.locationSensor );
        assertNotEquals(null,allSensors.temperatureSensor);
        assertNotEquals(null,allSensors.monitor);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asd", "123",""," ","~!"})
    @DisplayName("Test invalid input")
    void testInput1(String username){
        assertThrows(FileNotFoundException.class,(Executable)new AllSensors(username));
    }

    @Test
    @DisplayName("Test Stop Function")
    void testStopFunction(){
        AllSensors allSensors = new AllSensors("David");
        allSensors.stop();
        assertFalse(allSensors.signal);
        assertTrue(allSensors.communicator.isShutdown());
    }
    @ParameterizedTest
    @ValueSource(strings = {"David,A,200,10", "Jack,B,100,12"})
    @DisplayName("Test getSensorData Function")
    void testGetSensorData(String data) {
        String[] split = data.split(",");
        AllSensors allSensors = new AllSensors(split[0]);
        assertEquals(split[1], allSensors.getSensorData().location);
        assertEquals(split[0], allSensors.getSensorData().username);
        assertEquals(Integer.parseInt(split[2]), allSensors.getSensorData().aqi);
        assertEquals(Integer.parseInt(split[3]), allSensors.getSensorData().temperature);
    }
}