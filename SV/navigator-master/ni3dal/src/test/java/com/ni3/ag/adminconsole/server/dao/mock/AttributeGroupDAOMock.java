/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.server.dao.AttributeGroupDAO;

public class AttributeGroupDAOMock implements AttributeGroupDAO{

	public List<AttributeGroup> getAttributeGroups(int attributeId){
		return null;
	}

	public void deleteAll(List<AttributeGroup> attrGroups){
		throw new UnsupportedOperationException();
	}

	public void updateAttributeGroups(List<AttributeGroup> attributeGroups){
	}

	@Override
	public void updateAttributeGroup(AttributeGroup attributeGroup){

	}

	@Override
	public List<AttributeGroup> getAttributeGroupsByGroup(Group group){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttributeGroup getAttributeGroup(ObjectAttribute attr, Group group){
		// TODO Auto-generated method stub
		return null;
	}

}
