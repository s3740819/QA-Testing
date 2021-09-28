package support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreferenceUnitTest {
    @ParameterizedTest
    @ValueSource(strings = {"A,1,hello,pool,cinema","B,2,",",3,hello"})
    @DisplayName("Test constructor")
    void test(String data){
        String name = data.split(",")[0];
        int medicalCondition = Integer.parseInt(data.split(",")[1]);
        ArrayList<String> suggestions = new ArrayList<>(Arrays.asList(data.split(",")).subList(2, data.split(",").length));
        Preference preference = new Preference(name, medicalCondition, suggestions);
        assertEquals(preference.getName(), name);
        assertEquals(preference.getSuggestions(), suggestions);
        assertEquals(preference.getMedicalCondition(), medicalCondition);
    }

    @ParameterizedTest
    @ValueSource(strings = {"A,1,hello,pool,cinema","B,2,",",3,hello"})
    @DisplayName("Test toString function")
    void test1(String data){
        String name = data.split(",")[0];
        int medicalCondition = Integer.parseInt(data.split(",")[1]);
        ArrayList<String> suggestions = new ArrayList<>(Arrays.asList(data.split(",")).subList(2, data.split(",").length));
        Preference preference = new Preference(name, medicalCondition, suggestions);
        String expected = "Preference [name=" + name + ", medical condition=" + medicalCondition + ", suggestions=" + suggestions + "]";
        assertEquals(expected, preference.toString());
        System.out.println(expected);
    }
}