/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.util.ArrayList;
import java.util.List;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.UserAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.User;

public class ObjectGroupListSelectionListenerTest extends ACTestCase{
	UserAdminController controller;
	UserAdminView view;
	UserAdminModel model;

	List<Group> groups;
	List<ObjectAttribute> attributes;
	ObjectAttribute attr1;
	ObjectAttribute attr2;
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

		attr1 = new ObjectAttribute(new ObjectDefinition());
		attr2 = new ObjectAttribute(new ObjectDefinition());
		attributes = new ArrayList<ObjectAttribute>();
		attributes.add(attr1);
		attributes.add(attr2);

		model.setCurrentGroup(group1);
		model.setGroups(groups);
		model.setDeletedUsers(new ArrayList<User>());
	}

	public void testGetModelDataOneAttrGroup(){
		AttributeGroup attrGroup = new AttributeGroup();
		attrGroup.setGroup(group1);
		attrGroup.setObjectAttribute(attr2);
		group1.setAttributeGroups(new ArrayList<AttributeGroup>());
		group1.getAttributeGroups().add(attrGroup);
		
		ObjectGroupListSelectionListener ls = new ObjectGroupListSelectionListener(controller);
		List<AttributeGroup> result = ls.getModelData(attributes);
		assertEquals(1, result.size());
		assertSame(attrGroup, result.get(0));
	}
	
	public void testGetModelDataNoAttrGroup(){
		ObjectGroupListSelectionListener ls = new ObjectGroupListSelectionListener(controller);
		List<AttributeGroup> result = ls.getModelData(attributes);
		assertEquals(0, result.size());
	}

	public Group getGroup(String name){
		Group group1 = new Group();
		group1.setName(name);
		group1.setUsers(new ArrayList<User>());
		return group1;
	}

}
