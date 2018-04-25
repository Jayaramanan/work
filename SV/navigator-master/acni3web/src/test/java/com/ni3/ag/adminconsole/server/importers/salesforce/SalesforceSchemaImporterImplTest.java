/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.importers.salesforce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.mockito.Mockito;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.GenerateMandatoryAttributeRule;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.DescribeTab;
import com.sforce.soap.partner.DescribeTabSetResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.PicklistEntry;
import com.sforce.ws.ConnectionException;

public class SalesforceSchemaImporterImplTest extends TestCase{

	private SalesforceSchemaImporterImpl importer;
	private List<Group> groups;

	@Override
	protected void setUp() throws Exception{
		importer = new SalesforceSchemaImporterImpl();
		groups = new ArrayList<Group>();
		Group group1 = new Group();
		group1.setId(1);
		Group group2 = new Group();
		group2.setId(2);
		groups.add(group1);
		groups.add(group2);
	}

	public void testAddSchemaPrivileges(){
		Schema schema = new Schema();
		schema.setId(1);
		importer.addSchemaPrivileges(schema, groups);
		assertEquals(2, schema.getSchemaGroups().size());
		final SchemaGroup schemaGroup = schema.getSchemaGroups().get(0);
		assertEquals(groups.get(0), schemaGroup.getGroup());
		assertEquals(schema, schemaGroup.getSchema());
		assertTrue(schemaGroup.isCanRead());

		final SchemaGroup schemaGroup2 = schema.getSchemaGroups().get(1);
		assertEquals(groups.get(1), schemaGroup2.getGroup());
		assertEquals(schema, schemaGroup2.getSchema());
		assertTrue(schemaGroup2.isCanRead());
	}

	public void testAddObjectDefinitionPrivileges(){
		ObjectDefinition od = new ObjectDefinition();
		od.setId(11);

		importer.addObjectDefinitionPrivileges(od, groups);
		assertEquals(2, od.getObjectGroups().size());
		for (int i = 0; i < 2; i++){
			final ObjectGroup objectGroup = od.getObjectGroups().get(i);
			assertEquals(groups.get(i), objectGroup.getGroup());
			assertEquals(od, objectGroup.getObject());
			assertTrue(objectGroup.isCanRead());
			assertTrue(objectGroup.isCanCreate());
			assertTrue(objectGroup.isCanUpdate());
			assertTrue(objectGroup.isCanDelete());
		}
	}

	public void testAddAttributePrivilegesFieldNull(){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setId(111);

		importer.addAttributePrivileges(attr, null, groups);
		for (int i = 0; i < 2; i++){
			final AttributeGroup attributeGroup = attr.getAttributeGroups().get(i);
			assertEquals(groups.get(i), attributeGroup.getGroup());
			assertEquals(attr, attributeGroup.getObjectAttribute());
			assertFalse(attributeGroup.isCanRead());
			assertEquals(EditingOption.NotVisible, attributeGroup.getEditingOption());
		}
	}

	public void testAddAttributePrivilegesFieldMandatory(){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setId(111);

		Field field = Mockito.mock(Field.class);
		Mockito.when(field.isNillable()).thenReturn(false);
		Mockito.when(field.isCalculated()).thenReturn(false);
		Mockito.when(field.isCreateable()).thenReturn(true);
		Mockito.when(field.isDefaultedOnCreate()).thenReturn(false);

		importer.addAttributePrivileges(attr, field, groups);
		for (int i = 0; i < 2; i++){
			final AttributeGroup attributeGroup = attr.getAttributeGroups().get(i);
			assertEquals(groups.get(i), attributeGroup.getGroup());
			assertEquals(attr, attributeGroup.getObjectAttribute());
			assertTrue(attributeGroup.isCanRead());
			assertEquals(EditingOption.Mandatory, attributeGroup.getEditingOption());
		}
	}

	public void testAddAttributePrivilegesFieldEditable(){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setId(111);

		Field field = Mockito.mock(Field.class);
		Mockito.when(field.isNillable()).thenReturn(true);
		Mockito.when(field.isCalculated()).thenReturn(false);
		Mockito.when(field.isDefaultedOnCreate()).thenReturn(false);

		Mockito.when(field.isCreateable()).thenReturn(true);
		Mockito.when(field.isUpdateable()).thenReturn(false);
		importer.addAttributePrivileges(attr, field, groups);
		for (int i = 0; i < 2; i++){
			final AttributeGroup attributeGroup = attr.getAttributeGroups().get(i);
			assertEquals(groups.get(i), attributeGroup.getGroup());
			assertEquals(attr, attributeGroup.getObjectAttribute());
			assertTrue(attributeGroup.isCanRead());
			assertEquals(EditingOption.Editable, attributeGroup.getEditingOption());
		}

		Mockito.when(field.isCreateable()).thenReturn(false);
		Mockito.when(field.isUpdateable()).thenReturn(true);
		importer.addAttributePrivileges(attr, field, groups);
		for (int i = 0; i < 2; i++){
			final AttributeGroup attributeGroup = attr.getAttributeGroups().get(i);
			assertEquals(groups.get(i), attributeGroup.getGroup());
			assertEquals(attr, attributeGroup.getObjectAttribute());
			assertTrue(attributeGroup.isCanRead());
			assertEquals(EditingOption.Editable, attributeGroup.getEditingOption());
		}
	}

	public void testAddAttributePrivilegesFieldNotVisible(){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setId(111);

		Field field = Mockito.mock(Field.class);
		Mockito.when(field.isNillable()).thenReturn(true);
		Mockito.when(field.isCreateable()).thenReturn(false);

		importer.addAttributePrivileges(attr, field, groups);
		for (int i = 0; i < 2; i++){
			final AttributeGroup attributeGroup = attr.getAttributeGroups().get(i);
			assertEquals(groups.get(i), attributeGroup.getGroup());
			assertEquals(attr, attributeGroup.getObjectAttribute());
			assertTrue(attributeGroup.isCanRead());
			assertEquals(EditingOption.NotVisible, attributeGroup.getEditingOption());
		}
	}

	public void testGetDataType(){
		assertEquals(DataType.INT, importer.getDataType(FieldType._int));
		assertEquals(DataType.INT, importer.getDataType(FieldType._boolean)); // TODO should be DataType.BOOL

		assertEquals(DataType.DECIMAL, importer.getDataType(FieldType._double));
		assertEquals(DataType.DECIMAL, importer.getDataType(FieldType.currency));
		assertEquals(DataType.DECIMAL, importer.getDataType(FieldType.percent));

		assertEquals(DataType.DATE, importer.getDataType(FieldType.date));

		assertEquals(DataType.URL, importer.getDataType(FieldType.url));

		assertEquals(DataType.TEXT, importer.getDataType(FieldType.string));
		assertEquals(DataType.TEXT, importer.getDataType(FieldType.datetime));
		assertEquals(DataType.TEXT, importer.getDataType(FieldType.picklist));
		assertEquals(DataType.TEXT, importer.getDataType(FieldType.multipicklist));
		assertEquals(DataType.TEXT, importer.getDataType(FieldType.email));
	}

	public void testIsFieldImportable(){
		assertFalse(importer.isFieldImportable(null));
		Field field = Mockito.mock(Field.class);
		Mockito.when(field.getType()).thenReturn(null);
		assertFalse(importer.isFieldImportable(field));

		Mockito.when(field.getType()).thenReturn(FieldType.reference);
		assertFalse(importer.isFieldImportable(field));

		Mockito.when(field.getType()).thenReturn(FieldType.id);
		assertFalse(importer.isFieldImportable(field));

		Mockito.when(field.getType()).thenReturn(FieldType._int);
		assertTrue(importer.isFieldImportable(field));

		Mockito.when(field.getType()).thenReturn(FieldType.picklist);
		assertTrue(importer.isFieldImportable(field));
	}

	public void testAddPredefinedAttribute(){
		PicklistEntry entry = Mockito.mock(PicklistEntry.class);
		Mockito.when(entry.getValue()).thenReturn("Value");
		Mockito.when(entry.getLabel()).thenReturn("Label");
		Mockito.when(entry.getActive()).thenReturn(true);

		ObjectAttribute oa = new ObjectAttribute();
		oa.setId(111);
		oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		importer.addPredefinedAttribute(oa, entry);
		assertEquals(1, oa.getPredefinedAttributes().size());
		PredefinedAttribute pa = oa.getPredefinedAttributes().get(0);
		assertEquals(oa, pa.getObjectAttribute());
		assertEquals("Value", pa.getValue());
		assertEquals("Label", pa.getLabel());
		assertTrue(pa.getToUse());
		assertEquals(1, pa.getSort().intValue());

		Mockito.when(entry.getValue()).thenReturn("Value2");
		Mockito.when(entry.getLabel()).thenReturn("Label2");
		Mockito.when(entry.getActive()).thenReturn(false);
		importer.addPredefinedAttribute(oa, entry);
		assertEquals(2, oa.getPredefinedAttributes().size());
		PredefinedAttribute pa1 = oa.getPredefinedAttributes().get(0);
		assertEquals(oa, pa1.getObjectAttribute());
		assertEquals("Value", pa1.getValue());
		assertEquals("Label", pa1.getLabel());
		assertTrue(pa1.getToUse());
		assertEquals(1, pa1.getSort().intValue());

		PredefinedAttribute pa2 = oa.getPredefinedAttributes().get(1);
		assertEquals(oa, pa2.getObjectAttribute());
		assertEquals("Value2", pa2.getValue());
		assertEquals("Label2", pa2.getLabel());
		assertFalse(pa2.getToUse());
		assertEquals(2, pa2.getSort().intValue());
	}

	public void testAddObjectAttribute(){
		ObjectDefinition od = new ObjectDefinition();
		od.setId(11);
		od.setTableName("usr_schema_table");
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());

		// int attribute
		Field field = Mockito.mock(Field.class);
		Mockito.when(field.getName()).thenReturn("AttrName");
		Mockito.when(field.getLabel()).thenReturn("AttrLabel");
		Mockito.when(field.getType()).thenReturn(FieldType._int);

		importer.addObjectAttribute(od, field);
		assertEquals(1, od.getObjectAttributes().size());
		ObjectAttribute attr = od.getObjectAttributes().get(0);
		assertEquals("AttrName", attr.getName());
		assertEquals("AttrLabel", attr.getLabel());
		assertEquals("usr_schema_table", attr.getInTable());
		assertEquals(DataType.INT, attr.getDataType());
		assertEquals(1, attr.getSort().intValue());
		assertEquals(1, attr.getLabelSort().intValue());
		assertEquals(1, attr.getSearchSort().intValue());
		assertEquals(1, attr.getMatrixSort().intValue());
		assertEquals(1, attr.getFilterSort().intValue());
		assertTrue(attr.isAggregable());
		assertFalse(attr.getIsMultivalue());
		assertFalse(attr.isPredefined());

		// picklist attribute
		Mockito.when(field.getName()).thenReturn("AttrName2");
		Mockito.when(field.getLabel()).thenReturn("AttrLabel2");
		Mockito.when(field.getType()).thenReturn(FieldType.picklist);
		importer.addObjectAttribute(od, field);
		assertEquals(2, od.getObjectAttributes().size());
		ObjectAttribute attr2 = od.getObjectAttributes().get(1);
		assertEquals("AttrName2", attr2.getName());
		assertEquals("AttrLabel2", attr2.getLabel());
		assertEquals("usr_schema_table", attr2.getInTable());
		assertEquals(DataType.TEXT, attr2.getDataType());
		assertEquals(2, attr2.getSort().intValue());
		assertEquals(2, attr2.getLabelSort().intValue());
		assertEquals(2, attr2.getSearchSort().intValue());
		assertEquals(2, attr2.getMatrixSort().intValue());
		assertEquals(2, attr2.getFilterSort().intValue());
		assertFalse(attr2.isAggregable());
		assertFalse(attr2.getIsMultivalue());
		assertTrue(attr2.isPredefined());

		Mockito.when(field.getType()).thenReturn(FieldType._double);
		importer.addObjectAttribute(od, field);
		assertEquals(3, od.getObjectAttributes().size());
		ObjectAttribute attr3 = od.getObjectAttributes().get(2);
		assertTrue(attr3.isAggregable());
		assertFalse(attr3.getIsMultivalue());
		assertFalse(attr3.isPredefined());

		Mockito.when(field.getType()).thenReturn(FieldType.multipicklist);
		importer.addObjectAttribute(od, field);
		assertEquals(4, od.getObjectAttributes().size());
		ObjectAttribute attr4 = od.getObjectAttributes().get(3);
		assertFalse(attr4.isAggregable());
		assertTrue(attr4.getIsMultivalue());
		assertTrue(attr4.isPredefined());
	}

	public void testAddObjectDefinition(){
		Schema schema = new Schema();
		schema.setId(1);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());

		User user = new User();
		user.setId(1);

		DescribeTab tab = Mockito.mock(DescribeTab.class);
		Mockito.when(tab.getSobjectName()).thenReturn("ObjectName");
		Mockito.when(tab.getLabel()).thenReturn("ObjectLabel");

		importer.addObjectDefinition(schema, tab, user);
		assertEquals(1, schema.getObjectDefinitions().size());
		ObjectDefinition od = schema.getObjectDefinitions().get(0);
		assertEquals(schema, od.getSchema());
		assertEquals("ObjectName", od.getName());
		assertEquals("ObjectLabel", od.getDescription());
		assertEquals(ObjectType.NODE, od.getObjectType());
		assertEquals(1, od.getSort().intValue());
		assertEquals(user, od.getCreatedBy());
		assertNotNull(od.getCreationDate());

		Mockito.when(tab.getSobjectName()).thenReturn("object1!@#$%^&*() - +=test");
		ObjectDefinition od2 = importer.addObjectDefinition(schema, tab, user);
		assertEquals("object1 - test", od2.getName());
		assertEquals(2, schema.getObjectDefinitions().size());
	}

	public void testFillMandatoryAttributes(){
		ObjectDefinition od = new ObjectDefinition();
		od.setId(11);
		od.setObjectType(ObjectType.NODE);
		od.setTableName("usr_schema_table");
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());

		ACValidationRule rule = new GenerateMandatoryAttributeRule();
		importer.setGenerateMandatoryAttributeRule(rule);

		importer.fillMandatoryAttributes(groups, od);
		assertEquals(4, od.getObjectAttributes().size());
		for (int i = 0; i < 4; i++){
			ObjectAttribute attr = od.getObjectAttributes().get(i);
			assertEquals(2, attr.getAttributeGroups().size());
			assertFalse(attr.getAttributeGroups().get(0).isCanRead());
			assertFalse(attr.getAttributeGroups().get(1).isCanRead());
			assertEquals(EditingOption.NotVisible, attr.getAttributeGroups().get(0).getEditingOption());
			assertEquals(EditingOption.NotVisible, attr.getAttributeGroups().get(1).getEditingOption());
		}
	}

	public void testAddSchema(){
		User user = new User();
		Schema schema = importer.addSchema("schema", user);
		assertEquals("schema", schema.getName());
		assertEquals(user, schema.getCreatedBy());
		assertNotNull(schema.getCreationDate());

		schema = importer.addSchema("schema!@#$%^&*() - +=test", user);
		assertEquals("schema - test", schema.getName());
	}

	public void testImportAttributes(){
		ObjectDefinition od = new ObjectDefinition();
		od.setId(11);
		od.setObjectType(ObjectType.NODE);
		od.setTableName("usr_schema_table");
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());

		FieldType[] fieldTypes = { FieldType._int, FieldType.picklist, FieldType.string, FieldType.reference, FieldType.id };
		Field[] fields = new Field[5];
		for (int i = 0; i < fieldTypes.length; i++){
			fields[i] = createField("Field" + (i + 1), fieldTypes[i]);
		}

		DescribeSObjectResult result = Mockito.mock(DescribeSObjectResult.class);
		Mockito.when(result.getFields()).thenReturn(fields);
		PartnerConnection connection = Mockito.mock(PartnerConnection.class);
		try{
			Mockito.when(connection.describeSObject(Mockito.anyString())).thenReturn(result);

			importer.importAttributes(od, groups, connection);
		} catch (ConnectionException e){
			fail();
		}

		DataType[] expectedTypes = { DataType.INT, DataType.TEXT, DataType.TEXT };
		assertEquals(3, od.getObjectAttributes().size());
		for (int i = 0; i < 3; i++){
			ObjectAttribute attr = od.getObjectAttributes().get(i);
			assertEquals("Field" + (i + 1), attr.getName());
			assertEquals("Field" + (i + 1) + " label", attr.getLabel());
			assertEquals(expectedTypes[i], attr.getDataType());
			assertEquals(2, attr.getAttributeGroups().size());
			if (i == 1){
				assertEquals(1, attr.getPredefinedAttributes().size());
			}
		}
	}

	public void testImportSchema(){
		Schema schema = new Schema();
		schema.setId(1);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());

		User user = new User();
		user.setId(1);

		ACValidationRule rule = new GenerateMandatoryAttributeRule();
		importer.setGenerateMandatoryAttributeRule(rule);

		FieldType[] fieldTypes = { FieldType._int, FieldType.picklist, FieldType.string, FieldType.reference, FieldType.id };
		Field[] fields = new Field[5];
		for (int i = 0; i < fieldTypes.length; i++){
			fields[i] = createField("Field" + (i + 1), fieldTypes[i]);
		}

		DescribeTab sfTab1 = Mockito.mock(DescribeTab.class);
		Mockito.when(sfTab1.getSobjectName()).thenReturn("Tab1");

		DescribeTab sfTab2 = Mockito.mock(DescribeTab.class);
		Mockito.when(sfTab2.getSobjectName()).thenReturn("Tab2");
		DescribeTab[] sfTabs = { sfTab1, sfTab2 };

		DescribeTabSetResult sfTabSet = Mockito.mock(DescribeTabSetResult.class);
		Mockito.when(sfTabSet.getTabs()).thenReturn(sfTabs);
		Mockito.when(sfTabSet.getLabel()).thenReturn("TabSet1");

		DescribeTabSetResult sfTabSet2 = Mockito.mock(DescribeTabSetResult.class);
		Mockito.when(sfTabSet2.getTabs()).thenReturn(sfTabs);
		Mockito.when(sfTabSet2.getLabel()).thenReturn("TabSet2");
		DescribeTabSetResult[] sfTabSets = { sfTabSet, sfTabSet2 };

		DescribeSObjectResult sfObject = Mockito.mock(DescribeSObjectResult.class);
		Mockito.when(sfObject.getFields()).thenReturn(fields);
		PartnerConnection connection = Mockito.mock(PartnerConnection.class);
		try{
			Mockito.when(connection.describeTabs()).thenReturn(sfTabSets);
			Mockito.when(connection.describeSObject(Mockito.anyString())).thenReturn(sfObject);

			importer.importSchema(schema, "TabSet2", groups, user, Arrays.asList("Tab1"), connection);
		} catch (ConnectionException e){
			fail();
		}
		assertEquals(1, schema.getObjectDefinitions().size());
		ObjectDefinition od = schema.getObjectDefinitions().get(0);
		assertEquals("Tab1", od.getName());
		assertEquals(7, od.getObjectAttributes().size());
		assertEquals(2, od.getObjectGroups().size());

		DataType[] expectedTypes = { DataType.INT, DataType.TEXT, DataType.TEXT };
		for (int i = 0; i < 3; i++){
			ObjectAttribute attr = od.getObjectAttributes().get(i + 4);
			assertEquals("Field" + (i + 1), attr.getName());
			assertEquals("Field" + (i + 1) + " label", attr.getLabel());
			assertEquals(expectedTypes[i], attr.getDataType());
			assertEquals(i + 5, attr.getSort().intValue());
			assertEquals(2, attr.getAttributeGroups().size());
			if (i == 1){
				assertEquals(1, attr.getPredefinedAttributes().size());
			}
		}
	}

	private Field createField(String name, FieldType type){
		Field field = new Field();
		field.setName(name);
		field.setLabel(name + " label");
		field.setType(type);
		if (type == FieldType.picklist){
			PicklistEntry entry = new PicklistEntry();
			entry.setValue(name + " value");
			entry.setValue(name + " value label");
			field.setPicklistValues(new PicklistEntry[] { entry });
		}
		return field;
	}
}
