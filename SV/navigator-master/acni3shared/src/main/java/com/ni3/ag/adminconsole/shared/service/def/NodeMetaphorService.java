/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.validation.ACException;

public interface NodeMetaphorService{
	List<Schema> getSchemasWithObjects();

	List<Icon> getAllIcons();

	Integer addNewIcon(Icon icon, boolean uploadToDocroot) throws ACException;

	void deleteIcons(List<Icon> iconsToDelete) throws ACException;

	void updateObject(ObjectDefinition object);

	ObjectDefinition reloadObject(Integer id);
}
