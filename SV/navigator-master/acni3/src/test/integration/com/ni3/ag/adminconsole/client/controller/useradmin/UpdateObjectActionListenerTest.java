/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class UpdateObjectActionListenerTest extends ACTestCase{

	List<PredefinedAttribute> attributes;
	PredefinedAttribute attr1;
	PredefinedAttribute attr2;
	
	GroupPrefilter pAttrGroup1;
	GroupPrefilter pAttrGroup2;
	GroupPrefilter pAttrGroup3;
	
	List<GroupPrefilter> pAttrGroups;
	
	UpdateObjectActionListener ls;

	@Override
	protected void setUp() throws Exception{
		
		Group group1 = getGroup("Group1");
		
		attr1 = new PredefinedAttribute();
		attr1.setLabel("pa1");
		attr1.setId(1);
		attr2 = new PredefinedAttribute();
		attr2.setLabel("pa2");
		attr2.setId(2);
		attributes = new ArrayList<PredefinedAttribute>();
		attributes.add(attr1);
		attributes.add(attr2);
		
		pAttrGroup1 = new GroupPrefilter(group1, attr1);
		pAttrGroup2 = new GroupPrefilter(group1, attr2);
		pAttrGroup3 = new GroupPrefilter(group1, attr2);
		pAttrGroups = new ArrayList<GroupPrefilter>();
		pAttrGroups.add(pAttrGroup1);
		pAttrGroups.add(pAttrGroup2);
		pAttrGroups.add(pAttrGroup3);
		
		ls = new UpdateObjectActionListener(null);
	}

	public void testGetPAttributeGroupsToUpdate(){
		pAttrGroup1.setEmpty(false);
		pAttrGroup2.setEmpty(true);
		pAttrGroup3.setEmpty(true);

		List<GroupPrefilter> result = ls.getPAttributeGroupsToUpdate(pAttrGroups);
		assertEquals(1, result.size());
		assertSame(pAttrGroup1, result.get(0));
		assertFalse(result.get(0).isEmpty());
	}

	public void testGetPAttributeGroupsToDeleteNoneExist(){
		pAttrGroup1.setEmpty(false);
		pAttrGroup2.setEmpty(true);
		pAttrGroup3.setEmpty(true);

		List<GroupPrefilter> result = ls.getPAttributeGroupsToDelete(pAttrGroups);
		assertEquals(0, result.size());
	}
	
	public void testGetPAttributeGroupsToDeleteAllExist(){
		pAttrGroup1.setEmpty(false);
		pAttrGroup1.setNew(false);
		pAttrGroup2.setEmpty(true);
		pAttrGroup2.setNew(false);
		pAttrGroup3.setEmpty(true);
		pAttrGroup3.setNew(false);

		List<GroupPrefilter> result = ls.getPAttributeGroupsToDelete(pAttrGroups);
		assertEquals(2, result.size());
		assertSame(pAttrGroup2, result.get(0));
		assertSame(pAttrGroup3, result.get(1));
		assertTrue(result.get(0).isEmpty());
		assertTrue(result.get(1).isEmpty());
	}

	public Group getGroup(String name){
		Group group1 = new Group();
		group1.setName(name);
		return group1;
	}
}
