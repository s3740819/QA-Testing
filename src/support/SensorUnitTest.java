package support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SensorUnitTest {
    @ParameterizedTest
    @ValueSource(strings = {"A,abc","Jack,asd","Jack,AQI", "David,Temperature", "David,Location"})
    @DisplayName("Test constructor")
    void test(String data){
        String name = data.split(",")[0];
        String type = data.split(",")[1];
        Sensor sensor = new Sensor(name, type);

        assertEquals(sensor.getType(), type);
        assertEquals(sensor.getUsername(), name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,AQI,100","Jack,Temperature,12","Jack,Location,B", "David,AQI,200","David,Temperature,10", "David,Location,A"})
    @DisplayName("Test get current value")
    void test1(String data){
        String name = data.split(",")[0];
        String type = data.split(",")[1];
        Sensor sensor = new Sensor(name, type);
        assertEquals(data.split(",")[2], sensor.getCurrentValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,AQI,/{100=15, 90=11}","Jack,Temperature,/{12=5, 15=3, 20=4}","Jack,Location,/{B=1, C=15, D=14}"
                            , "David,AQI,/{200=15, 90=11}","David,Temperature,/{10=5, 23=3, 20=4}", "David,Location,/{A=1, C=15, D=14}"})
    @DisplayName("Test read data function")
    void test2(String data){
        String name = data.split(",")[0];
        String type = data.split(",")[1];
        Sensor sensor = new Sensor(name, type);
        assertEquals(data.split(",/")[1],sensor.readData().toString());
        System.out.println(sensor.readData().toString());
    }

}