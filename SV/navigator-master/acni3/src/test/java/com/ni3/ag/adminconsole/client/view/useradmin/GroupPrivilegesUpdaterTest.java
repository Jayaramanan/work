/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.client.controller.useradmin.GroupPrivilegesUpdater;
import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;

public class GroupPrivilegesUpdaterTest extends TestCase{

	private GroupPrivilegesUpdater updater;
	private Group group;
	private Schema schema;
	private ObjectDefinition object;
	private ObjectAttribute attribute;
	private PredefinedAttribute predefined;

	@Override
	protected void setUp() throws Exception{
		group = new Group();
		group.setId(1);
		schema = new Schema();
		schema.setSchemaGroups(new ArrayList<SchemaGroup>());
		schema.getSchemaGroups().add(new SchemaGroup(schema, new Group()));
		schema.getSchemaGroups().add(new SchemaGroup(schema, group));
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());

		object = new ObjectDefinition();
		object.setObjectGroups(new ArrayList<ObjectGroup>());
		object.getObjectGroups().add(new ObjectGroup(object, new Group()));
		object.getObjectGroups().add(new ObjectGroup(object, group));
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		schema.getObjectDefinitions().add(object);

		attribute = new ObjectAttribute();
		attribute.setAttributeGroups(new ArrayList<AttributeGroup>());
		attribute.getAttributeGroups().add(new AttributeGroup(attribute, new Group()));
		attribute.getAttributeGroups().add(new AttributeGroup(attribute, group));
		object.getObjectAttributes().add(attribute);

		predefined = new PredefinedAttribute();
		predefined.setPredefAttributeGroups(new ArrayList<GroupPrefilter>());
		predefined.getPredefAttributeGroups().add(new GroupPrefilter(new Group(), predefined));
		predefined.getPredefAttributeGroups().add(new GroupPrefilter(group, predefined));
		List<Schema> schemas = new ArrayList<Schema>();
		schemas.add(schema);

		updater = new GroupPrivilegesUpdater(schemas);
	}

	public void testSetCanReadAttribute(){
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attribute.getPredefinedAttributes().add(predefined);
		updater.setCanReadAttribute(attribute, group, true, false, true);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
		assertTrue(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(1, predefined.getPredefAttributeGroups().size());

		updater.setCanReadAttribute(attribute, group, false, false, true);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(1, predefined.getPredefAttributeGroups().size());
	}

	public void testSetCanReadAttributeForcePredefineds(){
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attribute.getPredefinedAttributes().add(predefined);
		updater.setCanReadAttribute(attribute, group, true, true, true);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
		assertTrue(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(1, predefined.getPredefAttributeGroups().size());

		updater.setCanReadAttribute(attribute, group, false, true, true);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(1, predefined.getPredefAttributeGroups().size());
	}

	public void testSetCanReadAttributeNotExist(){
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attribute.getAttributeGroups().clear();
		updater.setCanReadAttribute(attribute, group, true, false, true);
		assertTrue(attribute.getAttributeGroups().get(0).isCanRead());

		attribute.getAttributeGroups().clear();
		updater.setCanReadAttribute(attribute, group, false, false, true);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
	}

	public void testSetCanReadSchema(){
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attribute.getPredefinedAttributes().add(predefined);

		updater.setCanReadSchema(schema, group, true, false);
		assertTrue(schema.getSchemaGroups().get(1).isCanRead());
		assertFalse(object.getObjectGroups().get(1).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(2, predefined.getPredefAttributeGroups().size());

		updater.setCanReadSchema(schema, group, false, false);
		assertFalse(object.getObjectGroups().get(1).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(2, predefined.getPredefAttributeGroups().size());
	}

	public void testSetCanReadObject(){
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attribute.getPredefinedAttributes().add(predefined);

		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attribute);

		updater.setCanReadObject(object, group, true, false);
		assertTrue(object.getObjectGroups().get(1).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(2, predefined.getPredefAttributeGroups().size());

		updater.setCanReadObject(object, group, false, false);
		assertFalse(object.getObjectGroups().get(1).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(2, predefined.getPredefAttributeGroups().size());
	}

	public void testSetCanReadObjectForceAttributes(){
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attribute.getPredefinedAttributes().add(predefined);

		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attribute);

		updater.setCanReadObject(object, group, true, true);
		assertTrue(object.getObjectGroups().get(1).isCanRead());
		assertTrue(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(1, predefined.getPredefAttributeGroups().size());

		updater.setCanReadObject(object, group, false, true);
		assertFalse(object.getObjectGroups().get(1).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());
		assertEquals(1, predefined.getPredefAttributeGroups().size());
	}

	public void testSetCanReadObjectNotExist(){
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		updater.setCanReadObject(object, group, true, false);
		assertFalse(object.getObjectGroups().get(0).isCanRead());
		assertTrue(object.getObjectGroups().get(1).isCanRead());

		updater.setCanReadObject(object, group, false, false);
		assertFalse(object.getObjectGroups().get(0).isCanRead());
		assertFalse(object.getObjectGroups().get(1).isCanRead());
	}

	public void testSetCanReadPredefined(){
		updater.setCanReadPredefined(predefined, group, true);
		assertEquals(1, predefined.getPredefAttributeGroups().size());
		assertNotSame(group, predefined.getPredefAttributeGroups().get(0).getGroup());

		updater.setCanReadPredefined(predefined, group, false);
		assertEquals(2, predefined.getPredefAttributeGroups().size());
		assertSame(group, predefined.getPredefAttributeGroups().get(1).getGroup());
	}

	public void testResetLockedToUnlockedState(){
		attribute.getAttributeGroups().get(1).setCanRead(true);
		attribute.getAttributeGroups().get(1).setEditingOption(EditingOption.ReadOnly);

		assertNull(attribute.getAttributeGroups().get(1).getEditingOptionLocked());

		updater.resetLockedToUnlockedState(group);

		assertEquals(EditingOption.ReadOnly, attribute.getAttributeGroups().get(1).getEditingOptionLocked());

		attribute.getAttributeGroups().get(1).setCanRead(false);
		attribute.getAttributeGroups().get(1).setEditingOption(EditingOption.NotVisible);

		updater.resetLockedToUnlockedState(group);

		assertEquals(EditingOption.NotVisible, attribute.getAttributeGroups().get(1).getEditingOptionLocked());
	}
}
