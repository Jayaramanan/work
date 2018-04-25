/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;

public class GroupDAOMock implements GroupDAO{

	@Override
	public Group addGroup(Group group){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteGroup(Group group){
		// TODO Auto-generated method stub

	}

	@Override
	public Group getGroup(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group getGroupByName(String name){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getGroups(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdate(Group group){
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Group group){
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteGroupScope(GroupScope scope){
		// TODO Auto-generated method stub

	}

	@Override
	public void saveOrUpdateAll(List<Group> groups){
		// TODO Auto-generated method stub

	}

}
