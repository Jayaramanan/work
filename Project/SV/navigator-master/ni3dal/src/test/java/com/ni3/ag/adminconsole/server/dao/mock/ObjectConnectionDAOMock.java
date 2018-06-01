/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;

public class ObjectConnectionDAOMock implements ObjectConnectionDAO{

	public List<ObjectConnection> getObjectConnections(){
		List<ObjectConnection> objectConnections = new ArrayList<ObjectConnection>();
		objectConnections.add(getObjectConnectionObject());
		return objectConnections;
	}

	public void saveOrUpdate(ObjectConnection objectConnection){
		//
	}

	private ObjectConnection getObjectConnectionObject(){
		ObjectConnection objectConnection = new ObjectConnection();

		// fill parameters here
		objectConnection.setId(1);
		objectConnection.setFromObject(getObjectDefinitionObject(1, "from object name"));
		objectConnection.setToObject(getObjectDefinitionObject(2, "to object name"));
		objectConnection.setObject(getObjectDefinitionObject(3, "object name"));

		PredefinedAttribute connType = new PredefinedAttribute();
		connType.setId(1);
		connType.setLabel("Connection Type");
		objectConnection.setConnectionType(connType);

		objectConnection.setLineStyle(LineStyle.FULL);

		objectConnection.setRgb("#00209F");

		LineWeight lineWeight = new LineWeight();
		lineWeight.setId(1);
		lineWeight.setLabel("Line weight");
		lineWeight.setWidth(BigDecimal.ONE);
		objectConnection.setLineWeight(lineWeight);

		return objectConnection;
	}

	private ObjectDefinition getObjectDefinitionObject(int uniq, String name){
		ObjectDefinition objectDefinition = new ObjectDefinition();

		// fill parameters here
		objectDefinition.setId(uniq);
		objectDefinition.setDescription("desctiption");
		objectDefinition.setName(name);
		objectDefinition.setSchema(null);
		objectDefinition.setSort(uniq);

		return objectDefinition;
	}

	public void delete(ObjectConnection objectConnection){
		throw new UnsupportedOperationException();
	}

	public void deleteAll(List<ObjectConnection> objectConnection){
		throw new UnsupportedOperationException();
	}

	public void saveOrUpdateAll(List<ObjectConnection> objectConnection){
		// saved
	}

	public List<ObjectConnection> getObjectConnections(ObjectDefinition object){
		ObjectConnection conn1 = getObjectConnectionObject();
		conn1.setObject(object);
		ObjectConnection conn2 = getObjectConnectionObject();
		conn2.setObject(object);
		ArrayList<ObjectConnection> result = new ArrayList<ObjectConnection>();
		result.add(conn1);
		result.add(conn2);
		return result;
	}

	@Override
	public void saveOrUpdateNoMerge(ObjectConnection objectConnection){
	}

	public void deleteConnectionsByObject(ObjectDefinition objectDefinition){
		throw new UnsupportedOperationException();
	}

	public List<ObjectConnection> getConnectionsByObject(ObjectDefinition objectDefinition){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectConnection> getConnectionsByConnectionType(PredefinedAttribute ct){
		// TODO Auto-generated method stub
		return null;
	}

}
