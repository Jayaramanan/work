package com.ni3.ag.navigator.client.geoanalytics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import com.ni3.ag.navigator.client.controller.geoanalytics.GeoAnalyticsController;
import junit.framework.TestCase;

public class GeoAnalyticsControllerTest extends TestCase{
	public void testFilterNullValues() throws Exception{
		GeoAnalyticsController controller =  Mockito.mock(GeoAnalyticsController.class);
		Method method = GeoAnalyticsController.class.getDeclaredMethod("filterNullValues", List.class, List.class);
		method.setAccessible(true);

		method.invoke(controller, null, null);
		method.invoke(controller, new ArrayList<Integer>(), null);
		method.invoke(controller, null, new ArrayList<Double>());
		
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> expectedIDs = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);

		expectedIDs.add(1);
		expectedIDs.add(3);
		
		List<Double> values = new ArrayList<Double>();
		List<Double> expectedValues = new ArrayList<Double>();
		values.add(1.0);
		values.add(null);
		values.add(2.0);

		expectedValues.add(1.0);
		expectedValues.add(2.0);
		
		method.invoke(controller, ids, values);
		assertTrue(expectedIDs.equals(ids));
		assertTrue(expectedValues.equals(values));
	}
}
