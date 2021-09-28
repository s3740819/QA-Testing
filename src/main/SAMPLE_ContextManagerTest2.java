package main;

import java.util.List;

import main.ContextManager;
import org.junit.Before;
import org.junit.Test;

import helper.SensorData;
import helper.User;
import support.LocationDetails;

import static org.junit.Assert.*;

public class SAMPLE_ContextManagerTest2 {
	private User david1;
	
	@Before
	public void setUp() throws Exception {
		david1 = new User(3, new int[] {16}, 90, 89,
	    		new SensorData("David", "A", 15, 25), 0, false, false);
	}

	@Test
	public void testReadCityInfo() {
		List<LocationDetails> cityInfo = ContextManager.readCityInfo();
		String[] names = {"Vivo City Shopping Centre", "Crescent Mall",
				"Dam Sen Parklands", "Ho Chi Minh City, Downtown"};
		String[] locations = {"A", "B", "C", "D"};

		for (int i=0; i<names.length; i++) {
			assertEquals("Wrong location name. Expect: " + names[i] + ". Actual: " 
							+ cityInfo.get(i).getName(),
					names[i], cityInfo.get(i).getName());
			assertEquals("Wrong location. Expect: " + locations[i] + ". Actual: " + cityInfo.get(i).getLocation(), 
					locations[i], cityInfo.get(i).getLocation());
		}
		assertEquals("Wrong service on location B",
				"cinema", cityInfo.get(1).getServices().get(0));
	}
	
	@Test
	public void testCalculateAPOThreshhold() {
		// with user David1 configuration, expected APO threshold should be 90 
		assertEquals("Check CalculateAPOThreshhold failed", 
				ContextManager.calculateapoThreshhold(david1).intValue(), 90);
	}

}
