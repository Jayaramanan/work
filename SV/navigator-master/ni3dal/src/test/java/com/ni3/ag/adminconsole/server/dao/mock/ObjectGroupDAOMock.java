/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.server.dao.ObjectGroupDAO;

public class ObjectGroupDAOMock implements ObjectGroupDAO{

	@Override
	public void deleteGroupsByGroup(Group g){
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteGroupsByObject(ObjectDefinition object){
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateObjectGroups(List<ObjectGroup> objectGroups){
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateObjectGroup(ObjectGroup objectGroup){
		//
	}

	@Override
	public List<ObjectGroup> getByGroup(Group group){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectGroup getObjectGroup(ObjectDefinition od, Group group){
		// TODO Auto-generated method stub
		return null;
	}

}
