package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTestLocationServerAndConfigFile {
    /* To perform this test case:
    * The IceBox server must be turned on.
    *
    * Data resources for this test case are files (located in test_resources):
    * InvalidLocationServerConfig
    * NoIndoorLocationServerConfig
    * */
    private static LocationServer locationServer;
    private static String pathname;

    // Reimplementation of readConfig() function of LocationServer as LocationServer.readConfig() does not accept file path as argument to read config
    static LinkedHashMap<String, String> readConfig(String pathname) { // changed private -> none
        File file = new File(pathname);
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String status = line.split(":")[0];
                String[] locations = line.split(":")[1].trim().split(",");
                for (String location : locations) {
                    result.put(location, status);
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
        locationServer = new LocationServer();
    }

    @Test
    @DisplayName("TC: LocationServer reads invalid configuration file")
    void testLocationServerReadInvalidFile(){
        pathname = "test_resources/InvalidLocationServerConfig";

        assertThrows(Exception.class, () -> locationServer.table = readConfig(pathname)); // expect to throw exception as location is invalid
    }

    @Test
    @DisplayName("TC: LocationServer reads configuration file with no indoor locations")
    void testLocationServerReadFileWithNoIndoor(){
        pathname = "test_resources/NoIndoorLocationServerConfig";

        assertThrows(Exception.class, () -> locationServer.table = readConfig(pathname)); // expect to throw exception as location is invalid
    }

    @Test
    @DisplayName("TC: LocationServer reads valid configuration file")
    void testLocationServerReadValidFile(){
        LinkedHashMap<String, String> expectedOutput = new LinkedHashMap<>();
        expectedOutput.put("A", "Indoor");
        expectedOutput.put("B", "Indoor");
        expectedOutput.put("C", "Outdoor");
        expectedOutput.put("D", "Outdoor");

        assertEquals(expectedOutput, locationServer.readConfig());

    }
}
