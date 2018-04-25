/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Context;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.validation.ACException;

public interface AttributeEditService{
	List<Schema> getSchemas();

	ObjectDefinition reloadObjectDefinition(Integer id) throws ACException;

	void updateObjectDefinition(ObjectDefinition object, List<Context> list, boolean updateLiveData) throws ACException;

}
