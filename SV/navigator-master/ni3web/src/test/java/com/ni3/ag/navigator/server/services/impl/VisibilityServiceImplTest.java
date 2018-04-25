/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;

import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.shared.domain.DataType;
import junit.framework.TestCase;

public class VisibilityServiceImplTest extends TestCase{

	public void testgetSchemaWithPrivileges(){
		VisibilityServiceImpl service = new VisibilityServiceImpl();
		Schema expected = generateExpectedSchema();
		Schema initialSchema = generateInitialSchema();
		Schema result = service.getSchemaWithPrivileges(initialSchema, 1);
		testEqualSchemas(expected, result);
	}

	private Schema generateInitialSchema(){
		Schema schema = new Schema();
		schema.setId(1);
		schema.setDefinitions(new ArrayList<ObjectDefinition>());
		for (int i = 1; i <= 10; i++){
			schema.getDefinitions().add(generateInitialDefinition(i));
		}
		return schema;
	}

	private ObjectDefinition generateInitialDefinition(int id){
		ObjectDefinition od = new ObjectDefinition();
		od.setId(id);
		od.setObjectPermissions(new ArrayList<ObjectDefinitionGroup>());
		for (int idd : new int[]{1, 2, 3}){
			ObjectDefinitionGroup odg = new ObjectDefinitionGroup();
			odg.setObject(od);
			odg.setGroupId(idd);
			odg.setCanRead(id % 2 == 1);
			od.getObjectPermissions().add(odg);
		}
		od.setAttributes(new ArrayList<Attribute>());
		for(int i = 1; i <= 10; i++){
			od.getAttributes().add(generateInitialAttribute(i, od));
		}
		return od;
	}

	private Attribute generateInitialAttribute(int id, ObjectDefinition od){
		Attribute a = new Attribute();
		a.setPredefined_(1);
		a.setValues(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setId(1);
		a.getValues().add(pa);
		a.setEntity(od);
		a.setId(id);
		a.setDatatype(DataType.INT);
		a.setAttributeGroups(new ArrayList<AttributeGroup>());
		for(int idd : new int[]{1, 2, 3}){
			AttributeGroup ag = new AttributeGroup();
			ag.setAttribute(a);
			ag.setGroupId(idd);
			ag.setCanRead(id % 2 == 1);
			a.getAttributeGroups().add(ag);
		}
		return a;
	}

	private Schema generateExpectedSchema(){
		Schema schema = new Schema();
		schema.setId(1);
		schema.setDefinitions(new ArrayList<ObjectDefinition>());
		for (int i = 1; i <= 10; i += 2){
			schema.getDefinitions().add(generateExpectedDefinition(i));
		}
		return schema;
	}

	private ObjectDefinition generateExpectedDefinition(int i){
		ObjectDefinition od = new ObjectDefinition();
		od.setId(i);
		for (int id : new int[]{1}){
			od.setObjectPermissions(new ArrayList<ObjectDefinitionGroup>());
			ObjectDefinitionGroup odg = new ObjectDefinitionGroup();
			odg.setObject(od);
			odg.setGroupId(id);
			odg.setCanRead(true);
			od.getObjectPermissions().add(odg);
		}
		od.setAttributes(new ArrayList<Attribute>());
		for(int j = 1; j <= 10; j++){
			od.getAttributes().add(generateExpectedAttribute(j, od));
		}
		return od;
	}

	private Attribute generateExpectedAttribute(int id, ObjectDefinition od){
		Attribute a = new Attribute();
		a.setValues(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setId(1);
		a.getValues().add(pa);
		a.setEntity(od);
		a.setId(id);
		a.setAttributeGroups(new ArrayList<AttributeGroup>());
		for(int idd : new int[]{1}){
			AttributeGroup ag = new AttributeGroup();
			ag.setAttribute(a);
			ag.setGroupId(idd);
			ag.setCanRead(true);
			a.getAttributeGroups().add(ag);
		}
		return a;
	}

	private void testEqualSchemas(Schema expected, Schema result){
		assertEquals(expected, result);
		assertEquals(expected.getDefinitions(), result.getDefinitions());
		for (int i = 0; i < expected.getDefinitions().size(); i++){
			assertEquals(expected.getDefinitions().get(i), result.getDefinitions().get(i));
			assertEquals(expected.getDefinitions().get(i).getAttributes(), result.getDefinitions().get(i).getAttributes());
			for (int j = 0; j < expected.getDefinitions().get(i).getAttributes().size(); j++){
				assertEquals(expected.getDefinitions().get(i).getAttributes().get(j), result.getDefinitions().get(i).getAttributes().get(j));
				assertEquals(expected.getDefinitions().get(i).getAttributes().get(j).getValues(), result.getDefinitions().get(i).getAttributes().get(j).getValues());
				for (int k = 0; k < expected.getDefinitions().get(i).getAttributes().get(j).getValues().size(); k++){
					assertEquals(expected.getDefinitions().get(i).getAttributes().get(j).getValues().get(k), result.getDefinitions().get(i).getAttributes().get(j).getValues().get(k));
				}
			}
		}
	}
}
