/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.Collection;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.validation.ACException;

public interface PredefinedAttributeService{

	void updateObjectAttribute(ObjectAttribute oa, Collection<PredefinedAttribute> nestedPredefineds,
	        List<Object[]> deletedPredefineds) throws ACException;

	ObjectAttribute reloadAttribute(Integer id);

	ErrorContainer checkReferencedConnectionTypes(List<PredefinedAttribute> deletedPredefinedAttributes);

	void calculateFormulaValue(Integer attributeId) throws ACException;

	List<Schema> getFullSchemas();

	List<PredefinedAttribute> getAllPredefinedAttributes(ObjectDefinition od);

	ErrorContainer checkReferencesFromMetaphors(PredefinedAttribute pa);

	boolean isUsedInUserTable(PredefinedAttribute pa);

}
