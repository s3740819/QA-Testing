package main;

import com.zeroc.Ice.Current;
import helper.PreferenceRequest;
import helper.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.Preference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreferenceRepositoryUnitTest {

    @ParameterizedTest
    @ValueSource(strings = {"Jack,2,20,30", "David,3,25", "name,0,", ",0,", "~!,0,"})
    @DisplayName("Test get user info")
    void testUserGetter(String data){
        String username = data.split(",")[0];
        String expectedMedicalCondition = data.split(",")[1];
        ArrayList<String> expectedTempThreshold = new ArrayList<>();
        for (int i = 2; i< data.split(",").length; i++){
            expectedTempThreshold.add(data.split(",")[i]);
        }
        PreferenceRepository.preferences = PreferenceRepository.readPreference();
        PreferenceRepository.PreferenceWorkerI preferenceWorkerI = new PreferenceRepository.PreferenceWorkerI();
        User user = preferenceWorkerI.getUserInfo(username, new Current());
        assertEquals(expectedMedicalCondition, String.valueOf(user.medicalConditionType));
        assertEquals(Arrays.toString(expectedTempThreshold.toArray()), Arrays.toString(user.tempThreshholds));
    }


    @ParameterizedTest
    @ValueSource(strings = {"Jack,1, ,cinema", "Jack,2, ,cinema", "Jack,0, ,null", "Jack,6, ,null", "Jack,3,APO,cinema", "Jack,0,APO,bowling", "Jack,0,20,shops", "Jack,0,19,null",
                            "Jack,0,30,pool","David,1, ,shops", "David,0, ,null", "David,6, ,null", "David,0,APO,cinema", "David,1,APO,shops", "David,0,25,pool", "David,0,24,null"})
    @DisplayName("Test get Preference")
    void testPrefGetter(String input){
        String username = input.split(",")[0];
        String weather = input.split(",")[1];
        String value = input.split(",")[2];
        if (value.equals(" ")){
            value ="";
        }
        String expectedSuggestion = input.split(",")[3];
        PreferenceRepository.preferences = PreferenceRepository.readPreference();
        PreferenceRequest request = new PreferenceRequest(username, Integer.parseInt(weather), value);
        PreferenceRepository.PreferenceWorkerI preferenceWorkerI = new PreferenceRepository.PreferenceWorkerI();
        String actualSuggestion = preferenceWorkerI.getPreference(request, new Current());
        if (actualSuggestion == null) actualSuggestion = "null";
        assertEquals(expectedSuggestion,actualSuggestion);
    }

    @Test
    @DisplayName("Test Preference reader")
    void testPrefReader(){
        List<Preference> list = PreferenceRepository.readPreference();
        String expected = "[Preference [name=Jack, medical condition=2, suggestions=[when 20 suggest shops, when 30 suggest pool, when APO suggest bowling, when weather suggest cinema]]" +
                             ", Preference [name=David, medical condition=3, suggestions=[when 25 suggest pool, when APO suggest cinema, when weather suggest shops]]]";
        assertEquals(expected, list.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,12,null", "Jack,-12,null", "Jack,20,shops", "Jack,25,shops", "Jack,30,pool",
                            "David,-2,null", "David,10,null","David,25,pool", "David,100,pool"})
    @DisplayName("Test the suggestion handler for temperature")
    void testTempSuggestion(String data){
        String username = data.split(",")[0];
        int tempVal = Integer.parseInt(data.split(",")[1]);
        PreferenceRepository.preferences = PreferenceRepository.readPreference();
        String expectedSuggestion = data.split(",")[2];
        String actualSuggestion = PreferenceRepository.getSuggestionTemp(username, tempVal);
        if (actualSuggestion == null) actualSuggestion = "null";
        assertEquals(expectedSuggestion, actualSuggestion);
    }


    @ParameterizedTest
    @ValueSource(strings = {"Jack,1,cinema", "Jack,2,cinema", "Jack,0,null", "Jack,6,null", "Jack,3,cinema",
            "David,0,null", "David,1,shops","David,2,shops", "David,3,shops", "David,4,null"})
    @DisplayName("Test the suggestion handler for weather")
    void testWeatherSuggestion(String data){
        String username = data.split(",")[0];
        int weatherVal = Integer.parseInt(data.split(",")[1]);
        PreferenceRepository.preferences = PreferenceRepository.readPreference();
        String expectedSuggestion = data.split(",")[2];
        String actualSuggestion = PreferenceRepository.getSuggestionWeather(username, weatherVal);
        if (actualSuggestion == null) actualSuggestion = "null";
        assertEquals(expectedSuggestion, actualSuggestion);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,bowling", "David,cinema", "Bao,null"})
    @DisplayName("Test the suggestion handler for APO")
    void testAPOSuggestion(String data) {
        String username = data.split(",")[0];
        PreferenceRepository.preferences = PreferenceRepository.readPreference();
        String expectedSuggestion = data.split(",")[1];
        String actualSuggestion = PreferenceRepository.getSuggestionAPO(username);
        if (actualSuggestion == null) actualSuggestion = "null";
        assertEquals(expectedSuggestion, actualSuggestion);
    }
}