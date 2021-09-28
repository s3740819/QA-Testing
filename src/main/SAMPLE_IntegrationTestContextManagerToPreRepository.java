package main;

import com.zeroc.Ice.Current;
import helper.PreferenceRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class SAMPLE_IntegrationTestContextManagerToPreRepository {
	/* You need to run the following models to test this intergrationPreference
	 * PreferenceRepository.java
	 * LocationServer.java
	 */
	private static WeatherAlarms weatherAlarms = new WeatherAlarms();
	//Setting the environment for integrating the CM to the PR
	@Before
	public void setUpBeforeClass() {
		Handler.initialize(weatherAlarms);

		//Add user
		String username = "Jack";
		ContextManager.ContextManagerWorkerI contextManagerWorkerI = new ContextManager.ContextManagerWorkerI();
		contextManagerWorkerI.addUser(username,new Current());
	}
	@AfterAll
	public static void restart(){
		weatherAlarms.communicator.shutdown();
	}
	@After
	public void reset(){
		Handler.reset();
	}
	/* Test user preference message sending from the Preference Repository module
	 * to the ContextManager
	 */
	@Test
	public void sendAlertTemperature() {
		PreferenceRequest request = new PreferenceRequest("Jack", 0, "25");
		// actual output from the PR
		String suggest = ContextManager.preferenceWorker.getPreference(request);
		// assert the expected suggestion against the suggestion from the PR
		assertEquals("shops",suggest);
	}

	@Test
	public void sendAlertTemperature1() {
		PreferenceRequest request = new PreferenceRequest("Jack", 0, "25");
		// actual output from the PR
		String suggest = ContextManager.preferenceWorker.getPreference(request);
		// assert the expected suggestion against the suggestion from the PR
		assertEquals("shops",suggest);
	}
}
