/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;

public class AttributeEditControllerTest extends TestCase{
	private AttributeEditController controller;
	private AttributeEditModel model;
	private ObjectDefinition object;

	@Override
	protected void setUp() throws Exception{
		controller = (AttributeEditController) ACSpringFactory.getInstance().getBean("attributeEditController");
		model = (AttributeEditModel) controller.getModel();
		object = new ObjectDefinition();
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute oa = new ObjectAttribute(object);
		oa.setSort(1);
		oa.setMatrixSort(1);
		oa.setSearchSort(1);
		oa.setLabelSort(1);
		oa.setFilterSort(1);
		object.getObjectAttributes().add(oa);

		oa = new ObjectAttribute(object);
		oa.setSort(2);
		oa.setMatrixSort(2);
		oa.setSearchSort(2);
		oa.setLabelSort(2);
		oa.setFilterSort(2);
		object.getObjectAttributes().add(oa);
		model.setCurrentObjectDefinition(object);
	}

	public void testHasDifferentSortsDiffMatrixSort(){
		object.getObjectAttributes().get(1).setMatrixSort(3);
		assertTrue(controller.hasDifferentSorts(object.getObjectAttributes()));
	}

	public void testHasDifferentSortsDiffSearchSort(){
		object.getObjectAttributes().get(1).setSearchSort(3);
		assertTrue(controller.hasDifferentSorts(object.getObjectAttributes()));
	}

	public void testHasDifferentSortsDiffLabelSort(){
		object.getObjectAttributes().get(1).setLabelSort(3);
		assertTrue(controller.hasDifferentSorts(object.getObjectAttributes()));
	}

	public void testHasDifferentSortsDiffFilterSort(){
		object.getObjectAttributes().get(1).setFilterSort(3);
		assertTrue(controller.hasDifferentSorts(object.getObjectAttributes()));
	}

	public void testHasDifferentSorts(){
		assertFalse(controller.hasDifferentSorts(object.getObjectAttributes()));
	}

	public void testResetSorts(){
		ObjectAttribute attr = object.getObjectAttributes().get(1);
		attr.setSort(5);
		controller.resetSorts();
		assertEquals(5, attr.getSort(), 0);
		assertEquals(5, attr.getMatrixSort(), 0);
		assertEquals(5, attr.getSearchSort(), 0);
		assertEquals(5, attr.getLabelSort(), 0);
		assertEquals(5, attr.getFilterSort(), 0);

		attr = object.getObjectAttributes().get(0);
		assertEquals(1, attr.getSort(), 0);
		assertEquals(1, attr.getMatrixSort(), 0);
		assertEquals(1, attr.getSearchSort(), 0);
		assertEquals(1, attr.getLabelSort(), 0);
		assertEquals(1, attr.getFilterSort(), 0);
	}

	public void testResetSorts2(){
		ObjectAttribute attr = object.getObjectAttributes().get(1);
		attr.setMatrixSort(5);
		attr.setSearchSort(6);
		attr.setLabelSort(7);
		attr.setFilterSort(8);
		controller.resetSorts(object.getObjectAttributes());
		assertEquals(2, attr.getSort(), 0);
		assertEquals(2, attr.getMatrixSort(), 0);
		assertEquals(2, attr.getSearchSort(), 0);
		assertEquals(2, attr.getLabelSort(), 0);
		assertEquals(2, attr.getFilterSort(), 0);

		attr = object.getObjectAttributes().get(0);
		assertEquals(1, attr.getSort(), 0);
		assertEquals(1, attr.getMatrixSort(), 0);
		assertEquals(1, attr.getSearchSort(), 0);
		assertEquals(1, attr.getLabelSort(), 0);
		assertEquals(1, attr.getFilterSort(), 0);
	}
}
