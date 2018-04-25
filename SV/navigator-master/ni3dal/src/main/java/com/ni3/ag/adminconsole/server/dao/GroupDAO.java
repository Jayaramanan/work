/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;

public interface GroupDAO{
	List<Group> getGroups();

	Group getGroup(Integer id);

	void deleteGroup(Group group);

	Group addGroup(Group group);

	void saveOrUpdate(Group group);

	void update(Group group);

	Group getGroupByName(String name);

	void deleteGroupScope(GroupScope scope);

	void saveOrUpdateAll(List<Group> groups);
}
