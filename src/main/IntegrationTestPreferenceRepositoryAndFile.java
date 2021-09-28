package main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Preference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegrationTestPreferenceRepositoryAndFile {
    /* To perform this test case:
    * The IceBox server must be turned on.
    *
    * */
    private static String pathname;
    private static PreferenceRepository preferenceRepository;

    // Reimplementation of readPreference() function of PreferenceRepository as PreferenceRepository.readPreference() does not accept file path as argument to read config
    static List<Preference> readPreference(String pathname) {
        List<Preference> result = new ArrayList<>();
        File file = new File(pathname);
        try {
            Scanner sc = new Scanner(file);
            List<String> strings = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.equals("***")) {
                    strings.add(line);
                } else {
                    Preference preference = new Preference(strings);
                    result.add(preference);
                    strings = new ArrayList<>();
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @BeforeEach
    public void setUp() {
        preferenceRepository = new PreferenceRepository();
    }


    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to nonsense data")
    void testLocationServerReadInvalidFileDueToNonsenseData(){
        pathname = "test_resources/RubbishPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to more than one weather preference")
    void testLocationServerReadInvalidFileDueToMoreThanOneWeather(){
        pathname = "test_resources/MoreThanOneWeatherPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to more than one APO preference")
    void testLocationServerReadInvalidFileDueToMoreThanOneAPO(){
        pathname = "test_resources/MoreThanOneAPOPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to more than 3 users")
    void testLocationServerReadInvalidFileDueToMoreThanThreeUsers(){
        pathname = "test_resources/MoreThanThreeUsersPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to no APO preference")
    void testLocationServerReadInvalidFileDueToNoAPO(){
        pathname = "test_resources/NoAPOPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to no Temperature preference")
    void testLocationServerReadInvalidFileDueToNoTemperature(){
        pathname = "test_resources/NoTemperaturePreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads invalid preference file due to no Weather preference")
    void testLocationServerReadInvalidFileDueToNoWeather(){
        pathname = "test_resources/NoWeatherAlarmPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }


    @Test
    @DisplayName("TC: PreferenceRepository reads out of bound preference file")
    void testLocationServerReadEmptyPreferenceFile(){
        pathname = "test_resources/OutOfBoundPreference";

        assertThrows(Exception.class, () -> preferenceRepository.preferences = readPreference(pathname));
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads valid one user preference file")
    void testLocationServerReadValidOneUserPreferenceFile(){
        pathname = "test_resources/OneUserPreference";

        List<Preference> expectedPreferences = new ArrayList<>();

        List<String> expectedStrings = new ArrayList<>();
        expectedStrings.add("name: Bryan");
        expectedStrings.add("Medical Condition Type: 2");
        expectedStrings.add("pref: when 30 suggest pool");
        expectedStrings.add("pref: when APO suggest cinema");
        expectedStrings.add("pref: when weather suggest cinema");

        expectedPreferences.add(new Preference(expectedStrings));


        preferenceRepository.preferences = readPreference(pathname);

        assertEquals(expectedPreferences.toString(), preferenceRepository.preferences.toString());
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads valid two users preference file")
    void testLocationServerReadValidTwoUsersPreferenceFile(){
        pathname = "test_resources/TwoUsersPreference";

        List<Preference> expectedPreferences = new ArrayList<>();

        List<String> expectedStrings = new ArrayList<>();
        expectedStrings.add("name: Bryan");
        expectedStrings.add("Medical Condition Type: 2");
        expectedStrings.add("pref: when 30 suggest pool");
        expectedStrings.add("pref: when APO suggest cinema");
        expectedStrings.add("pref: when weather suggest cinema");

        expectedPreferences.add(new Preference(expectedStrings));

        expectedStrings.clear();
        expectedStrings.add("name: Jack");
        expectedStrings.add("Medical Condition Type: 3");
        expectedStrings.add("pref: when 32 suggest cinema");
        expectedStrings.add("pref: when APO suggest restaurants");
        expectedStrings.add("pref: when weather suggest cinema");

        expectedPreferences.add(new Preference(expectedStrings));

        preferenceRepository.preferences = readPreference(pathname);

        assertEquals(expectedPreferences.toString(), preferenceRepository.preferences.toString());
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads valid three users preference file")
    void testLocationServerReadValidThreeUsersPreferenceFile(){
        pathname = "test_resources/ThreeUsersPreference";

        List<Preference> expectedPreferences = new ArrayList<>();

        List<String> expectedStrings = new ArrayList<>();
        expectedStrings.add("name: Bryan");
        expectedStrings.add("Medical Condition Type: 2");
        expectedStrings.add("pref: when 30 suggest pool");
        expectedStrings.add("pref: when APO suggest cinema");
        expectedStrings.add("pref: when weather suggest cinema");

        expectedPreferences.add(new Preference(expectedStrings));

        expectedStrings.clear();
        expectedStrings.add("name: Jack");
        expectedStrings.add("Medical Condition Type: 3");
        expectedStrings.add("pref: when 32 suggest cinema");
        expectedStrings.add("pref: when APO suggest restaurants");
        expectedStrings.add("pref: when weather suggest cinema");

        expectedPreferences.add(new Preference(expectedStrings));

        expectedStrings.clear();
        expectedStrings.add("name: David");
        expectedStrings.add("Medical Condition Type: 1");
        expectedStrings.add("pref: when 40 suggest pool");
        expectedStrings.add("pref: when APO suggest shops");
        expectedStrings.add("pref: when weather suggest shops");

        expectedPreferences.add(new Preference(expectedStrings));


        preferenceRepository.preferences = readPreference(pathname);

        assertEquals(expectedPreferences.toString(), preferenceRepository.preferences.toString());
    }

    @Test
    @DisplayName("TC: PreferenceRepository reads one user with many temperature preferences preference file")
    void testLocationServerReadManyTemperaturesPreferenceFile(){
        pathname = "test_resources/ManyTemperaturesPreference";

        List<Preference> expectedPreferences = new ArrayList<>();

        List<String> expectedStrings = new ArrayList<>();
        expectedStrings.add("name: Bryan");
        expectedStrings.add("Medical Condition Type: 2");
        expectedStrings.add("pref: when 20 suggest Ferris wheel");
        expectedStrings.add("pref: when 30 suggest cinema");
        expectedStrings.add("pref: when 35 suggest pool");
        expectedStrings.add("pref: when APO suggest cinema");
        expectedStrings.add("pref: when weather suggest cinema");

        expectedPreferences.add(new Preference(expectedStrings));


        preferenceRepository.preferences = readPreference(pathname);

        assertEquals(expectedPreferences.toString(), preferenceRepository.preferences.toString());
    }



}
