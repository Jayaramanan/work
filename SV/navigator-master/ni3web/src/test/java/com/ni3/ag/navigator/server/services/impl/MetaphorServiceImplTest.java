/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Metaphor;
import com.ni3.ag.navigator.server.domain.MetaphorData;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.MetaphorIcon;

import junit.framework.TestCase;

public class MetaphorServiceImplTest extends TestCase{
	private MetaphorServiceImpl service;
	private DBObject object;

	@Override
	protected void setUp() throws Exception{
		object = new DBObject(1000, 10);
		object.setData(new HashMap<Integer, String>());
		object.getData().put(100, "200");
		object.getData().put(101, "201");
		object.getData().put(102, "202");

		service = new MetaphorServiceImpl();
	}

	public void testIsExactMatchTrue(){
		List<MetaphorData> list = new ArrayList<MetaphorData>();
		assertTrue(service.isExactMatch(list, object));

		MetaphorData md = new MetaphorData();
		md.setAttributeId(100);
		md.setData(200);
		list.add(md);
		assertTrue(service.isExactMatch(list, object));

		md = new MetaphorData();
		md.setAttributeId(101);
		md.setData(201);
		list.add(md);
		assertTrue(service.isExactMatch(list, object));

		md = new MetaphorData();
		md.setAttributeId(102);
		md.setData(202);
		list.add(md);
		assertTrue(service.isExactMatch(list, object));
	}

	public void testIsExactMatchFalse(){
		List<MetaphorData> list = new ArrayList<MetaphorData>();

		MetaphorData md = new MetaphorData();
		md.setAttributeId(101);
		md.setData(200);
		list.add(md);
		assertFalse(service.isExactMatch(list, object));

		md = new MetaphorData();
		md.setAttributeId(101);
		md.setData(201);
		list.add(md);
		assertFalse(service.isExactMatch(list, object));
	}

	public void testGetMatchCount(){
		List<MetaphorData> list = new ArrayList<MetaphorData>();
		assertEquals(0, service.getMatchCount(list, object));

		MetaphorData md = new MetaphorData();
		md.setAttributeId(100);
		md.setData(200);
		list.add(md);
		assertEquals(1, service.getMatchCount(list, object));

		MetaphorData md1 = new MetaphorData();
		md1.setAttributeId(101);
		md1.setData(201);
		list.add(md1);
		assertEquals(2, service.getMatchCount(list, object));

		MetaphorData md2 = new MetaphorData();
		md2.setAttributeId(102);
		md2.setData(202);
		list.add(md2);
		assertEquals(3, service.getMatchCount(list, object));

		md2.setData(300);
		assertEquals(2, service.getMatchCount(list, object));

		md1.setAttributeId(105);
		assertEquals(1, service.getMatchCount(list, object));
	}

	public void testGetMaxMatchMetaphor(){
		List<Metaphor> metaphors = new ArrayList<Metaphor>();
		List<MetaphorData> data = new ArrayList<MetaphorData>();
		MetaphorData md0 = new MetaphorData();
		md0.setAttributeId(100);
		md0.setData(200);
		data.add(md0);
		MetaphorData md1 = new MetaphorData();
		md1.setAttributeId(101);
		md1.setData(201);
		data.add(md1);
		MetaphorData md2 = new MetaphorData();
		md2.setAttributeId(102);
		md2.setData(202);
		data.add(md2);

		Metaphor metaphor1 = new Metaphor();
		metaphor1.setObjectDefinitionId(11); // different od
		metaphor1.setMetaphorSet("set");
		metaphor1.setMetaphorData(data);
		metaphors.add(metaphor1);

		assertNull(service.getMaxMatchMetaphor(object, metaphors, "set"));

		List<MetaphorData> data2 = new ArrayList<MetaphorData>();
		MetaphorData md20 = new MetaphorData();
		md20.setAttributeId(100);
		md20.setData(200);
		data2.add(md20);
		MetaphorData md21 = new MetaphorData();
		md21.setAttributeId(101);
		md21.setData(201);
		data2.add(md21);
		MetaphorData md22 = new MetaphorData();
		md22.setAttributeId(102);
		md22.setData(202);
		data2.add(md22);

		Metaphor metaphor2 = new Metaphor();
		metaphor2.setObjectDefinitionId(10);
		metaphor2.setMetaphorSet("differentSet"); // different metaphor set
		metaphor2.setMetaphorData(data2);
		metaphors.add(metaphor2);
		assertNull(service.getMaxMatchMetaphor(object, metaphors, "set"));

		metaphor1.setObjectDefinitionId(10); // same od
		assertSame(metaphor1, service.getMaxMatchMetaphor(object, metaphors, "set"));

		metaphor1.setObjectDefinitionId(11); // different od
		metaphor2.setMetaphorSet("set"); // same metaphor set
		assertSame(metaphor2, service.getMaxMatchMetaphor(object, metaphors, "set"));

		metaphor1.setObjectDefinitionId(10);
		md0.setAttributeId(105);
		assertSame(metaphor2, service.getMaxMatchMetaphor(object, metaphors, "set"));

		md21.setData(300);
		assertSame(metaphor1, service.getMaxMatchMetaphor(object, metaphors, "set"));

		md1.setData(301);
		assertSame(metaphor2, service.getMaxMatchMetaphor(object, metaphors, "set"));

		md22.setData(302);
		assertSame(metaphor1, service.getMaxMatchMetaphor(object, metaphors, "set"));

		md2.setData(302);
		assertSame(metaphor2, service.getMaxMatchMetaphor(object, metaphors, "set"));
	}

	public void testGetAssignedIconName(){
		ObjectDefinition od = new ObjectDefinition(10);
		od.setAttributes(new ArrayList<Attribute>());

		assertNull(service.getAssignedIconName(object, od));

		final Attribute a = new Attribute(111);
		a.setName("iconname");
		od.getAttributes().add(a);
		assertNull(service.getAssignedIconName(object, od));

		object.getData().put(a.getId(), "");
		assertNull(service.getAssignedIconName(object, od));

		object.getData().put(a.getId(), "icon.png");
		assertEquals("icon.png", service.getAssignedIconName(object, od));
	}

	public void testGetMetaphor(){
		List<Metaphor> metaphors = new ArrayList<Metaphor>();
		List<MetaphorData> data = new ArrayList<MetaphorData>();
		MetaphorData md1 = new MetaphorData();
		md1.setAttributeId(100);
		md1.setData(200);
		data.add(md1);
		MetaphorData md2 = new MetaphorData();
		md2.setAttributeId(102);
		md2.setData(202);
		data.add(md2);

		Metaphor metaphor1 = new Metaphor();
		metaphor1.setObjectDefinitionId(11); // different od
		metaphor1.setMetaphorSet("set");
		metaphor1.setIconName("metaphor1.png");
		metaphor1.setPriority(1);
		metaphor1.setMetaphorData(data);
		metaphors.add(metaphor1);

		List<MetaphorData> data2 = new ArrayList<MetaphorData>();
		MetaphorData md21 = new MetaphorData();
		md21.setAttributeId(101);
		md21.setData(201);
		data2.add(md21);
		MetaphorData md22 = new MetaphorData();
		md22.setAttributeId(102);
		md22.setData(202);
		data2.add(md22);

		Metaphor metaphor2 = new Metaphor();
		metaphor2.setObjectDefinitionId(10);
		metaphor2.setMetaphorSet("differentSet"); // different metaphor set
		metaphor2.setIconName("metaphor2.png");
		metaphor2.setPriority(1);
		metaphor2.setMetaphorData(data2);
		metaphors.add(metaphor2);

		ObjectDefinition od = new ObjectDefinition(10);
		od.setAttributes(new ArrayList<Attribute>());
		od.setMetaphors(metaphors);

		MetaphorIcon result = service.getMetaphor(object, od, "set"); // no metaphor found
		assertNotNull(result);
		assertEquals("all.png", result.getIconName());
		assertEquals(new Integer(100), result.getPriority());

		metaphor1.setObjectDefinitionId(10);
		result = service.getMetaphor(object, od, "set"); // the first metaphor
		assertNotNull(result);
		assertEquals("metaphor1.png", result.getIconName());
		assertEquals(new Integer(1), result.getPriority());

		metaphor2.setMetaphorSet("set");
		result = service.getMetaphor(object, od, "set"); // the first metaphor, as priorities are the same
		assertNotNull(result);
		assertEquals("metaphor1.png", result.getIconName());
		assertEquals(new Integer(1), result.getPriority());

		metaphor1.setPriority(3);
		result = service.getMetaphor(object, od, "set"); // the second metaphor, as its priority is higher (lower value)
		assertNotNull(result);
		assertEquals("metaphor2.png", result.getIconName());
		assertEquals(new Integer(1), result.getPriority());

		md22.setData(300);
		result = service.getMetaphor(object, od, "set"); // the first metaphor, as the second doesn't match exactly
		assertNotNull(result);
		assertEquals("metaphor1.png", result.getIconName());
		assertEquals(new Integer(3), result.getPriority());

		md1.setData(301);
		md2.setData(302);
		result = service.getMetaphor(object, od, "set"); // the second metaphor, as it has bigger match count
		assertNotNull(result);
		assertEquals("metaphor2.png", result.getIconName());
		assertEquals(new Integer(1), result.getPriority());

		md1.setData(200);
		md2.setData(202);
		data2.remove(1);
		result = service.getMetaphor(object, od, "set"); // the second metaphor, as its priority is higher (lower value)
		assertNotNull(result);
		assertEquals("metaphor2.png", result.getIconName());
		assertEquals(new Integer(1), result.getPriority());

		final Attribute a = new Attribute(111);
		a.setName("iconname");
		od.getAttributes().add(a);
		object.getData().put(a.getId(), "icon.png");
		result = service.getMetaphor(object, od, "set"); // assigned metaphor
		assertNotNull(result);
		assertEquals("icon.png", result.getIconName());
		assertEquals(new Integer(0), result.getPriority());
	}

}
