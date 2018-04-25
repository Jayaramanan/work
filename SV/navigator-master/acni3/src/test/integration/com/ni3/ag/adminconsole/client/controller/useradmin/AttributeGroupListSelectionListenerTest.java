/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.util.ArrayList;
import java.util.List;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.UserAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class AttributeGroupListSelectionListenerTest extends ACTestCase{
	UserAdminController controller;
	UserAdminView view;
	UserAdminModel model;

	List<Group> groups;
	List<PredefinedAttribute> attributes;
	PredefinedAttribute attr1;
	PredefinedAttribute attr2;
	Group group1;

	@Override
	protected void setUp() throws Exception{
		ACMain.ScreenWidth = 500.;
		ACMain.ScreenHeight = 500.;
		controller = (UserAdminController) ACSpringFactory.getInstance().getBean("userAdminController");
		view = controller.getView();
		model = controller.getModel();

		group1 = getGroup("Group1");
		Group group2 = getGroup("Group2");
		groups = new ArrayList<Group>();
		groups.add(group1);
		groups.add(group2);

		attr1 = new PredefinedAttribute();
		attr1.setLabel("pa1");
		attr1.setId(1);
		attr2 = new PredefinedAttribute();
		attr2.setLabel("pa2");
		attr2.setId(2);
		attributes = new ArrayList<PredefinedAttribute>();
		attributes.add(attr1);
		attributes.add(attr2);

		model.setCurrentGroup(group1);
		model.setGroups(groups);
	}

	public void testGetModelData(){
		GroupPrefilter pAttrGroup = new GroupPrefilter(group1, attr1);
		pAttrGroup.setEmpty(false);
		group1.setPredefAttributeGroups(new ArrayList<GroupPrefilter>());
		group1.getPredefAttributeGroups().add(pAttrGroup);

		AttributeGroupListSelectionListener ls = new AttributeGroupListSelectionListener(controller);
		List<GroupPrefilter> result = ls.getModelData(attributes);
		assertEquals(2, result.size());
		assertSame(pAttrGroup, result.get(0));
		assertSame(attr1, result.get(0).getPredefinedAttribute());
		assertSame(attr2, result.get(1).getPredefinedAttribute());
		assertFalse(result.get(0).isEmpty());
		assertTrue(result.get(1).isEmpty());
	}

	public void testGetModelDataNoPAttrGroup(){
		AttributeGroupListSelectionListener ls = new AttributeGroupListSelectionListener(controller);
		List<GroupPrefilter> result = ls.getModelData(attributes);
		assertEquals(2, result.size());
		assertSame(attr1, result.get(0).getPredefinedAttribute());
		assertSame(attr2, result.get(1).getPredefinedAttribute());
		assertTrue(result.get(0).isEmpty());
		assertTrue(result.get(1).isEmpty());
	}

	public Group getGroup(String name){
		Group group1 = new Group();
		group1.setName(name);
		return group1;
	}
}
