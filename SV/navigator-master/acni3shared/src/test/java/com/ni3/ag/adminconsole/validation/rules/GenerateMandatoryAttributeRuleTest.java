/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class GenerateMandatoryAttributeRuleTest extends TestCase{
	ObjectDefinition object;
	SchemaAdminModel testModel;
	GenerateMandatoryAttributeRule rule;

	@Override
	protected void setUp() throws Exception{
		object = new ObjectDefinition();
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.setObjectType(ObjectType.EDGE);
		testModel = new SchemaAdminModel();
		testModel.setCurrentObjectDefinition(object);
		rule = new GenerateMandatoryAttributeRule();
	}

	public void testCreateBooleanPredefinedAttributes(){
		ObjectAttribute attr = new ObjectAttribute();
		rule.createBooleanPredefinedAttributes(attr);
		assertEquals(2, attr.getPredefinedAttributes().size());
	}

	public void testCreateAttribute(){
		rule.createAttribute(ObjectAttribute.COMMENT_ATTRIBUTE_NAME, ObjectAttribute.COMMENT_ATTRIBUTE_LABEL, object);
		assertEquals(1, object.getObjectAttributes().size());
		assertEquals(ObjectAttribute.COMMENT_ATTRIBUTE_NAME, object.getObjectAttributes().get(0).getName());
	}

	public void testCreateAttributeInPath(){
		rule.createAttribute(ObjectAttribute.INPATH_ATTRIBUTE_NAME, ObjectAttribute.INPATH_ATTRIBUTE_NAME, object);
		assertEquals(1, object.getObjectAttributes().size());
		assertEquals(ObjectAttribute.INPATH_ATTRIBUTE_NAME, object.getObjectAttributes().get(0).getName());
		assertEquals(2, object.getObjectAttributes().get(0).getPredefinedAttributes().size());
	}

	public void testCreateAttributeConnectionType(){
		rule.createAttribute(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME,
		        ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_LABEL, object);
		assertEquals(1, object.getObjectAttributes().size());
		assertEquals(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME, object.getObjectAttributes().get(0).getName());
		assertEquals(1, object.getObjectAttributes().get(0).getPredefinedAttributes().size());
	}

	public void testPerformCheck(){
		rule.performCheck(testModel);
		assertEquals(8, object.getObjectAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(1).getPredefinedAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(3).getPredefinedAttributes().size());
	}

	public void testPerformCheckExistSomeAttr(){
		ObjectAttribute inPathAttr = new ObjectAttribute();
		inPathAttr.setName("InPath");
		ObjectAttribute connTypeAttr = new ObjectAttribute();
		connTypeAttr.setName("ConnectionType");
		object.getObjectAttributes().add(inPathAttr);
		object.getObjectAttributes().add(connTypeAttr);
		rule.performCheck(testModel);
		assertEquals(8, object.getObjectAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(3).getPredefinedAttributes().size());
	}

	public void testGetDataType(){

		DataType dt = rule.getDataType("Cmnt");
		assertEquals(DataType.TEXT, dt);

		dt = rule.getDataType("Directed");
		assertEquals(DataType.INT, dt);

		dt = rule.getDataType("Strength");
		assertEquals(DataType.INT, dt);

		dt = rule.getDataType("InPath");
		assertEquals(DataType.INT, dt);

		dt = rule.getDataType("ConnectionType");
		assertEquals(DataType.INT, dt);

		dt = rule.getDataType("FromID");
		assertEquals(DataType.INT, dt);

		dt = rule.getDataType("ToID");
		assertEquals(DataType.INT, dt);
	}

	public void testSetNextSorts(){
		ObjectDefinition object = new ObjectDefinition();
		ObjectAttribute newAttribute = new ObjectAttribute();
		rule.setNextSorts(newAttribute, object);
		assertEquals(1, newAttribute.getSort().intValue());
		assertEquals(1, newAttribute.getLabelSort().intValue());
		assertEquals(1, newAttribute.getFilterSort().intValue());
		assertEquals(1, newAttribute.getSearchSort().intValue());

		ObjectAttribute attr1 = new ObjectAttribute();
		attr1.setSort(1);
		attr1.setLabelSort(3);
		attr1.setFilterSort(5);
		attr1.setSearchSort(2);
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attr1);
		newAttribute = new ObjectAttribute();
		rule.setNextSorts(newAttribute, object);
		assertEquals(2, newAttribute.getSort().intValue());
		assertEquals(4, newAttribute.getLabelSort().intValue());
		assertEquals(6, newAttribute.getFilterSort().intValue());
		assertEquals(3, newAttribute.getSearchSort().intValue());

		ObjectAttribute attr2 = new ObjectAttribute();
		ObjectAttribute attr3 = new ObjectAttribute();
		attr2.setSort(5);
		attr2.setLabelSort(5);
		attr2.setFilterSort(5);
		attr2.setSearchSort(5);
		attr3.setSort(3);
		attr3.setLabelSort(3);
		attr3.setFilterSort(3);
		attr3.setSearchSort(3);
		object.getObjectAttributes().add(attr2);
		object.getObjectAttributes().add(attr3);
		newAttribute = new ObjectAttribute();
		rule.setNextSorts(newAttribute, object);
		assertEquals(6, newAttribute.getSort().intValue());
		assertEquals(6, newAttribute.getLabelSort().intValue());
		assertEquals(6, newAttribute.getFilterSort().intValue());
		assertEquals(6, newAttribute.getSearchSort().intValue());
	}

	public void testPerformCheckWithContextAttr(){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setName("attr1");
		attr.setInContext(true);
		object.getObjectAttributes().add(attr);

		rule.performCheck(testModel);
		assertEquals(10, object.getObjectAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(2).getPredefinedAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(4).getPredefinedAttributes().size());
		assertEquals("favoritesid", object.getObjectAttributes().get(9).getName());
	}

}
