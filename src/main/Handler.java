package main;

public class Handler {
    static void initialize(WeatherAlarms weatherAlarms){
        if (LocationServer.communicator != null && !LocationServer.communicator.isShutdown()){
            LocationServer.communicator.shutdown();
        }
        if (ContextManager.communicator != null && !ContextManager.communicator.isShutdown()){
            ContextManager.communicator.shutdown();
        }
        if (EnviroAPPUI.communicator != null && !EnviroAPPUI.communicator.isShutdown()){
            EnviroAPPUI.communicator.shutdown();
        }
        if (PreferenceRepository.communicator != null && !PreferenceRepository.communicator.isShutdown()){
            PreferenceRepository.communicator.shutdown();
        }
        LocationServer.table = LocationServer.readConfig();
        LocationServer.setupLocationWorker(new String[0]);
        PreferenceRepository.preferences = PreferenceRepository.readPreference();
        PreferenceRepository.setupPreferenceWorker(new String[0]);
        weatherAlarms.setupWeatherAlarmWorker(new String[0]);
        ContextManager.communicator = com.zeroc.Ice.Util.initialize(new String[0]);
        ContextManager.cityInfo = ContextManager.readCityInfo();
        ContextManager.iniPreferenceWorker();
        ContextManager.iniLocationMapper();
        ContextManager.iniWeatherAlarmWorker();
        ContextManager.runWeatherAlarm();
        ContextManager.setupContextManagerWorker();
    }

    static void reset (){
        ContextManager.locationWorker.terminate();
        ContextManager.preferenceWorker.terminate();
        ContextManager.weatherAlarmWorker.terminate();
        ContextManager.communicator.shutdown();
        ContextManager.alerters.clear();
        ContextManager.users.clear();
        ContextManager.proxies.clear();
        ContextManager.subcribers.clear();
    }
}
