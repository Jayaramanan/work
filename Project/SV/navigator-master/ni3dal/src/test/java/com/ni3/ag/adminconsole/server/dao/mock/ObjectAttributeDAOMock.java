/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;

public class ObjectAttributeDAOMock implements ObjectAttributeDAO{

	public static final String[] allowedAttributeNames = new String[] { "col1", "col2", "ConnectionType" };

	@Override
	public ObjectAttribute getObjectAttribute(int id){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setId(id);
		return attr;
	}

	@Override
	public ObjectAttribute getObjectAttributeByName(String colName, Integer objectId){
		boolean allowedName = false;

		for (String name : allowedAttributeNames)
			if (colName.equals(name))
				allowedName = true;
		if (!allowedName)
			return null;

		ObjectDefinition parent = new ObjectDefinition();
		parent.setId(objectId);

		ObjectAttribute attr = new ObjectAttribute();
		attr.setName(colName);
		attr.setObjectDefinition(parent);
		return attr;
	}

	@Override
	public void saveOrUpdate(ObjectAttribute oa){
		// TODO Auto-generated method stub

	}

	@Override
	public List<ObjectAttribute> getPredefinedObjectAttributes(ObjectDefinition object){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateAll(List<ObjectAttribute> oaList){
		// TODO Auto-generated method stub

	}

	@Override
	public List<ObjectAttribute> getObjectAttributesWithFormulas(ObjectDefinition od){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectAttribute merge(ObjectAttribute oa){
		// TODO Auto-generated method stub
		return null;
	}

}
