/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public interface ObjectAttributeDAO{
	public void saveOrUpdate(ObjectAttribute oa);

	public ObjectAttribute merge(ObjectAttribute oa);

	ObjectAttribute getObjectAttribute(int id);

	public ObjectAttribute getObjectAttributeByName(String colName, Integer objectId);

	public List<ObjectAttribute> getPredefinedObjectAttributes(ObjectDefinition object);

	public void saveOrUpdateAll(List<ObjectAttribute> oaList);

	public List<ObjectAttribute> getObjectAttributesWithFormulas(ObjectDefinition od);

}
