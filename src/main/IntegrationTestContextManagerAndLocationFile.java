package main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.LocationDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegrationTestContextManagerAndLocationFile {
    /* To perform this test case:
    * The IceBox server must be turned on.
    *
    * */
    private static String pathname;

    // Reimplementation of readCityInfo() function of ContextManager as ContextManger.readCityInfo() does not accept file path as argument to read config
    static List<LocationDetails> readCityInfo(String pathname) {
        List<LocationDetails> result = new ArrayList<>();
        File file = new File(pathname);
        int count = 0;
        try {
            Scanner sc = new Scanner(file);
            List<String> strings = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.equals("***")) {
                    strings.add(line);
                } else {
                    LocationDetails locationDetails = new LocationDetails(strings);
                    result.add(locationDetails);
                    strings = new ArrayList<>();
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Test
    @DisplayName("TC: ContextManager when its location file contains rubbish data")
    void testContextManagerReadingRubbishLocationFile(){
        pathname = "test_resources/RubbishCityInfo";

        assertThrows(Exception.class, () -> ContextManager.cityInfo = readCityInfo(pathname)); // expect to throw exception as location is invalid
    }

    @Test
    @DisplayName("TC: ContextManager when its location file lack entries")
    void testContextManagerReadingLackEntriesLocationFile(){
        pathname = "test_resources/LackEntriesCityInfo";

        assertThrows(Exception.class, () -> ContextManager.cityInfo = readCityInfo(pathname)); // expect to throw exception as location is invalid
    }

    @Test
    @DisplayName("TC: ContextManager when reading correctly formatted location file")
    void testContextManagerReadingValidLocationFile(){
        pathname = "test_resources/ValidCityInfo";

        ContextManager.cityInfo = readCityInfo(pathname);

        List<LocationDetails> expectedCityInfo = new ArrayList<>();

        List<String> expectedStrings = new ArrayList<>();
        expectedStrings.add("name: Indooroopilly Shopping Centre");
        expectedStrings.add("location: A");
        expectedStrings.add("information: It is the largest shopping centre in Brisbane");
        expectedStrings.add("services: cinema, restaurants, pool, shops");

        expectedCityInfo.add(new LocationDetails(expectedStrings));

        assertEquals(expectedCityInfo.toString(), ContextManager.cityInfo.toString());
    }

}
