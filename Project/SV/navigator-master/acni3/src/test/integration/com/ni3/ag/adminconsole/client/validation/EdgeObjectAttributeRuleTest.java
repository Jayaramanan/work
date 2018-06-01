/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

import junit.framework.TestCase;

public class EdgeObjectAttributeRuleTest extends TestCase{
	String[] names = new String[] { "Cmnt", "Directed", "Strength", "InPath", "ConnectionType", "cmnt", "directed",
	        "strength", "inpath", "connectiontype" };

	public void testPerformCheck(){
		List<ObjectDefinition> schemas = generateSchemas();
		ACValidationRule rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("edgeObjectAttributeRule");
		rule.performCheck();
		for (ObjectDefinition schema : schemas){
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				for (ObjectAttribute oa : od.getObjectAttributes()){
					assertEquals(oa.getInTable(), "CIS_EDGES");
				}
			}
		}
	}

	private List<ObjectDefinition> generateSchemas(){
		ArrayList<ObjectDefinition> schemas = new ArrayList<ObjectDefinition>();
		for (int i = 0; i < 3; i++){
			ObjectDefinition schema = new ObjectDefinition();
			schema.setId(i + 1);
			schema.setObjectDefinitions(generateObjectDefinitions(schema, 10));
			schemas.add(schema);
		}
		return schemas;
	}

	private List<ObjectDefinition> generateObjectDefinitions(ObjectDefinition schema, int cc){
		ArrayList<ObjectDefinition> objs = new ArrayList<ObjectDefinition>();
		for (int i = 0; i < cc; i++){
			ObjectDefinition od = new ObjectDefinition();
			ObjectType ot = new ObjectType();
			ot.setName("Edge");
			od.setObjectType(ot);
			od.setObjectAttributes(generateObjectAttributes(od));
			objs.add(od);
		}
		return objs;
	}

	private List<ObjectAttribute> generateObjectAttributes(ObjectDefinition od){
		ArrayList<ObjectAttribute> oas = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < names.length; i++){
			ObjectAttribute oa = new ObjectAttribute();
			oa.setName(names[i]);
			oas.add(oa);
		}
		return oas;
	}

}
