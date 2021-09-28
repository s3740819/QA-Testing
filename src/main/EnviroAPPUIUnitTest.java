package main;

import com.zeroc.Ice.Current;
import helper.Alert;
import main.EnviroAPPUI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EnviroAPPUIUnitTest {

    @ParameterizedTest
    @ValueSource(strings={"Something","","123"})
    @DisplayName("Test print function")
    void printMessage(String message) {
        String first = "*******************************************************************\n" +
                "Context-aware UV Smart Application Main Menu\n";
        String second="Please select an option\n" +
                "1. Search for information on a specific item of interest\n" +
                "2. Search for items of interest in current location\n" +
                "E. Exit\n";
        String out;
        if (!message.equals("")){
            out = first + message +"\n" + second;
        }
        else out = first + second;
        out = out.replaceAll("\n", "").replaceAll("\r", "");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        EnviroAPPUI.printMessage(message);
        assertEquals(out,outContent.toString().replaceAll("\n", "\r").replaceAll("\r", ""));
        System.setOut(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Weather,1,cinema,pool", "Weather,2,mall,pagoda"
            , "Weather,3","Weather,0,pool", "Weather,5,park", "APO,160,pool","APO,-10,pool"
            , "APO,300,pool" , "Temperature,-10,pool", "Temperature,30,park", "abc,2,football" })
    @DisplayName("Test alert")
    void testAlert(String data){
        String[] split = data.split(",");
        String type = split[0];
        int value = Integer.parseInt(split[1]);
        String[] locations = new String[split.length-2];
        for (int i = 2, index = 0; i < split.length;i++, index++){
            locations[index] = split[i];
        }
        Alert alert = new Alert(type, value, locations);
        EnviroAPPUI.AlerterI alerterI = new EnviroAPPUI.AlerterI();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        alerterI.alert(alert, new Current());
        String message = "*******************************************************************\nContext-aware UV Smart Application Main Menu\n";
        if (type.equals("Weather") && value < 4 && value > 0) message += "Warning, extreme weather is detected, the current weather event is  " + EnviroAPPUI.weatherMapping.get(value) + "\n";
        else if (type.equals("Temperature")) message += "Warning,Temperature is now: " + value + "\n";
        else if (type.equals("APO") && value < 250 && value >= 0) message += "Warning, significant air pollution level detected, the current AQI is " + value + "\n";
        if (!message.equals("*******************************************************************\nContext-aware UV Smart Application Main Menu\n")) {
            message += "Suggestion - please go to: " + "\n";
            for (String location : locations) {
                message += location + " , ";
            }
        }
        String expected = outContent.toString().split("Please select an option")[0].replaceAll("\n", "\r").replaceAll("\r", "");
        assertEquals(expected, (message).replaceAll("\n", "\r").replaceAll("\r", ""));
    }
}