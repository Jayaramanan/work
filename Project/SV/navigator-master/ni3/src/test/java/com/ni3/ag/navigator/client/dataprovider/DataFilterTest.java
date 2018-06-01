/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.dataprovider;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.*;
import org.mockito.Mockito;

import junit.framework.TestCase;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Value;

public class DataFilterTest extends TestCase{
	private DataFilter filter;
	private DBObject dbObject;

	@Override
	protected void setUp() throws Exception{
		filter = new DataFilter();
		Entity entity = Mockito.mock(Entity.class);
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(createAttribute(100, entity, false, false));
		attributes.add(createAttribute(101, entity, false, true));
		attributes.add(createAttribute(102, entity, true, false));
		attributes.add(createAttribute(103, entity, true, true));
		Mockito.when(entity.getReadableAttributes()).thenReturn(attributes);

		dbObject = new DBObject(entity);

		filter.addExclusion(new Value(111, 0, null, null));
		filter.addExclusion(new Value(112, 0, null, null));
		filter.addExclusion(new Value(113, 0, null, null));
	}

	public void testIsObjectFilteredOutEmptyFilter(){
		assertFalse(filter.isObjectFilteredOut(dbObject));
	}

	public void testIsObjectFilteredOutIncorrectObject(){
		assertFalse(filter.isObjectFilteredOut(null));
		assertFalse(filter.isObjectFilteredOut(new DBObject()));
	}

	public void testIsObjectFilteredOutNotFiltered(){
		dbObject.setValue(100, "not predefined value");
		dbObject.setValue(101, null);
		dbObject.setValue(102, new Value(114, 0, null, null));
		dbObject.setValue(103, new Value[] { new Value(115, 0, null, null), new Value(116, 0, null, null) });
		assertFalse(filter.isObjectFilteredOut(dbObject));

		dbObject.setValue(103, new Value[] { new Value(113, 0, null, null), new Value(116, 0, null, null) });
		assertFalse(filter.isObjectFilteredOut(dbObject));

		dbObject.setValue(103, null);
		assertFalse(filter.isObjectFilteredOut(dbObject));

		dbObject.setValue(102, null);
		dbObject.setValue(103, new Value[] { new Value(115, 0, null, null), new Value(116, 0, null, null) });
		assertFalse(filter.isObjectFilteredOut(dbObject));
	}

	public void testIsObjectFilteredFilteredByPredefined(){
		dbObject.setValue(100, "not predefined value");
		dbObject.setValue(101, null);
		dbObject.setValue(102, new Value(113, 0, null, null));
		dbObject.setValue(103, new Value[] { new Value(115, 0, null, null), new Value(116, 0, null, null) });
		assertTrue(filter.isObjectFilteredOut(dbObject));
	}

	public void testIsObjectFilteredFilteredByMultiPredefined(){
		dbObject.setValue(100, "not predefined value");
		dbObject.setValue(101, null);
		dbObject.setValue(102, new Value(114, 0, null, null));
		dbObject.setValue(103, new Value[] { new Value(113, 0, null, null) });
		assertTrue(filter.isObjectFilteredOut(dbObject));

		dbObject.setValue(103, new Value[] { new Value(111, 0, null, null), new Value(112, 0, null, null) });
		assertTrue(filter.isObjectFilteredOut(dbObject));
	}

	private Attribute createAttribute(int id, Entity entity, boolean predefined, boolean multivalue){
		Attribute attribute = new Attribute();
		attribute.ID = id;
		attribute.ent = entity;
		attribute.predefined = predefined;
		attribute.multivalue = multivalue;
		return attribute;
	}
}
