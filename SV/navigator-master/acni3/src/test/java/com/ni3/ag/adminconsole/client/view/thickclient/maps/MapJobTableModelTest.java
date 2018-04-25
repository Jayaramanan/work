/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.User;

import junit.framework.TestCase;

public class MapJobTableModelTest extends TestCase{

	public void testValidateLongitude(){
		MapJobTableModel model = new MapJobTableModel();
		assertEquals(100.0, model.validateLongitude(new BigDecimal(100)).doubleValue());
		assertEquals(180.0, model.validateLongitude(new BigDecimal(200)).doubleValue());
		assertEquals(180.0, model.validateLongitude(new BigDecimal(180.1)).doubleValue());
		assertEquals(-180.0, model.validateLongitude(new BigDecimal(-200)).doubleValue());
		assertEquals(-100.0, model.validateLongitude(new BigDecimal(-100)).doubleValue());
		assertNull(model.validateLongitude(null));
	}

	public void testValidateLatitude(){
		MapJobTableModel model = new MapJobTableModel();
		assertEquals(50.0, model.validateLatitude(new BigDecimal(50)).doubleValue());
		assertEquals(90.0, model.validateLatitude(new BigDecimal(200)).doubleValue());
		assertEquals(90.0, model.validateLatitude(new BigDecimal(90.1)).doubleValue());
		assertEquals(-90.0, model.validateLatitude(new BigDecimal(-200)).doubleValue());
		assertEquals(-9.0, model.validateLatitude(new BigDecimal(-9)).doubleValue());
		assertNull(model.validateLatitude(null));
	}

	public void testValidateScale(){
		User user = new User();
		user.setId(1);
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "100000,300000,5000000");
		MapJobTableModel model = new MapJobTableModel(null, map);
		String result = model.validateScale("123, 234", user);
		assertEquals("", result);

		result = model.validateScale("123, 100000,300000", user);
		assertEquals("100000,300000", result);

		result = model.validateScale("5000000, 100000,  300000 ", user);
		assertEquals("100000,300000,5000000", result);

		assertNull(model.validateScale("", null));
	}
}
