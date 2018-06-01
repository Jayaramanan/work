/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.services.ExportService;

public class ExportServiceMock implements ExportService{

	@Override
	public List<ObjectAttribute> getAvailableAttributes(Integer odId, Integer groupId){
		return null;
	}

	@Override
	public Integer getGroupId(Integer userId){
		return null;
	}

	@Override
	public List<ObjectDefinition> getObjectDefinitionsByCisObjects(String ids){
		List<ObjectDefinition> odList = new ArrayList<ObjectDefinition>();
		ObjectDefinition od = new ObjectDefinition();
		od.setId(1);
		od.setName("object 1");
		odList.add(od);
		ObjectDefinition od2 = new ObjectDefinition();
		od2.setId(2);
		od2.setName("object 2");
		odList.add(od2);
		return odList;
	}

	@Override
	public Map<Integer, String> getSrcIdMap(List<ObjectDefinition> objectDefinitions){
		return null;
	}

	@Override
	public List<Object[]> getUserData(ObjectDefinition od, List<ObjectAttribute> attributes, String objectIds){
		return null;
	}

	@Override
	public boolean isAvailableObject(Integer odId, Integer groupId){
		return true;
	}

}
