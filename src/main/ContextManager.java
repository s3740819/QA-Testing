package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.IceStorm.AlreadySubscribed;
import com.zeroc.IceStorm.BadQoS;
import com.zeroc.IceStorm.InvalidSubscriber;
import com.zeroc.IceStorm.TopicPrx;

import helper.*;
import support.LocationDetails;

public class ContextManager {
	//TODO introduce test variables for IntegrationBetweenContextManagerVsLocationServer
	static boolean isSendingLocationList = false;
	static boolean isAPOReached = false;
	static boolean isTemperatureReached = false;
	static boolean isWeatherReached = false;
	static List<String> suggestedLocations;

	//TODO introduce test variables for IntegrationBetweenAllSensorsVsContextManager
	static SensorData receivedSensorDataFromAllSensors;
	static int countTemperatureAlert = 0;
	static int countAPOAlert = 0;

	static List<LocationDetails> cityInfo; // change private -> none
	static Integer currentWeather; // change private to none - package-access
	static LocationWorkerPrx locationWorker; // change private to none
	static PreferenceWorkerPrx preferenceWorker; // changed private -> none
	static WeatherAlarmWorkerPrx weatherAlarmWorker; //changed private -> none
	static Communicator communicator; // changed private -> none
	static LinkedHashMap<String, TopicPrx> subcribers = new LinkedHashMap<>();
	static LinkedHashMap<String, ObjectPrx> proxies = new LinkedHashMap<>();
	static LinkedHashMap<String, User> users = new LinkedHashMap<>();
	static LinkedHashMap<String, AlerterPrx> alerters = new LinkedHashMap<>();
	private static final String INDOOR = "Indoor";
	private static final String OUTDOOR = "Outdoor";
	private static final String APO = "APO";
	private static final String TEMPERATURE = "Temperature";
	private static final String WEATHER = "Weather";
	private static final Integer NORMAL = 0;

	//TODO adding line of code for testing
	private static final Integer OFFSET = 1;
	static PreferenceRequest testRequest = new PreferenceRequest();
	static String testPreference = new String();
	static String testCurrentUsername = new String();
	static String searchInfo = new String();
	static String[] searchItems = new String[0];
	static Boolean shutdown = false;

	public static class MonitorI implements Monitor {
		@Override
		public void report(SensorData sensorData, Current current) {
			//TODO introduce test variable
			receivedSensorDataFromAllSensors = sensorData.clone();
			String username = sensorData.username;
			User user = users.get(username);
			if (user.sensorData == null) {
				user.sensorData = sensorData;
				user.apoThreshhold = calculateapoThreshhold(user);
			}
			String previouslocationStatus = locationWorker.locationMapping(user.sensorData.location);
			String currentLocationStatus = locationWorker.locationMapping(sensorData.location);
			int previousaqi = user.sensorData.aqi;
			int previousTemperature = user.sensorData.temperature;
			user.sensorData = sensorData;
			if (previouslocationStatus.equals(OUTDOOR) && currentLocationStatus.equals(INDOOR)) {
				resetClock(username);
			}
			if (user.sensorData.aqi != previousaqi) {
				resetClock(username);
				user.apoThreshhold = calculateapoThreshhold(user);
			}
			if (user.sensorData.temperature != previousTemperature) {
				user.tempReached = false;
			}
			if (currentLocationStatus.equals(OUTDOOR)) {
				tickClock(username);
			}

			boolean apoReached = checkapoReached(user);
			boolean tempReached = checkTempReached(user);
			if (apoReached) {
				if (!user.apoReached) {
					//TODO test variable
					isAPOReached = true;
					PreferenceRequest request = new PreferenceRequest(username, currentWeather, APO);
					//TODO adding line of code for testing
					testRequest = request;
					String preference = preferenceWorker.getPreference(request);
					//TODO adding line of code for testing
					testPreference=preference;
					List<String> locations = getLocationsByService(preference);
					//TODO add this line of code for testing
					suggestedLocations = locations;
					Alert alert = new Alert(APO, user.sensorData.aqi, locations.stream().toArray(String[]::new));
					//TODO add test variable
					countAPOAlert += 1;
					alerters.get(username).alert(alert);
				}
			} else {
				if (!user.tempReached && tempReached) {
					//TODO test variable
					isTemperatureReached = true;
					PreferenceRequest request = new PreferenceRequest(username, currentWeather,
							Integer.toString(user.sensorData.temperature));
					//TODO adding line of code for testing
					testRequest = request;
					String preference = preferenceWorker.getPreference(request);
					//TODO adding line of code for testing
					testPreference=preference;
					List<String> locations = getLocationsByService(preference);
					//TODO add this line of code for testing
					suggestedLocations = locations;
					Alert alert = new Alert(TEMPERATURE, user.sensorData.temperature,
							locations.stream().toArray(String[]::new));
					//TODO add test variable
					countTemperatureAlert += 1;
					System.out.println(alerters.get(username).toString());
					alerters.get(username).alert(alert);
				}
			}
			user.apoReached = apoReached;
			user.tempReached = tempReached;
			//System.out.println(username + " --Weather: " + currentWeather + "  --Location: " + user.sensorData.location + "  --Location Status:"
				//	+ currentLocationStatus + "  --aqi: " + user.sensorData.aqi + "  --apoThreshhold: "
					//+ user.apoThreshhold + "  --Temperature: " + user.sensorData.temperature + "  --Clock: "
					//+ user.clock);
		}
	}
	
	
	public static class ContextManagerWorkerI implements ContextManagerWorker {

		@Override
		public void addUser(String username, Current current) {
			//TODO adding line for testing
			testCurrentUsername = username;

			User user = preferenceWorker.getUserInfo(username);
			users.put(username, user);
			setupAlerter(username);
			setupSubcriber(username);
			System.out.println(username + " added!");
			checkWeather(currentWeather);
		}

		@Override
		public String searchInfo(String item, Current current) {
			String result = null;
			for (LocationDetails locationDetails : cityInfo) {
				if (locationDetails.getName().equals(item)) {
					result = locationDetails.getInfo();
				}
			}
			//TODO adding line for testing
			searchInfo = result;
			return result;
		}

		@Override
		public String[] searchItems(String username, Current current) {
			List<String> result = new ArrayList<>();
			String currentLocation = users.get(username).sensorData.location;
			for (LocationDetails locationDetails : cityInfo) {
				if (locationDetails.getLocation().equals(currentLocation)) {
					result.add(locationDetails.getName());
				}
			}
			//TODO adding line for testing
			searchItems = result.stream().toArray(String[]::new);
			return result.stream().toArray(String[]::new);
		}

		@Override
		public void deleteUser(String username, Current current) {
			users.remove(username);
			alerters.remove(username);
			subcribers.get(username).unsubscribe(proxies.get(username));
			subcribers.remove(username);
			proxies.remove(username);
			if (users.size() == 0) {
				locationWorker.terminate();
				preferenceWorker.terminate();
				weatherAlarmWorker.terminate();
				communicator.shutdown();
				System.out.println("Context Manager has terminated!");
				//TODO adding line of code for testing
				shutdown=true;
			}
		}

	}

	public static void main(String[] args) {
		communicator = com.zeroc.Ice.Util.initialize(args);
		cityInfo = readCityInfo();
		iniPreferenceWorker();
		iniLocationMapper();
		iniWeatherAlarmWorker();
		runWeatherAlarm();
		setupContextManagerWorker();
		communicator.waitForShutdown();
		System.exit(0);
	}

	private static void setupAlerter(String username) {
		String topicName = username + "-alerts";
		com.zeroc.Ice.ObjectPrx obj = communicator.stringToProxy("IceStorm/TopicManager:tcp -p 10000");
		com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(obj);
		TopicPrx topic = null;
		while (topic == null) {
			try {
				topic = topicManager.retrieve(topicName);
			} catch (com.zeroc.IceStorm.NoSuchTopic ex) {
				try {
					topic = topicManager.create(topicName);
				} catch (com.zeroc.IceStorm.TopicExists ex1) {

				}
			}
		}
		com.zeroc.Ice.ObjectPrx pub = topic.getPublisher().ice_oneway();
		AlerterPrx alerter = AlerterPrx.uncheckedCast(pub);
		alerters.put(username, alerter);
	}

	private static void setupSubcriber(String username) {
		String topicName = username + "-sensors";
		com.zeroc.Ice.ObjectPrx obj = communicator.stringToProxy("IceStorm/TopicManager:tcp -p 10000");
		com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(obj);

		com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(topicName, "tcp");

		Monitor monitor = new MonitorI();
		ObjectPrx proxy = adapter.addWithUUID(monitor).ice_oneway();
		TopicPrx subcriber = null;
		adapter.activate();

		try {
			subcriber = topicManager.retrieve(topicName);
			java.util.Map<String, String> qos = null;
			subcriber.subscribeAndGetPublisher(qos, proxy);
			subcribers.put(username, subcriber);
			proxies.put(username, proxy);
		} catch (com.zeroc.IceStorm.NoSuchTopic ex) {

		} catch (AlreadySubscribed e) {

			e.printStackTrace();
		} catch (BadQoS e) {

			e.printStackTrace();
		} catch (InvalidSubscriber e) {

			e.printStackTrace();
		}
	}

	static void setupContextManagerWorker() { // changed private -> none
		com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("ContextManagerWorker",
				"default -p 13333");
		adapter.add(new ContextManagerWorkerI(), com.zeroc.Ice.Util.stringToIdentity("ContextManagerWorker"));
		adapter.activate();
	}

	static void iniPreferenceWorker() { // changed private -> none
		com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("PreferenceWorker:default -p 14444");
		preferenceWorker = PreferenceWorkerPrx.checkedCast(base);
	}

	static void iniLocationMapper() { // changed private -> none
		com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("LocationWorker:default -p 11111");
		locationWorker = LocationWorkerPrx.checkedCast(base);
	}
	
	static void iniWeatherAlarmWorker() { // changed private -> none
		com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("WeatherAlarmWorker:default -p 15555");
		weatherAlarmWorker = WeatherAlarmWorkerPrx.checkedCast(base);
	}
	
	static void runWeatherAlarm() { // changed private -> none
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true){
					currentWeather = weatherAlarmWorker.getWeather();
					checkWeather(currentWeather);
					try {
						Thread.currentThread();
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}

	static void checkWeather(Integer currentWeather){ //change private to none - package access
		if (currentWeather != NORMAL){
			//TODO add test variable
			isWeatherReached = true;
			alerters.forEach((username,alerter)->{
				PreferenceRequest request = new PreferenceRequest(username, currentWeather, null);
				//TODO addling line of code for testing
				testRequest = request;
				String preference = preferenceWorker.getPreference(request);
				//TODO addling line of code for testing
				testPreference = preference;
				List<String> locations = getLocationsByService(preference);
				//TODO add this line of code for testing
				suggestedLocations = locations;
				Alert alert = new Alert(WEATHER, currentWeather, locations.stream().toArray(String[]::new));
				alerter.alert(alert);
			});
		}
	}
	
	static List<LocationDetails> readCityInfo() { // change private -> none (package-access)
		List<LocationDetails> result = new ArrayList<>();
		File file = new File("CityInfo");
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

	// this function query location status for each location with the specified service, and return only INDOOR location (but the filter function here located in CM)
	static List<String> getLocationsByService(String service) { // change private to none
		List<String> result = new ArrayList<>();
		//TODO a test variable
		isSendingLocationList = true;
		for (LocationDetails locationDetails : cityInfo) {
			if (locationDetails.getServices().contains(service)) {
				String locationStatus = locationWorker.locationMapping(locationDetails.getLocation());
				if (locationStatus.equals(INDOOR)) {
					result.add(locationDetails.getName());
				}
			}
		}
		return result;
	}

	static void resetClock(String username) { // change private to none
		users.get(username).clock = 0;
	}

	static void tickClock(String username) { // change private to none
		users.get(username).clock += 1;
	}

	static boolean checkapoReached(User user) { // change private to none
		return user.clock == user.apoThreshhold;
	}

	static boolean checkTempReached(User user) { // change private to none
		int temperature = user.sensorData.temperature;
		List<Integer> tempThreshholds = Arrays.stream(user.tempThreshholds).boxed().collect(Collectors.toList());
		return temperature >= Collections.min(tempThreshholds);
	}

	static Integer calculateapoThreshhold(User user) { // change private -> none (package-access)
		Integer medicalConditionType = user.medicalConditionType;
		Integer aqi = user.sensorData.aqi;
		Integer result = null;
		if (aqi >= 1 && aqi <= 50) {
			result = medicalConditionType * 30;
		} else if (aqi >= 51 && aqi <= 100) {
			result = medicalConditionType * 15;
		} 
		else if (aqi >= 101 && aqi <= 150) {
			result = medicalConditionType * 10;
		}else {
			result = medicalConditionType * 5;
		}
		return result;
	}

}
