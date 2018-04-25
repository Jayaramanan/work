package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.mockito.Mockito;

import com.ni3.ag.navigator.client.domain.datatype.Ni3Datatype;
import com.ni3.ag.navigator.client.util.PrivateAccesor;
import com.ni3.ag.navigator.shared.domain.DataType;

public class DBObjectTest extends TestCase{
	public void setUp() throws Exception{
		super.setUp();
	}

	public void tearDown() throws Exception{
		super.tearDown();
	}

	/**
	 * Method: getId()
	 */
	public void testGetId() throws Exception{
		DBObject obj = new DBObject(1);
		assertEquals(1, obj.getId());
		obj.setId(2);
		assertEquals(2, obj.getId());
	}

	public void testGetEntity() throws Exception{
		Entity e = new Entity(1);
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute a1 = new Attribute();
		attributes.add(a1);
		PrivateAccesor.setPrivateField(e, "attributesAll", attributes);
		DBObject o = new DBObject(e);
		assertSame(e, o.getEntity());
	}

	public void testReloadData() throws Exception{
		Entity e = new Entity(2);
		e.setSchema(Mockito.mock(Schema.class));
		List<Attribute> attributes = new ArrayList<Attribute>();
		PrivateAccesor.setPrivateField(e, "attributesAll", attributes);

		Attribute a1 = new Attribute();
		PrivateAccesor.setPrivateField(a1, "ID", 1);
		PrivateAccesor.setPrivateField(a1, "dType", DataType.TEXT);
		PrivateAccesor.setPrivateField(a1, "datatype", Ni3Datatype.createDatatype(a1));
		attributes.add(a1);

		Attribute a2 = new Attribute();
		PrivateAccesor.setPrivateField(a2, "ID", 2);
		PrivateAccesor.setPrivateField(a2, "dType", DataType.INT);
		PrivateAccesor.setPrivateField(a2, "datatype", Ni3Datatype.createDatatype(a2));
		attributes.add(a2);

		com.ni3.ag.navigator.shared.domain.DBObject dbObject = new com.ni3.ag.navigator.shared.domain.DBObject(1, 2);
		dbObject.setData(new HashMap<Integer, String>());
		dbObject.getData().put(1, "hello");
		dbObject.getData().put(2, "1");

		DBObject o = new DBObject(e);
		o.setData(dbObject);

		assertEquals("hello", o.getValue(1));
		assertEquals(1, o.getValue(2));
	}

	public void testAssignValue() throws Exception{
		Entity e = new Entity(2);
		e.setSchema(Mockito.mock(Schema.class));
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<Attribute> contextAttributes = new ArrayList<Attribute>();
		PrivateAccesor.setPrivateField(e, "attributesAll", attributes);

		Attribute a1 = new Attribute();
		PrivateAccesor.setPrivateField(a1, "ID", 1);
		PrivateAccesor.setPrivateField(a1, "dType", DataType.TEXT);
		PrivateAccesor.setPrivateField(a1, "datatype", Ni3Datatype.createDatatype(a1));
		attributes.add(a1);

		Attribute a2 = new Attribute();
		PrivateAccesor.setPrivateField(a2, "ID", 2);
		PrivateAccesor.setPrivateField(a2, "dType", DataType.INT);
		PrivateAccesor.setPrivateField(a2, "datatype", Ni3Datatype.createDatatype(a2));
		attributes.add(a2);

		Attribute a3 = new Attribute();
		PrivateAccesor.setPrivateField(a3, "ID", 3);
		PrivateAccesor.setPrivateField(a3, "dType", DataType.TEXT);
		PrivateAccesor.setPrivateField(a3, "datatype", Ni3Datatype.createDatatype(a3));
		contextAttributes.add(a3);
		attributes.add(a3);

		Attribute a4 = new Attribute();
		PrivateAccesor.setPrivateField(a4, "ID", 4);
		PrivateAccesor.setPrivateField(a4, "dType", DataType.INT);
		PrivateAccesor.setPrivateField(a4, "datatype", Ni3Datatype.createDatatype(a4));
		contextAttributes.add(a4);
		attributes.add(a4);

		DBObject o = new DBObject(e);
		com.ni3.ag.navigator.shared.domain.DBObject dbObject = new com.ni3.ag.navigator.shared.domain.DBObject(1, 2);
		dbObject.setData(new HashMap<Integer, String>());
		dbObject.getData().put(1, "hello");
		dbObject.getData().put(2, "1");
		dbObject.getData().put(3, "zz");
		dbObject.getData().put(4, "3");

		o.setData(dbObject);
		assertEquals("hello", o.getValue(1));
		assertEquals(1, o.getValue(2));
		assertEquals("zz", o.getValue(3));
		assertEquals(3, o.getValue(4));

		o.assignValue("aaa", a3);
		o.assignValue("10", a4);
		assertEquals("hello", o.getValue(1));
		assertEquals(1, o.getValue(2));
		assertEquals("aaa", o.getValue(3));
		assertEquals(10, o.getValue(4));
	}

	public void testGetValueAsDouble(){
		DBObject obj = new DBObject();
		Attribute attr = new Attribute();
		obj.getData().put(100, null);
		obj.getData().put(101, new Value(0, 0, "10", "label"));
		obj.getData().put(102, 10.5);
		obj.getData().put(103, 10);

		attr.ID = 100;
		assertEquals(0.0, obj.getValueAsDouble(attr));

		attr.ID = 101;
		assertEquals(0.0, obj.getValueAsDouble(attr));
		attr.ID = 102;
		assertEquals(10.5, obj.getValueAsDouble(attr));
		attr.ID = 103;
		assertEquals(10.0, obj.getValueAsDouble(attr));
	}
}
