/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSequenceState;
import com.ni3.ag.adminconsole.validation.ACException;

public interface UserAdminService{
	List<Group> getGroups();

	void updateUsers(List<User> users);

	void deleteUsers(List<User> users);

	void deleteGroup(Group group);

	Group addGroup(Group group);

	void updateGroup(Group group);

	Group copyGroup(Group sourceGroup, String newName) throws ACException;

	List<User> getUnassignedUsers();

	List<ObjectDefinition> getObjects();

	void updateObjectGroups(List<ObjectGroup> objectGroups);

	void updateAttributeGroups(List<AttributeGroup> attributeGroups);

	List<ObjectConnection> getConnections();

	User getUser(Integer id);

	Integer addUser(User user);

	Group reloadGroup(Integer id);

	List<Group> reloadGroupUsers(List<Group> groups);

	Group getGroup(Integer id);

	List<ObjectDefinition> getObjectsByUser(User u);

	List<Schema> getSchemasByUser(User u);

	void deleteGroupScope(GroupScope scope);

	User resetPassword(User userToReset) throws ACException;

	Map<Integer, List<UserSequenceState>> getUserRanges() throws ACException;

	Integer getDeltasByUser(User u);

	Integer getOutDeltasByUser(User u);

	Integer getOfflineJobsByUser(User u);

	List<Schema> getSchemas();

	void updateSchemas(List<Schema> schemas);

	Integer getMaximumUsersWithEtlAccess();

	String getPasswordFormat(int userId);

	boolean getConfigLockedObjectsSetting(int groupId);

	void updateConfigLockedObjectsSetting(Integer id, boolean showLockedColumns);
}
