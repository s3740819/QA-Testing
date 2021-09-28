package main;

import com.zeroc.Ice.Current;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationServerUnitTest {

    @Test
    @DisplayName("Test read configuration function")
    void test(){
        LinkedHashMap<String, String> map = LocationServer.readConfig();
        String expected = "[A=Indoor, B=Indoor, C=Outdoor, D=Outdoor]";
        assertEquals(expected, map.entrySet().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A,Indoor", "B,Indoor", "C,Outdoor","D,Outdoor","E,null", ",null"})
    @DisplayName("Test location mapping function")
    void testLocationMapping(String data){
        LocationServer.table = LocationServer.readConfig();
        LocationServer.LocationWorkerI locationWorkerI = new LocationServer.LocationWorkerI();
        String result = locationWorkerI.locationMapping(data.split(",")[0], new Current());
        if (result == null) result = "null";
        assertEquals(data.split(",")[1],result);
    }

}