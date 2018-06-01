/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSequenceState;
import com.ni3.ag.adminconsole.server.dao.mock.ObjectConnectionDAOMock;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;

public class UserAdminServiceMock implements UserAdminService{

	public List<Group> getGroups(){
		Group group1 = new Group();
		group1.setName("Group 1");
		group1.setUsers(getUsers());
		Group group2 = new Group();
		group2.setName("Group 2");
		List<Group> groups = new ArrayList<Group>();
		groups.add(group1);
		groups.add(group2);
		return groups;
	}

	public List<User> getUsers(){
		User user1 = new User();
		user1.setFirstName("First");
		user1.setLastName("Last");
		user1.setUserName("username");
		user1.setPassword("password");
		List<User> users = new ArrayList<User>();
		users.add(user1);
		return users;
	}

	public void updateUsers(List<User> users){
	}

	public void deleteUsers(List<User> users){
	}

	public Group addGroup(Group group){
		return null;
	}

	public void deleteGroup(Group group){
	}

	public void updateGroup(Group group){
	}

	@Override
	public Group copyGroup(Group sourceGroup, String newName){
		return null;
	}

	public List<User> getUnassignedUsers(){
		User user = new User();
		user.setUserName("Unassigned user");
		user.setPassword("xx");
		List<User> users = new ArrayList<User>();
		users.add(user);
		return users;
	}

	public List<ObjectDefinition> getObjects(){
		ObjectDefinition objectDefinition = new ObjectDefinition();
		objectDefinition.setId(1);
		objectDefinition.setName("name");
		List<ObjectDefinition> objects = new ArrayList<ObjectDefinition>();
		return objects;
	}

	public void updateAttributeGroups(List<AttributeGroup> attributeGroups){
	}

	public void updateObjectGroups(List<ObjectGroup> objectGroups){
	}

	public void deletePAttributeGroups(List<GroupPrefilter> groupsToDelete){
	}

	public void updatePAttributeGroups(List<GroupPrefilter> groupsToUpdate){
	}

	public List<ObjectConnection> getConnections(){
		return new ObjectConnectionDAOMock().getObjectConnections();
	}

	public User getUser(Integer id){
		return null;
	}

	public Integer addUser(User user){
		return null;
	}

	@Override
	public Group reloadGroup(Integer id){
		return null;
	}

	@Override
	public List<Group> reloadGroupUsers(List<Group> groups){
		return null;
	}

	@Override
	public Group getGroup(Integer id){
		return null;
	}

	@Override
	public List<ObjectDefinition> getObjectsByUser(User u){
		return null;
	}

	@Override
	public List<Schema> getSchemasByUser(User u){
		return null;
	}

	@Override
	public void deleteGroupScope(GroupScope scope){

	}

	@Override
	public User resetPassword(User userToReset){
		return userToReset;
	}

	@Override
	public Map<Integer, List<UserSequenceState>> getUserRanges(){
		return null;
	}

	@Override
	public Integer getDeltasByUser(User u){
		return null;
	}

	@Override
	public Integer getOutDeltasByUser(User u){
		return null;
	}

	@Override
	public Integer getOfflineJobsByUser(User u){
		return null;
	}

	@Override
	public List<Schema> getSchemas(){
		return null;
	}

	@Override
	public void updateSchemas(List<Schema> schemas){

	}

	@Override
	public Integer getMaximumUsersWithEtlAccess(){
		return null;
	}

	@Override
	public String getPasswordFormat(int userId){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getConfigLockedObjectsSetting(int groupId){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateConfigLockedObjectsSetting(Integer id, boolean showLockedColumns){
		// TODO Auto-generated method stub

	}

}
