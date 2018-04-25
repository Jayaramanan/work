/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;

public interface ObjectGroupDAO{
	void deleteGroupsByObject(ObjectDefinition object);

	void updateObjectGroups(List<ObjectGroup> objectGroups);

	void deleteGroupsByGroup(Group g);

	void updateObjectGroup(ObjectGroup objectGroup);

	List<ObjectGroup> getByGroup(Group group);

	ObjectGroup getObjectGroup(ObjectDefinition od, Group group);

}
