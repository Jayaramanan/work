/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.Collection;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public interface PredefinedAttributeDAO{
	List<PredefinedAttribute> getPredefinedAttributes(ObjectAttribute attribute);

	List<PredefinedAttribute> getPredefinedAttributes(Integer attributeId);

	Collection<PredefinedAttribute> saveOrUpdateAll(Collection<PredefinedAttribute> attrs);

	List<PredefinedAttribute> getPredefinedAttributes();

	PredefinedAttribute saveOrUpdate(PredefinedAttribute attr);

	PredefinedAttribute getById(Integer id);

	PredefinedAttribute getPredefinedAttributeByValue(ObjectAttribute attr, String value);

	Object merge(Object entity);

	PredefinedAttribute getPredefinedAttributeByLabel(ObjectAttribute objectAttribute, String label);

	boolean isUsedInUserTable(PredefinedAttribute pa);

    void updateValuesInUserTable(PredefinedAttribute pa, Integer newValue);
}
