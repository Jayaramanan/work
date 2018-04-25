/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

import junit.framework.TestCase;

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

public class GroupPrivilegesTableModelTest extends TestCase{
	private GroupPrivilegesTableModel model;
	private Group group;
	private Schema schema;
	private ObjectDefinition object;
	private ObjectAttribute attribute;
	private PredefinedAttribute predefined;

	@Override
	protected void setUp() throws Exception{

		group = new Group();
		group.setId(1);
		object = new ObjectDefinition();
		object.setObjectGroups(new ArrayList<ObjectGroup>());
		object.getObjectGroups().add(new ObjectGroup(object, new Group()));
		object.getObjectGroups().add(new ObjectGroup(object, group));
		attribute = new ObjectAttribute();
		attribute.setAttributeGroups(new ArrayList<AttributeGroup>());
		attribute.getAttributeGroups().add(new AttributeGroup(attribute, new Group()));
		AttributeGroup ag = new AttributeGroup(attribute, group);
		attribute.getAttributeGroups().add(ag);

		predefined = new PredefinedAttribute();
		predefined.setPredefAttributeGroups(new ArrayList<GroupPrefilter>());
		predefined.getPredefAttributeGroups().add(new GroupPrefilter(new Group(), predefined));
		predefined.getPredefAttributeGroups().add(new GroupPrefilter(group, predefined));

		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attribute);

		schema = new Schema();
		schema.setSchemaGroups(new ArrayList<SchemaGroup>());
		schema.getSchemaGroups().add(new SchemaGroup(schema, new Group()));
		schema.getSchemaGroups().add(new SchemaGroup(schema, group));

		object.setSchema(schema);
		List<Schema> schemas = new ArrayList<Schema>();
		schemas.add(schema);

		model = new GroupPrivilegesTableModel(new JTree(), group, schemas, true);
	}

	public void testIsCanCreate(){
		assertFalse(model.isCanCreate(predefined));
		assertFalse(model.isCanCreate(attribute));
		assertFalse(model.isCanCreate(schema));

		assertFalse(model.isCanCreate(object));

		ObjectGroup oug = object.getObjectGroups().get(0);
		oug.setCanCreate(true);
		assertFalse(model.isCanCreate(object));

		oug = object.getObjectGroups().get(1);
		oug.setCanCreate(true);
		assertTrue(model.isCanCreate(object));
	}

	public void testIsCanDelete(){
		assertFalse(model.isCanDelete(predefined));
		assertFalse(model.isCanDelete(attribute));
		assertFalse(model.isCanDelete(schema));

		assertFalse(model.isCanDelete(object));

		ObjectGroup oug = object.getObjectGroups().get(0);
		oug.setCanDelete(true);
		assertFalse(model.isCanDelete(object));

		oug = object.getObjectGroups().get(1);
		oug.setCanDelete(true);
		assertTrue(model.isCanDelete(object));
	}

	public void testIsCanRead(){
		assertFalse(model.isCanRead(schema));

		// object
		assertFalse(model.isCanRead(object));
		ObjectGroup oug = object.getObjectGroups().get(0);
		oug.setCanRead(true);
		assertFalse(model.isCanRead(object));

		oug = object.getObjectGroups().get(1);
		oug.setCanRead(true);
		assertTrue(model.isCanRead(object));

		// attribute
		assertFalse(model.isCanRead(attribute));
		AttributeGroup ag = attribute.getAttributeGroups().get(0);
		ag.setCanRead(true);
		assertFalse(model.isCanRead(attribute));

		ag = attribute.getAttributeGroups().get(1);
		ag.setCanRead(true);
		assertTrue(model.isCanRead(attribute));

		// predefined
		assertFalse(model.isCanRead(predefined));
		predefined.getPredefAttributeGroups().remove(0);
		assertFalse(model.isCanRead(predefined));
		predefined.getPredefAttributeGroups().remove(0);
		assertTrue(model.isCanRead(predefined));
	}

	public void testIsCanReadAttribute(){
		assertFalse(model.isCanReadAttribute(attribute));
		AttributeGroup ag = attribute.getAttributeGroups().get(0);
		ag.setCanRead(true);
		assertFalse(model.isCanReadAttribute(attribute));

		ag = attribute.getAttributeGroups().get(1);
		ag.setCanRead(true);
		assertTrue(model.isCanReadAttribute(attribute));
	}

	public void testIsCanReadObject(){
		assertFalse(model.isCanReadObject(object));
		ObjectGroup oug = object.getObjectGroups().get(0);
		oug.setCanRead(true);
		assertFalse(model.isCanReadObject(object));

		oug = object.getObjectGroups().get(1);
		oug.setCanRead(true);
		assertTrue(model.isCanReadObject(object));
	}

	public void testIsCanReadPredefined(){
		assertFalse(model.isCanReadPredefined(predefined));
		predefined.getPredefAttributeGroups().remove(0);
		assertFalse(model.isCanReadPredefined(predefined));
		predefined.getPredefAttributeGroups().remove(0);
		assertTrue(model.isCanReadPredefined(predefined));
	}

	public void testGetCanUpdate(){
		assertNull(model.getCanUpdate(schema));
		assertNull(model.getCanUpdate(predefined));

		// object
		assertEquals(Boolean.FALSE, model.getCanUpdate(object));
		ObjectGroup oug = object.getObjectGroups().get(0);
		oug.setCanUpdate(true);
		assertEquals(Boolean.FALSE, model.getCanUpdate(object));

		oug = object.getObjectGroups().get(1);
		oug.setCanUpdate(true);
		assertEquals(Boolean.TRUE, model.getCanUpdate(object));

		// attribute
		assertEquals(EditingOption.NotVisible, model.getCanUpdate(attribute));
		AttributeGroup ag = attribute.getAttributeGroups().get(0);
		ag.setEditingOption(EditingOption.Editable);
		assertEquals(EditingOption.NotVisible, model.getCanUpdate(attribute));

		ag = attribute.getAttributeGroups().get(1);
		ag.setEditingOption(EditingOption.Editable);
		assertEquals(EditingOption.Editable, model.getCanUpdate(attribute));
	}

	public void testIsCanUpdateAttribute(){
		assertEquals(EditingOption.NotVisible, model.getCanUpdateAttribute(attribute));
		AttributeGroup ag = attribute.getAttributeGroups().get(0);
		ag.setEditingOption(EditingOption.Mandatory);
		assertEquals(EditingOption.NotVisible, model.getCanUpdateAttribute(attribute));

		ag = attribute.getAttributeGroups().get(1);
		ag.setEditingOption(EditingOption.Mandatory);
		assertEquals(EditingOption.Mandatory, model.getCanUpdateAttribute(attribute));
	}

	public void testIsCanUpdateObject(){
		assertFalse(model.isCanUpdateObject(object));
		ObjectGroup oug = object.getObjectGroups().get(0);
		oug.setCanUpdate(true);
		assertFalse(model.isCanUpdateObject(object));

		oug = object.getObjectGroups().get(1);
		oug.setCanUpdate(true);
		assertTrue(model.isCanUpdateObject(object));
	}

	public void testIsCellEditableSchema(){
		assertTrue(model.isCellEditable(schema, 0));
		assertTrue(model.isCellEditable(schema, 1));

		assertFalse(model.isCellEditable(schema, 2));
		assertFalse(model.isCellEditable(schema, 3));
		assertFalse(model.isCellEditable(schema, 4));
		assertFalse(model.isCellEditable(schema, 5));
	}

	public void testIsCellEditableObject(){
		assertTrue(model.isCellEditable(schema, 0));

		assertTrue(model.isCellEditable(new Object(), 0));
		assertTrue(model.isCellEditable(object, 0));

		assertFalse(model.isCellEditable(object, 1));

		schema.getSchemaGroups().get(1).setCanRead(true);

		assertTrue(model.isCellEditable(object, 1));

		assertFalse(model.isCellEditable(object, 2));
		assertFalse(model.isCellEditable(object, 3));
		assertFalse(model.isCellEditable(object, 4));
		assertFalse(model.isCellEditable(object, 5));

		object.getObjectGroups().get(1).setCanRead(true);

		assertTrue(model.isCellEditable(object, 2));
		assertTrue(model.isCellEditable(object, 3));
		assertTrue(model.isCellEditable(object, 4));
	}

	public void testIsCellEditableAttribute(){
		attribute.setObjectDefinition(object);

		for (int i = 1; i <= 7; i++){
			assertFalse(model.isCellEditable(attribute, i));
		}
		object.getObjectGroups().get(1).setCanRead(true);

		assertTrue(model.isCellEditable(attribute, 1));

		assertFalse(model.isCellEditable(attribute, 2));
		assertFalse(model.isCellEditable(attribute, 3));
		assertFalse(model.isCellEditable(attribute, 4));
		assertFalse(model.isCellEditable(attribute, 5));

		attribute.getAttributeGroups().get(1).setCanRead(true);
		assertFalse(model.isCellEditable(attribute, 2));

		object.getObjectGroups().get(1).setCanUpdate(true);
		assertTrue(model.isCellEditable(attribute, 4));
		assertTrue(model.isCellEditable(attribute, 5));

		attribute.getAttributeGroups().get(1).setEditingOptionLocked(EditingOption.ReadOnly);
		assertTrue(model.isCellEditable(attribute, 5));
	}

	public void testIsCellEditablePredefined(){
		predefined.setObjectAttribute(attribute);
		attribute.setObjectDefinition(object);
		for (int i = 1; i <= 8; i++){
			assertFalse(model.isCellEditable(attribute, i));
		}

		attribute.getAttributeGroups().get(1).setCanRead(true);

		assertTrue(model.isCellEditable(predefined, 1));
		for (int i = 2; i <= 8; i++){
			assertFalse(model.isCellEditable(attribute, i));
		}
	}

	public void testSetCanCreate(){
		model.setCanCreate(object, true);
		assertFalse(object.getObjectGroups().get(0).isCanCreate());
		assertTrue(object.getObjectGroups().get(1).isCanCreate());

		model.setCanCreate(object, false);
		assertFalse(object.getObjectGroups().get(0).isCanCreate());
		assertFalse(object.getObjectGroups().get(1).isCanCreate());
	}

	public void testSetCanDelete(){
		model.setCanDelete(object, true);
		assertFalse(object.getObjectGroups().get(0).isCanDelete());
		assertTrue(object.getObjectGroups().get(1).isCanDelete());

		model.setCanDelete(object, false);
		assertFalse(object.getObjectGroups().get(0).isCanDelete());
		assertFalse(object.getObjectGroups().get(1).isCanDelete());
	}

	public void testSetCanRead(){
		// object
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attribute);
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());

		model.setCanRead(object, true);
		assertFalse(object.getObjectGroups().get(0).isCanRead());
		assertTrue(object.getObjectGroups().get(1).isCanRead());

		model.setCanRead(object, false);
		assertFalse(object.getObjectGroups().get(0).isCanRead());
		assertFalse(object.getObjectGroups().get(1).isCanRead());

		// attribute
		model.setCanRead(attribute, true);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
		assertTrue(attribute.getAttributeGroups().get(1).isCanRead());

		model.setCanRead(attribute, false);
		assertFalse(attribute.getAttributeGroups().get(0).isCanRead());
		assertFalse(attribute.getAttributeGroups().get(1).isCanRead());

		// predefined
		model.setCanRead(predefined, true);
		assertEquals(1, predefined.getPredefAttributeGroups().size());
		assertNotSame(group, predefined.getPredefAttributeGroups().get(0).getGroup());

		model.setCanRead(predefined, false);
		assertEquals(2, predefined.getPredefAttributeGroups().size());
		assertSame(group, predefined.getPredefAttributeGroups().get(1).getGroup());
	}

	public void testSetCanUpdate(){
		// object
		model.setCanUpdate(object, true);
		assertFalse(object.getObjectGroups().get(0).isCanUpdate());
		assertTrue(object.getObjectGroups().get(1).isCanUpdate());

		model.setCanUpdate(object, false);
		assertFalse(object.getObjectGroups().get(0).isCanUpdate());
		assertFalse(object.getObjectGroups().get(1).isCanUpdate());

		// attribute
		model.setCanUpdate(attribute, EditingOption.Editable);
		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertEquals(EditingOption.Editable, attribute.getAttributeGroups().get(1).getEditingOption());

		model.setCanUpdate(attribute, EditingOption.NotVisible);
		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertEquals(EditingOption.NotVisible, attribute.getAttributeGroups().get(1).getEditingOption());
	}

	public void testSetCanUpdateAttribute(){
		model.setCanUpdateAttribute(attribute, EditingOption.Editable);
		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertEquals(EditingOption.Editable, attribute.getAttributeGroups().get(1).getEditingOption());

		model.setCanUpdateAttribute(attribute, EditingOption.NotVisible);
		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertEquals(EditingOption.NotVisible, attribute.getAttributeGroups().get(1).getEditingOption());
	}

	public void testSetCanUpdateObject(){
		model.setCanUpdateObject(object, true);
		assertFalse(object.getObjectGroups().get(0).isCanUpdate());
		assertTrue(object.getObjectGroups().get(1).isCanUpdate());

		model.setCanUpdateObject(object, false);
		assertFalse(object.getObjectGroups().get(0).isCanUpdate());
		assertFalse(object.getObjectGroups().get(1).isCanUpdate());
	}

	public void testSetCanUpdateObjectForceAttributes(){
		model.setCanUpdateObject(object, true);

		assertFalse(object.getObjectGroups().get(0).isCanUpdate());
		assertTrue(object.getObjectGroups().get(1).isCanUpdate());

		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertNull(attribute.getAttributeGroups().get(1).getEditingOption());

		model.setCanUpdateAttribute(attribute, EditingOption.Mandatory);
		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertEquals(EditingOption.Mandatory, attribute.getAttributeGroups().get(1).getEditingOption());

		model.setCanUpdateObject(object, false);
		assertFalse(object.getObjectGroups().get(0).isCanUpdate());
		assertFalse(object.getObjectGroups().get(1).isCanUpdate());
		assertNull(attribute.getAttributeGroups().get(0).getEditingOption());
		assertEquals(EditingOption.NotVisible, attribute.getAttributeGroups().get(1).getEditingOption());
	}

	public void testSetCanUpdateLocked(){

		model.setCanUpdateLocked(attribute, EditingOption.Mandatory);

		for (AttributeGroup ag : attribute.getAttributeGroups())
			if (group.equals(ag.getGroup()))
				assertEquals(EditingOption.Mandatory, ag.getEditingOptionLocked());
	}

	public void testgetCanUpdateLocked(){
		assertEquals(EditingOption.NotVisible, model.getCanUpdateLocked(attribute));

		model.setCanUpdateLocked(attribute, EditingOption.Mandatory);

		assertEquals(EditingOption.Mandatory, model.getCanUpdateLocked(attribute));
	}
}
