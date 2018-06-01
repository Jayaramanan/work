/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public interface AttributeGroupDAO{
	public List<AttributeGroup> getAttributeGroups(int attributeId);

	public void deleteAll(List<AttributeGroup> attrGroups);

	public void updateAttributeGroups(List<AttributeGroup> attributeGroups);

	public void updateAttributeGroup(AttributeGroup attributeGroup);

	public List<AttributeGroup> getAttributeGroupsByGroup(Group group);

	public AttributeGroup getAttributeGroup(ObjectAttribute attr, Group group);
}
