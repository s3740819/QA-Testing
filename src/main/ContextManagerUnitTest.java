package main;

import com.zeroc.Ice.Current;
import helper.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.LocationDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextManagerUnitTest {
    static WeatherAlarms weatherAlarms =new WeatherAlarms();
    @BeforeAll
    static void initialContextManager(){
        Handler.initialize(weatherAlarms);
    }

    @AfterAll
    public static void reset(){
        weatherAlarms.communicator.shutdown();
    }

    static void restartContextManager(){
        Handler.reset();
        initialContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,2","David,3","Bao,null"})
    @DisplayName("Test add user method")
    void testAddUser(String data){
        String user = data.split(",")[0];
        String expectedMedicalCondition = data.split(",")[1];
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        contextManagerWorkerI.addUser(user, new Current());
        if (!user.equals("Jack") && !user.equals("David")){
            assertEquals(0, ContextManager.users.size());
        }
        else{
            assertEquals(1, ContextManager.users.size());
            assertEquals(Integer.parseInt(expectedMedicalCondition), ContextManager.users.get(user).medicalConditionType);
        }
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Dam Sen Parklands~The Dam Sen Parklands area was created as part of the rejuvenation of the industrial upgrade undertaken for World Expo 1988. The Parklands area is spacious with plenty of green and spaces for all ages. A big lake promenade stretches the area of Dam Sen Parklands."
                            ,"Crescent Mall~Crescent Mall Shopping Centre is located 10km South of the Ho Chi Minh City central business district(CBD) and includes Banana Republic, Baskin Robins, CGV Cinema, Bobapop and over 130 specialty stores."
                            , "Vivo City Shopping Centre~Vivo City Shopping Centre is a major regional shopping centre in the southern suburb of Ho Chi Minh City, Vietnam. It is the second largest shopping centre in the southern suburbs of Ho Chi Minh City, by gross area, and contains the only H&M store in that region."
                            ,"Ho Chi Minh City, Downtown~The Ho Chi Minh City central business district (CBD), or 'the City' is located on a central point in district One. The point, known at its tip as Central Point, slopes upward to the north-west where 'the city' is bounded by parkland and the inner city suburb of District 3, District 4 and District 5."
                            ,"Vivo~null", "Vivo City~null"})
    @DisplayName("Test city info search method")
    void test(String data){
        String info = data.split("~")[0];
        String expected = data.split("~")[1];
        ContextManager.cityInfo = ContextManager.readCityInfo();
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        String actual = contextManagerWorkerI.searchInfo(info, new Current());
        System.out.println(actual);
        if (actual == null) actual = "null";
        assertEquals(expected, actual);
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,A","Jack,B","Jack,C","Jack,D","Jack,asd","Jack,123"
                            ,"David,A","David,B","David,C","David,D","David,!@","David,321"
                            ,"Bao,A"})
    @DisplayName("Test item search method")
    void testItemSearch(String data){
        String user = data.split(",")[0];
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        Current current = new Current();
        contextManagerWorkerI.addUser(user, current);
        ContextManager.users.get(user).sensorData.location = data.split(",")[1];
        String expected = "";
        if (data.split(",")[1].equals("C")) expected = "[Dam Sen Parklands]";
        else if (data.split(",")[1].equals("D")) expected = "[Ho Chi Minh City, Downtown]";
        else if (data.split(",")[1].equals("B")) expected = "[Crescent Mall]";
        else if (data.split(",")[1].equals("A")) expected = "[Vivo City Shopping Centre]";
        else expected = "[]";
        assertEquals(expected, Arrays.toString(contextManagerWorkerI.searchItems(user, current)));
        restartContextManager();

    }

    @Test
    @DisplayName("Test read city info")
    void testReadCityInfo(){
        List<LocationDetails> list = ContextManager.readCityInfo();
        String expectedResult = "[LocationDetails [name=Vivo City Shopping Centre, location=A, info=Vivo City Shopping Centre is a major regional shopping centre in the southern suburb of Ho Chi Minh City, Vietnam. It is the second largest shopping centre in the southern suburbs of Ho Chi Minh City, by gross area, and contains the only H&M store in that region., services=[cinema, restaurants, pool, shops, bowling]]" +
                ", LocationDetails [name=Crescent Mall, location=B, info=Crescent Mall Shopping Centre is located 10km South of the Ho Chi Minh City central business district(CBD) and includes Banana Republic, Baskin Robins, CGV Cinema, Bobapop and over 130 specialty stores., services=[cinema, restaurants, shops]]" +
                ", LocationDetails [name=Dam Sen Parklands, location=C, info=The Dam Sen Parklands area was created as part of the rejuvenation of the industrial upgrade undertaken for World Expo 1988. The Parklands area is spacious with plenty of green and spaces for all ages. A big lake promenade stretches the area of Dam Sen Parklands., services=[restaurants, pool, shops, Ferris wheel]]" +
                ", LocationDetails [name=Ho Chi Minh City, Downtown, location=D, info=The Ho Chi Minh City central business district (CBD), or 'the City' is located on a central point in district One. The point, known at its tip as Central Point, slopes upward to the north-west where 'the city' is bounded by parkland and the inner city suburb of District 3, District 4 and District 5., services=[restaurants, shops, market, bowling]]]";
        assertEquals(expectedResult, Arrays.toString(list.toArray()));
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,30", "David,100", "Jack,-1", "David,-100"})
    @DisplayName("Test tick clock function")
    void testTickClock(String data){
        String user = data.split(",")[0];
        int initialClock = Integer.parseInt(data.split(",")[1]);
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        Current current = new Current();
        contextManagerWorkerI.addUser(user, current);
        ContextManager.users.get(user).clock = initialClock;
        ContextManager.tickClock(user);
        assertEquals(initialClock+1, ContextManager.users.get(user).clock);
        System.out.println(ContextManager.users.get(user).clock);
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack,30","Jack,-1", "David,100", "David,-100"})
    @DisplayName("Test reset clock function")
    void testResetClock(String data){
        String user = data.split(",")[0];
        int initialClock = Integer.parseInt(data.split(",")[1]);
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        Current current = new Current();
        contextManagerWorkerI.addUser(user, current);
        ContextManager.users.get(user).clock = initialClock;
        ContextManager.resetClock(user);
        assertEquals(0, ContextManager.users.get(user).clock);
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jack","David","Bao"})
    @DisplayName("Test delete user method")
    void testDeleteUser(String data){
        String user = data.split(",")[0];
        ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
        int initialSize;
        if (user.equals("Jack") || user.equals("David")) {
            contextManagerWorkerI.addUser(user, new Current());
            initialSize = ContextManager.users.size();
            contextManagerWorkerI.deleteUser(user, new Current());
            assertEquals(initialSize-1, ContextManager.users.size());
            initialContextManager();
        }
        else {
            ContextManager.users.put(user, new User());
            initialSize = ContextManager.users.size();
            assertEquals(initialSize, ContextManager.users.size());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,40,30","1,80,20","2,10,60","2,80,40","2,150,20","3,0,80","3,55,77","3,111,30","2,159,10","1,151,10","1,150,0","2,200,20"})
    @DisplayName("Test APO reached checker")
    void testCheckAPOReach(String data){
        User user = new User();
        int aqi = Integer.parseInt(data.split(",")[1]);
        int type;
        if (aqi <= 50) type = 30;
        else if (aqi <= 100) type = 15;
        else if (aqi <= 150) type = 10;
        else type = 5;
        int medicalType = Integer.parseInt(data.split(",")[0]);
        user.apoThreshhold = medicalType*type;
        user.clock = Integer.parseInt(data.split(",")[2]);
        assertEquals((user.clock >= user.apoThreshhold), ContextManager.checkapoReached(user));
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"10,20,30","20,20,30","25,15,30","30,20,25","-10,10,20","-1,10", "20,10,20,30,40,50,60"})
    @DisplayName("Test temperature reached checker")
    void testCheckTempReach(String data){
        User user = new User();
        int currentTemp = Integer.parseInt(data.split(",")[0]);
        user.sensorData.temperature = currentTemp;
        user.tempThreshholds = new int[ data.split(",").length-1];
        boolean expected = false;
        for (int i = 1; i < data.split(",").length; i++){
            user.tempThreshholds[i-1] = Integer.parseInt(data.split(",")[i]);
            if (currentTemp >=Integer.parseInt(data.split(",")[i])) expected = true;
        }
        assertEquals(expected, ContextManager.checkTempReached(user));
        restartContextManager();
    }


    @ParameterizedTest
    @ValueSource(strings = {"1,0","1,50","1,51","1,100","1,150","1,151", "1,200", "1,250", "1,-1", "2,33", "2,111", "2,-1", "3,55", "3,-1", "3,500", "0,10"})
    @DisplayName("Test APO calculator")
    void testAPOCalculation(String data){
        User user = new User();
        user.sensorData.aqi = Integer.parseInt(data.split(",")[1]);
        user.medicalConditionType = Integer.parseInt(data.split(",")[0]);
        String expected;
        if (user.sensorData.aqi <= 50) expected = String.valueOf(user.medicalConditionType*30);
        else if (user.sensorData.aqi <= 100) expected = String.valueOf(user.medicalConditionType*15);
        else if (user.sensorData.aqi <= 150) expected = String.valueOf(user.medicalConditionType*10);
        else if (user.sensorData.aqi <= 200) expected = String.valueOf(user.medicalConditionType*5);
        else expected = "null";
        if (user.medicalConditionType == 0 || user.sensorData.aqi < 0) expected = "null";
        assertEquals(expected, ContextManager.calculateapoThreshhold(user).toString());
        restartContextManager();
    }

    @ParameterizedTest
    @ValueSource(strings = {"pool","shops","bowling","restaurants","market","cinema","gym", "abc"})
    @DisplayName("Test the getter: locations by service")
    void testGetLocationByService(String service){
        ContextManager.cityInfo = ContextManager.readCityInfo();
        ArrayList<String> expected = new ArrayList<>();
        for (LocationDetails locationDetails : ContextManager.cityInfo){
            if (locationDetails.getServices().contains(service) && ContextManager.locationWorker.locationMapping(locationDetails.getLocation()).equals("Indoor")){
                expected.add(locationDetails.getName());
            }
        }
        assertEquals(Arrays.toString(expected.toArray()), Arrays.toString(ContextManager.getLocationsByService(service).toArray()));
        restartContextManager();
    }
}