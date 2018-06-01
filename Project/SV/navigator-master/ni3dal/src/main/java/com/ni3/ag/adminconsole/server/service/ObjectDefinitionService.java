/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface ObjectDefinitionService{

	ObjectDefinition addObjectDefinition(Schema parent, String name, User user) throws ACException;

	void addAttributeGroups(List<ObjectAttribute> attributes, ObjectDefinition parent);

	ObjectDefinition updateObjectDefinition(ObjectDefinition od, boolean ignoreUserData) throws ACException;

	List<ObjectAttribute> getNewAttributes(List<ObjectAttribute> attributes);

}
