package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.IceStorm.TopicPrx;

import helper.Alarm;
import helper.AlarmPrx;
import helper.Monitor;
import helper.MonitorPrx;
import helper.SensorData;
import main.PreferenceRepository.PreferenceWorkerI;
import support.HandleUserInput;

public class WeatherAlarms {
	List<Integer> weatherConditions; // change private to none (package-access)
	Iterator<Integer> iterator; // change private to none (package-access)
	Communicator communicator; // change private to non (package-access)
	
	public WeatherAlarms(){
		this.weatherConditions = readWeatherConditions();
		this.iterator = weatherConditions.iterator();
	}
	
	public class WeatherAlarmWorkerI implements helper.WeatherAlarmWorker {

		@Override
		public int getWeather(Current current) {
			if (!iterator.hasNext()) {
				iterator = weatherConditions.iterator();
			}
			return iterator.next();
		}

		@Override
		public void terminate(Current current) {
			communicator.shutdown();
			System.out.println("Weather Alarm has terminated!");
		}
	}
	
	public static void main(String[] args) {
		WeatherAlarms weatherAlarms = new WeatherAlarms();
		weatherAlarms.setupWeatherAlarmWorker(args);
		weatherAlarms.communicator.waitForShutdown();
		System.exit(0);
	}
	
	void setupWeatherAlarmWorker(String[] args) { // change private to non
		communicator = com.zeroc.Ice.Util.initialize(args);
		com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("WeatherAlarmWorker",
				"default -p 15555");
		adapter.add(new WeatherAlarmWorkerI(), com.zeroc.Ice.Util.stringToIdentity("WeatherAlarmWorker"));
		adapter.activate();
	}
	
	List<Integer> readWeatherConditions(){ // change private to none
		File file = new File("weather_alarms.txt");
		List<Integer> result = new ArrayList<>();
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				result.add(Integer.parseInt(sc.nextLine()));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

}
