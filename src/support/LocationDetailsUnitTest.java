package support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationDetailsUnitTest {

    @ParameterizedTest
    @ValueSource(strings = {"Aeon,B,hello hello,pool,cinema","ABC,E,hello,","Aeon,,hello,cinema",",B,hello hello,pool"})
    @DisplayName("Test constructor")
    void test(String data){
        String name = data.split(",")[0];
        String location = data.split(",")[1];
        String info = data.split(",")[2];
        ArrayList<String> services = new ArrayList<>(Arrays.asList(data.split(",")).subList(3, data.split(",").length));
        LocationDetails locationDetails = new LocationDetails(name,location,info, services);
        assertEquals(locationDetails.getInfo(), info);
        assertEquals(locationDetails.getLocation(), location);
        assertEquals(locationDetails.getName(), name);
        assertEquals(Arrays.toString(locationDetails.getServices().toArray()), Arrays.toString(services.toArray()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aeon,B,hello hello,pool,cinema","ABC,E,hello,","Aeon,,hello,cinema",",B,hello hello,pool"})
    @DisplayName("Test toString function")
    void test1(String data){
        String name = data.split(",")[0];
        String location = data.split(",")[1];
        String info = data.split(",")[2];
        ArrayList<String> services = new ArrayList<>(Arrays.asList(data.split(",")).subList(3, data.split(",").length));
        String expected = "LocationDetails [name=" + name + ", location=" + location + ", info=" + info + ", services=" + services + "]";
        LocationDetails locationDetails = new LocationDetails(name,location,info, services);
        assertEquals(expected, locationDetails.toString());
        System.out.println(expected);
    }

}