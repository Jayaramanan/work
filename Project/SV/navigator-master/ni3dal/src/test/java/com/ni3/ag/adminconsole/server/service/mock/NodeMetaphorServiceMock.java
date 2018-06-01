/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.shared.service.def.NodeMetaphorService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public class NodeMetaphorServiceMock implements NodeMetaphorService{

	public List<Schema> getSchemasWithObjects(){
		return new ArrayList<Schema>();
	}

	public ErrorContainer validateColumns(List<ObjectAttribute> attributes){
		return new ErrorContainerImpl();
	}

	@Override
	public List<Icon> getAllIcons(){
		return null;
	}

	@Override
	public void deleteIcons(List<Icon> iconsToDelete){
	}

	@Override
	public Integer addNewIcon(Icon icon, boolean b) throws ACException{
		return null;
	}

	@Override
	public void updateObject(ObjectDefinition object){

	}

	@Override
	public ObjectDefinition reloadObject(Integer id){
		return null;
	}

}