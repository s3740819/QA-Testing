package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import helper.PreferenceRequest;
import helper.SensorData;
import helper.User;
import support.Preference;

public class PreferenceRepository {
	static List<Preference> preferences; // changed private -> none
	private static final String APO = "APO";
	private static final String WEATHER = "weather";
	private static final Integer NORMAL = 0;
	public static Communicator communicator;

	public static class PreferenceWorkerI implements helper.PreferenceWorker {

		@Override
		public User getUserInfo(String name, Current current) {
			Integer medicalType = 0;
			Integer clock = 0;
			Integer apoThreshhold = 0;
			SensorData sensorData = null;
			int weather = 0;
			List<Integer> tempThreshholds = new ArrayList<>();
			boolean apoReached = false;
			boolean tempReached = false;
			//TODO adding line of code for testing
//			boolean found = false;
			for (Preference preference : preferences) {
				if (preference.getName().equals(name)) {
//					TODO adding line of code for testing
//					found = true;
//					System.out.println("Received username " + name + " from Context Manager. The username is valid");
					medicalType = preference.getMedicalCondition();
					List<String> suggestions = preference.getSuggestions();
					for (String suggestion : suggestions) {
						try {
							Integer temp = Integer.parseInt(suggestion.split("\\s")[1]);
							tempThreshholds.add(temp);
						} catch (NumberFormatException e) {
						}
					}
				}
			}
			//TODO adding line of code for testing
//			if(found==false) System.out.println("Received username " + name + " from Context Manager. The username does not exist in the repository.");
			User result = new User(medicalType, tempThreshholds.stream().mapToInt(Integer::intValue).toArray(),
					apoThreshhold, clock, sensorData, weather, apoReached, tempReached);
			return result;
		}

		@Override
		public String getPreference(PreferenceRequest request, Current current) {
			//TODO delete this
//			System.out.println("Get preference triggered with request: request.username: " + request.username + " weather: " + request.weather + " value: " + request.value);

			String result = null;
			String username = request.username;
			Integer weather = request.weather;
			String value = request.value;
			if (value.isEmpty()){
				result = getSuggestionWeather(username, weather);
			}
			else{
				if (value.equals(APO)) {
					result = getSuggestionAPO(username);
				} else {
					Integer temp = Integer.parseInt(value);
					result = getSuggestionTemp(username, temp);
				}
			}
			
			return result;
		}

		@Override
		public void terminate(Current current) {
			communicator.shutdown();
			System.out.println("Preference Repository has terminated!");
		}
	}

	public static void main(String[] args) {
		preferences = readPreference();
		setupPreferenceWorker(args);
		communicator.waitForShutdown();
		System.exit(0);
	}

	static void setupPreferenceWorker(String[] args) { // change private to none - package access
		communicator = com.zeroc.Ice.Util.initialize(args);
		com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("PreferenceWorker",
				"default -p 14444");
		adapter.add(new PreferenceWorkerI(), com.zeroc.Ice.Util.stringToIdentity("PreferenceWorker"));
		adapter.activate();
	}

	// read each preference (and store in Preference object) in the Preference file - return a list of Preference objects
	static List<Preference> readPreference() { // changed private -> none
		List<Preference> result = new ArrayList<>();
		File file = new File("Preference");
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

	static String getSuggestionTemp(String name, Integer tempThreshhold) { //change private to none
		String result = null;
		Integer distance = Integer.MAX_VALUE;
		for (Preference preference : preferences) {
			if (preference.getName().equals(name)) {
				List<String> suggestions = preference.getSuggestions();
				for (String suggestion : suggestions) {
					String[] splits = suggestion.split("\\s");
					try {
						Integer temp = Integer.parseInt(splits[1]);
						Integer newDistance = tempThreshhold - temp;
						if (newDistance < distance && temp <= tempThreshhold) {
							result = splits[3];
							distance = tempThreshhold - temp;
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		}
		return result;
	}

	static String getSuggestionAPO(String name) { // change private to none
		String result = null;
		for (Preference preference : preferences) {
			if (preference.getName().equals(name)) {
				List<String> suggestions = preference.getSuggestions();
				for (String suggestion: suggestions){
					String[] splits = suggestion.split("\\s");
					String type = splits[1];
					if (type.equals(APO)){
						result = splits[3];
						break;
					}
				}
			}
		}
		return result;
	}
	
	static String getSuggestionWeather(String name, Integer weather) { // change private to none
		String result = null;
		for (Preference preference : preferences) {
			if (preference.getName().equals(name)) {
				List<String> suggestions = preference.getSuggestions();
				for (String suggestion: suggestions){
					String[] splits = suggestion.split("\\s");
					String type = splits[1];
					if (type.equals(WEATHER)){
						result = splits[3];
						break;
					}
				}
			}
		}
		return result;
	}
}
