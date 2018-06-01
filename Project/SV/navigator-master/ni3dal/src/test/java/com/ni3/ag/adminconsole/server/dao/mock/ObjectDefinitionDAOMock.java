/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;

public class ObjectDefinitionDAOMock implements ObjectDefinitionDAO{

	ArrayList<ObjectDefinition> definitions;

	public ObjectDefinitionDAOMock(){
		definitions = new ArrayList<ObjectDefinition>();
		definitions.add(getObjectDefinitionObject());
	}

	public List<ObjectDefinition> getObjectDefinitions(){
		return definitions;
	}

	private ObjectDefinition getObjectDefinitionObject(){
		ObjectDefinition objectDefinition = new ObjectDefinition();

		// fill parameters here
		objectDefinition.setId(1);
		objectDefinition.setDescription("desctiption");
		objectDefinition.setName("name");
		objectDefinition.setSchema(null);
		objectDefinition.setSort(2);

		objectDefinition.setObjectType(ObjectType.NODE);

		return objectDefinition;
	}

	public ObjectDefinition save(ObjectDefinition objectDefinition){
		boolean ok = definitions.add(objectDefinition);
		if (ok)
			return objectDefinition;
		return null;
	}

	public ObjectDefinition getObjectDefinition(int id){
		throw new UnsupportedOperationException();
	}

	public ObjectDefinition saveOrUpdate(ObjectDefinition objectDefinition){
		return objectDefinition;
	}

	public void deleteObject(ObjectDefinition od){
		throw new UnsupportedOperationException();
	}

	public List<ObjectDefinition> getObjects(){
		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();

		ObjectDefinition obj1 = getObjectDefinitionObject();
		obj1.setObjectAttributes(new ArrayList<ObjectAttribute>());

		ObjectAttribute attr1 = new ObjectAttribute(obj1);
		attr1.setInMetaphor(true);
		attr1.setName("column name 1");
		ObjectAttribute attr2 = new ObjectAttribute(obj1);
		attr2.setInMetaphor(true);
		attr2.setName("column name 2");

		obj1.getObjectAttributes().add(attr1);
		obj1.getObjectAttributes().add(attr2);

		ObjectDefinition obj2 = getObjectDefinitionObject();

		objDefinitions.add(obj1);
		objDefinitions.add(obj2);

		return objDefinitions;
	}

	public ObjectDefinition merge(ObjectDefinition od){
		// TODO Auto-generated method stub
		return null;
	}

	public List<ObjectDefinition> getNodeLikeObjectDefinitions(){
		return new ArrayList<ObjectDefinition>();
	}

	public void updateSchemaName(Integer schemaID, String schemaName){
		// TODO Auto-generated method stub

	}

	public void updateSchemaName(Integer schemaID, ObjectDefinition schema){
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectDefinition getObjectDefinitionByName(String objectName, Integer id){
		for (ObjectDefinition od : definitions){
			if (od.getName().equals(objectName) && od.getSchema().getId().equals(id))
				return od;
		}
		return null;
	}

	public List<ObjectDefinition> createObjectDefinitionsFromXML(){

		Schema od0 = new Schema();
		od0.setName("Test Schema");
		od0.setId(new Integer(0));

		ObjectDefinition od1 = new ObjectDefinition();
		od1.setName("Account");
		od1.setObjectType(ObjectType.fromLabel("Node"));
		od1.setId(new Integer(1));
		od1.setSchema(od0);
		save(od1);

		ObjectDefinition od2 = new ObjectDefinition();
		od2.setName("Test");
		od2.setObjectType(ObjectType.fromLabel("Edge"));
		od2.setId(new Integer(2));
		od2.setSchema(od0);
		save(od2);

		Schema od3 = new Schema();
		od3.setName("Imported Schema");
		od3.setId(new Integer(3));

		ObjectDefinition od4 = new ObjectDefinition();
		od4.setName("Other Other");
		od4.setObjectType(ObjectType.fromLabel("Node"));
		od4.setId(new Integer(4));
		od4.setSchema(od3);
		save(od4);
		return definitions;
	}

	public void createObjectDefinitionsFromExcel(PredefinedAttributeDAOMock predefinedDAO){
		Schema od0 = new Schema();
		od0.setName("Test Schema");
		od0.setId(new Integer(0));

		ObjectDefinition od1 = new ObjectDefinition();
		od1.setName("Account");
		od1.setObjectType(ObjectType.fromLabel("Node"));
		od1.setId(new Integer(1));
		od1.setSchema(od0);
		List<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
		ObjectAttribute oa = new ObjectAttribute(od1);
		oa.setName("col1");
		oa.setLabel("Test Attribute");
		oa.setPredefined(Boolean.TRUE);
		oa.setDataType(DataType.INT);
		attrs.add(oa);
		od1.setObjectAttributes(attrs);
		save(od1);

		ObjectDefinition od2 = new ObjectDefinition();
		od2.setName("Test");
		od2.setObjectType(ObjectType.fromLabel("Edge"));
		od2.setId(new Integer(2));
		od2.setSchema(od0);
		attrs = new ArrayList<ObjectAttribute>();
		oa = new ObjectAttribute(od2);
		oa.setName("ConnectionType");
		oa.setLabel("Connection Type");
		oa.setPredefined(Boolean.TRUE);
		oa.setDataType(DataType.INT);
		List<PredefinedAttribute> predefineds = new ArrayList<PredefinedAttribute>();
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setId(1);
		pa.setObjectAttribute(oa);
		pa.setValue("Organizational");
		pa.setLabel("Organizational");
		predefineds.add(pa);
		predefinedDAO.saveOrUpdate(pa);
		oa.setPredefinedAttributes(predefineds);
		attrs.add(oa);
		od2.setObjectAttributes(attrs);
		save(od2);
	}

	@Override
	public List<ObjectDefinition> getObjectDefinitionsByUser(User u){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectDefinition> getSchemaEdgeLikeObjects(Integer schemaId){
		List<ObjectDefinition> ret = new ArrayList<ObjectDefinition>();
		for (ObjectDefinition od : definitions){
			if (od.isEdge())
				ret.add(od);
		}
		return ret;
	}

	@Override
	public List<ObjectDefinition> getSchemaNodeLikeObjects(Integer schemaId){
		List<ObjectDefinition> ret = new ArrayList<ObjectDefinition>();
		for (ObjectDefinition od : definitions){
			if (od.isNode())
				ret.add(od);
		}
		return ret;
	}

	@Override
	public List<ObjectDefinition> getNodeObjectsWithNotFixedAttributes(){
		return null;
	}

	@Override
	public ObjectDefinition getObjectDefinitionWithInMetaphor(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObjectChartsByObject(ObjectDefinition object){
		// TODO Auto-generated method stub
	}

	@Override
	public void evict(ObjectDefinition od){
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
