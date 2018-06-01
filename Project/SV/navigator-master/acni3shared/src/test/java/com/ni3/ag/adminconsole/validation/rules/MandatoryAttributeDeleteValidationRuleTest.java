/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MandatoryAttributeDeleteValidationRuleTest extends TestCase{
	public void testPerformtCheckNodeObject(){

		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.NODE);
		ObjectAttribute oa = new ObjectAttribute(od);

		SchemaAdminModel model = new SchemaAdminModel();
		List<ObjectAttribute> attrsToDelete = new ArrayList<ObjectAttribute>();
		attrsToDelete.add(oa);
		model.setAttributesToDelete(attrsToDelete);
		ACValidationRule rule = new MandatoryAttributeDeleteValidationRule();

		oa.setName(ObjectAttribute.LON_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.LAT_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.SRCID_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.ICONNAME_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());
	}

	public void testPerformCheckNotFixedEdgeAttribute(){
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.EDGE);
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("Test");
		SchemaAdminModel model = new SchemaAdminModel();
		List<ObjectAttribute> attrsToDelete = new ArrayList<ObjectAttribute>();
		attrsToDelete.add(oa);
		model.setAttributesToDelete(attrsToDelete);
		ACValidationRule rule = new MandatoryAttributeDeleteValidationRule();
		rule.performCheck(model);
		assertTrue(rule.getErrorEntries().isEmpty());
	}

	public void testPerformCheckNotFixedNodeAttribute(){
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.NODE);
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("Test");
		SchemaAdminModel model = new SchemaAdminModel();
		List<ObjectAttribute> attrsToDelete = new ArrayList<ObjectAttribute>();
		attrsToDelete.add(oa);
		model.setAttributesToDelete(attrsToDelete);
		ACValidationRule rule = new MandatoryAttributeDeleteValidationRule();
		rule.performCheck(model);
		assertTrue(rule.getErrorEntries().isEmpty());
	}

	public void testPerformCheck(){
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.EDGE);
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName(ObjectAttribute.COMMENT_ATTRIBUTE_NAME);
		SchemaAdminModel model = new SchemaAdminModel();
		List<ObjectAttribute> attrsToDelete = new ArrayList<ObjectAttribute>();
		attrsToDelete.add(oa);
		model.setAttributesToDelete(attrsToDelete);
		ACValidationRule rule = new MandatoryAttributeDeleteValidationRule();
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.DIRECTED_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.STRENGTH_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.INPATH_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.FROM_ID_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.TO_ID_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		oa.setName(ObjectAttribute.SRCID_ATTRIBUTE_NAME);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());
	}
}
