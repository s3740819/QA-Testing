package main;

import com.zeroc.Ice.Current;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherAlarmsUnitTest {
    @Test
    @DisplayName("Test read weather condition")
    void Test(){
        WeatherAlarms weatherAlarms = new WeatherAlarms();
        List<Integer> list = weatherAlarms.readWeatherConditions();
        assertEquals("[0, 1, 2, 3]", list.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {0,2,3,6,-1})
    @DisplayName("Test get weather condition")
    void testWeatherGetter(int timeRead){
        WeatherAlarms.WeatherAlarmWorkerI weatherAlarmWorkerI = new WeatherAlarms().new WeatherAlarmWorkerI();
        ArrayList<Integer> actual = new ArrayList<>();
        ArrayList<Integer> expected = new ArrayList<>();
        for (int i =0, value = 0; i< timeRead;i++) {
            actual.add(weatherAlarmWorkerI.getWeather(new Current()));
            expected.add(value);
            if (value == 0){
                value = 1;
            }
            else if (value == 1){
                value = 2;
            }
            else if (value == 2){
                value = 3;
            }
            else value = 0;
        }
        assertEquals(expected, actual);
    }

}