/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.util.PrivateAccessor;
import com.ni3.ag.adminconsole.validation.ACException;
import junit.framework.TestCase;

public class UserDataImporterTest extends TestCase{
	private ObjectAttribute attr1;
	private ObjectAttribute attr2;
	private ObjectAttribute[] attributes;

	private DataType dt;
	private UserDataImporter importer;

	@Override
	protected void setUp() throws Exception{
		attr1 = new ObjectAttribute();
		attr1.setName("attr1");
		attr1.setId(1);
		dt = DataType.TEXT;
		attr1.setDataType(dt);

		attr2 = new ObjectAttribute();
		attr2.setName("attr2");
		attr2.setId(2);
		attr2.setDataType(dt);

		attributes = new ObjectAttribute[] { attr1, attr2 };

		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setLabel("label1");
		pa1.setValue("value1");
		pa1.setId(11);
		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setLabel("label2");
		pa2.setValue("value2");
		pa2.setId(22);
		attr1.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attr1.getPredefinedAttributes().add(pa1);
		attr1.getPredefinedAttributes().add(pa2);

		importer = new UserDataImporter();

		super.setUp();
	}

	public void testGetInsertSqlForNodeToUserTable(){
		String tableName = "usr_test";
		attr1.setInTable(tableName);
		attr2.setInTable(tableName);
		attr2.setDataType(DataType.INT);

		BigInteger id = BigInteger.valueOf(222);
		final ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(Arrays.asList(attributes));

		UserDataTable data = new UserDataTable(od, attributes);
		data.addRow(new String[] { "data1", "123" });

		String result = null;
		try{
			importer.prepareData(data, null, null, true);
			result = importer.getInsertSql(tableName, data, 0, id, true, 0, null);
		} catch (ACException e){
			fail("catched exception: " + e);
		}

		String expected = "insert into usr_test (id, attr1, attr2) values (222, 'data1', 123)";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetInsertSqlToCisNodeTable(){
		attr1.setDataType(DataType.INT);
		attr2.setDataType(DataType.INT);
		String tableName = "cis_nodes";
		attr1.setInTable(tableName);
		attr1.setName("lon");
		attr2.setInTable(tableName);
		attr2.setName("lat");

		BigInteger id = BigInteger.valueOf(222);

		UserDataTable data = new UserDataTable(new ObjectDefinition(), attributes);
		data.addRow(new String[] { "-33", "33" });

		String result = null;
		try{
			result = importer.getInsertSql(tableName, data, 0, id, false, 333, "nodeType");
		} catch (ACException e){
			fail("catched exception: " + e);
		}
		String expected = "insert into cis_nodes (id, nodeType, lon, lat) values (222, 333, -33, 33)";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetObjectInsertSql(){

		ObjectDefinition od = new ObjectDefinition();
		od.setId(333);

		BigInteger id = BigInteger.valueOf(222);

		String result = null;
		result = importer.getObjectInsertSQL(id, od, 1);
		String expected = "insert into cis_objects (id, objectType, userid, status, creator) values (222, 333, 1, 0, 1)";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetUpdateSqlForNodeToUserTable(){
		attr1.setDataType(DataType.TEXT);
		String tableName = "usr_test";
		attr1.setInTable(tableName);
		attr2.setInTable(tableName);
		attr2.setDataType(DataType.INT);

		Integer id = 222;
		final ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(Arrays.asList(attributes));
		UserDataTable data = new UserDataTable(od, attributes);
		data.addRow(new String[] { "data1", "123" });

		String result = null;
		try{
			importer.prepareData(data, null, null, true);
			result = importer.getUpdateSql(tableName, data, 0, id);
		} catch (ACException e){
			fail("catched exception: " + e);
		}

		String expected = "update usr_test set attr1 = 'data1', attr2 = 123 where id = 222";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetUpdateSqlToCisNodeTable(){
		attr1.setDataType(DataType.INT);
		attr2.setDataType(DataType.INT);
		String tableName = "cis_nodes";
		attr1.setInTable(tableName);
		attr1.setName("lon");
		attr2.setInTable(tableName);
		attr2.setName("lat");

		Integer id = 222;

		UserDataTable data = new UserDataTable(new ObjectDefinition(), attributes);
		data.addRow(new String[] { "-33", "33" });

		String result = null;
		try{
			result = importer.getUpdateSql(tableName, data, 0, id);
		} catch (ACException e){
			fail("catched exception: " + e);
		}
		String expected = "update cis_nodes set lon = -33, lat = 33 where id = 222";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetInsertSqlForEdgeToUserTable(){
		importer = new UserDataImporter();
		String tableName = "usr_test";
		attr1.setInTable(tableName);
		attr2.setInTable(tableName);
		attr2.setDataType(DataType.INT);

		BigInteger id = BigInteger.valueOf(222);

		final ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(Arrays.asList(attributes));

		UserDataTable data = new UserDataTable(od, attributes);
		data.addRow(new String[] { "data1", "123" });

		String result = null;
		try{
			importer.prepareData(data, null, null, true);
			result = importer.getInsertSql(tableName, data, 0, id, true, 0, null);
		} catch (ACException e){
			fail("catched exception: " + e);
		}

		String expected = "insert into usr_test (id, attr1, attr2) values (222, 'data1', 123)";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetInsertSqlToCisEdgeTable(){
		importer = new UserDataImporter();
		attr1.setDataType(DataType.INT);
		attr2.setDataType(DataType.INT);
		String tableName = "cis_edges";
		attr1.setInTable(tableName);
		attr2.setInTable(tableName);

		BigInteger id = BigInteger.valueOf(222);
		UserDataTable data = new UserDataTable(new ObjectDefinition(), attributes);
		data.addRow(new String[] { "-33", "33" });
		String result = null;
		try{
			result = importer.getInsertSql(tableName, data, 0, id, false, 333, "edgeType");
		} catch (ACException e){
			fail("catched exception: " + e);
		}
		String expected = "insert into cis_edges (id, edgeType, attr1, attr2) values (222, 333, -33, 33)";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetUpdateSqlForEdgeToUserTable(){
		importer = new UserDataImporter();
		String tableName = "usr_test";
		attr1.setInTable(tableName);
		attr2.setInTable(tableName);
		attr2.setDataType(DataType.INT);

		Integer id = 222;
		final ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(Arrays.asList(attributes));
		UserDataTable data = new UserDataTable(od, attributes);
		data.addRow(new String[] { "data1", "123" });

		String result = null;
		try{
			importer.prepareData(data, null, null, true);
			result = importer.getUpdateSql(tableName, data, 0, id);
		} catch (ACException e){
			fail("catched exception: " + e);
		}

		String expected = "update usr_test set attr1 = 'data1', attr2 = 123 where id = 222";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testGetUpdateSqlToCisEdgeTable(){
		importer = new UserDataImporter();
		attr1.setDataType(DataType.INT);
		attr2.setDataType(DataType.INT);
		String tableName = "cis_edges";
		attr1.setInTable(tableName);
		attr2.setInTable(tableName);

		Integer id = 222;
		UserDataTable data = new UserDataTable(new ObjectDefinition(), attributes);
		data.addRow(new String[] { "-33", "33" });
		String result = null;
		try{
			result = importer.getUpdateSql(tableName, data, 0, id);
		} catch (ACException e){
			fail("catched exception: " + e);
		}
		String expected = "update cis_edges set attr1 = -33, attr2 = 33 where id = 222";
		assertEquals(expected.toLowerCase(), result.toLowerCase());
	}

	public void testParseMultivalue(){
		importer = new UserDataImporter();
		String multivalue = importer.parseMultivaluePredefined("label1;label2", attr1);
		assertEquals("'{11}{22}'", multivalue);

		multivalue = importer.parseMultivaluePredefined("label2", attr1);
		assertEquals("'{22}'", multivalue);

		multivalue = importer.parseMultivaluePredefined("not existing label", attr1);
		assertNull(multivalue);
	}

	public void testGetFormattedDate(){
		DateFormat formatter = importer.getDateFormat("dd/MM/yyyy", new SimpleDateFormat(DataType.DISPLAY_DATE_FORMAT));
		assertEquals("20101020", importer.getFormattedDate("20/10/2010", formatter));
		assertEquals("20000102", importer.getFormattedDate("02/01/2000", formatter));
		assertNull(importer.getFormattedDate("20013", formatter));
		assertNull(importer.getFormattedDate(null, formatter));
	}

	public void testParseMultivalueDate(){
		DateFormat formatter = importer.getDateFormat("dd/MM/yyyy", new SimpleDateFormat(DataType.DISPLAY_DATE_FORMAT));
		assertEquals("'{20101020}{20000102}'", importer.parseMultivalueDate("20/10/2010;02/01/2000", formatter));
		assertEquals("'{20000102}'", importer.parseMultivalueDate("02/01/2000", formatter));
		assertNull(importer.parseMultivalueDate("20013", formatter));
		assertNull(importer.parseMultivalueDate(null, formatter));
	}

	public void testPrepareData(){

		attributes = new ObjectAttribute[6];
		attributes[0] = createAttribute("attr1", false, false, DataType.TEXT);
		attributes[1] = createAttribute("attr2", true, false, DataType.TEXT);
		attributes[2] = createAttribute("attr3", true, true, DataType.TEXT);
		attributes[3] = createAttribute("attr4", false, true, DataType.TEXT);
		attributes[4] = createAttribute("attr5", false, false, DataType.DATE, "dd.MM.yyyy");
		attributes[5] = createAttribute("attr6", false, true, DataType.DATE, "yyyy/MM/dd");

		final ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(Arrays.asList(attributes));
		UserDataTable data = new UserDataTable(od, attributes);
		data.addRow(new String[] { "xx", "L_attr21", "L_attr31;L_attr33;L_attr35", "abc;def", "15.11.2010",
				"2010/11/15;2010/09/25" });
		data.addRow(new String[] { "yy", "L_attr23", "L_attr32", "abc", "25.2010", "2010/25;2010/09/25" });
		data.addRow(new String[] { "zz", "L_attr24", "", "", "", "" });

		try{
			importer.prepareData(data, null, new SimpleDateFormat(DataType.DISPLAY_DATE_FORMAT), true);
		} catch (ACException e){
			fail("prepare data failed" + e.getErrors().get(0));
		}

		assertEquals("'xx'", data.getValue(0, 0));
		assertEquals(1, data.getValue(0, 1));
		assertEquals("'{1}{3}{5}'", data.getValue(0, 2));
		assertEquals("'{abc}{def}'", data.getValue(0, 3));
		assertEquals("'20101115'", data.getValue(0, 4));
		assertEquals("'{20101115}{20100925}'", data.getValue(0, 5));

		assertEquals("'yy'", data.getValue(1, 0));
		assertEquals(3, data.getValue(1, 1));
		assertEquals("'{2}'", data.getValue(1, 2));
		assertEquals("'{abc}'", data.getValue(1, 3));
		assertNull(data.getValue(1, 4));
		assertEquals("'{20100925}'", data.getValue(1, 5));

		assertEquals("'zz'", data.getValue(2, 0));
		assertEquals(4, data.getValue(2, 1));
		assertNull(data.getValue(2, 2));
		assertNull(data.getValue(2, 3));
		assertNull(data.getValue(2, 4));
		assertNull(data.getValue(2, 5));

	}

	public void testAddAndApplyFormulaAttributes(){
		ObjectDefinition od = new ObjectDefinition();
		List<ObjectAttribute> attributes = new ArrayList<ObjectAttribute>();
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());

		ObjectAttribute oa = createAttribute("attr1", true, true, DataType.TEXT);
		oa.setId(1);
		attributes.add(oa);
		od.getObjectAttributes().add(oa);

		oa = createAttribute("attr2", true, true, DataType.TEXT);
		oa.setId(2);
		attributes.add(oa);
		od.getObjectAttributes().add(oa);

		oa = createAttribute("attr3", true, true, DataType.TEXT);
		oa.setId(3);
		attributes.add(oa);
		oa.setPredefined_(2);
		od.getObjectAttributes().add(oa);

		ObjectAttribute oa4 = createAttribute("attr4", true, true, DataType.TEXT);
		oa4.setId(4);
		oa4.setPredefined_(2);
		od.getObjectAttributes().add(oa4);
		Formula f = new Formula();
		f.setAttribute(oa4);
		f.setFormula("print @attr1\n" + "if @attr1.nil?\n" + "   value='zz'\n" + "elsif \n" + "\t@attr1==\"a\"\n"
				+ "\tvalue='zzz'\n" + "else\n" + "    value='zzzz'\n" + "end\n" + "\n" + "return value\n");
		oa4.setFormula(f);

		ObjectAttribute oa5 = createAttribute("attr5", true, true, DataType.INT);
		oa5.setId(5);
		oa5.setPredefined_(2);
		od.getObjectAttributes().add(oa5);
		f = new Formula();
		f.setFormula("print @attr3\n" + "if @attr3.nil?\n" + "	value = 0\n" + "elsif \n" + "\t@attr3==\"a\"\n"
				+ "\tvalue=1\n" + "else\n" + "	value=2\n" + "end\n" + "return value\n");
		oa5.setFormula(f);

		UserDataTable userDataTable = new UserDataTable(od, attributes.toArray(new ObjectAttribute[attributes.size()]));
		UserDataImporter udi = new UserDataImporter();
		PrivateAccessor.invokePrivateMethod(udi, "addAndApplyFormulaAttributes", userDataTable);

		attributes = userDataTable.getAttributes();
		oa = attributes.get(2);
		assertEquals("attr3", oa.getName());

		oa = attributes.get(3);
		assertSame(oa4, oa);

		oa = attributes.get(4);
		assertSame(oa5, oa);

		userDataTable.addRow(new String[] { "a", "b", null });
		userDataTable.addRow(new String[] { null, "b", "a" });
		userDataTable.addRow(new String[] { "b", "b", "c" });

		PrivateAccessor.invokePrivateMethod(udi, "addAndApplyFormulaAttributes", userDataTable);

		assertEquals("'zzz'", userDataTable.getValue(0, 3));
		assertEquals("'zz'", userDataTable.getValue(1, 3));
		assertEquals("'zzzz'", userDataTable.getValue(2, 3));

		assertEquals(Byte.valueOf((byte) 0), userDataTable.getValue(0, 4));
		assertEquals(Byte.valueOf((byte) 1), userDataTable.getValue(1, 4));
		assertEquals(Byte.valueOf((byte) 2), userDataTable.getValue(2, 4));
	}

	public void testPlainValue(){
		ObjectAttribute at = new ObjectAttribute();
		Object result = PrivateAccessor.invokePrivateMethod(new UserDataImporter(), "plainValue", at, null);
		assertNull(result);

		result = PrivateAccessor.invokePrivateMethod(new UserDataImporter(), "plainValue", at, 1);
		assertEquals(1, result);

		at.setPredefined_(2);
		result = PrivateAccessor.invokePrivateMethod(new UserDataImporter(), "plainValue", at, 1);
		assertEquals(1, result);
		result = PrivateAccessor.invokePrivateMethod(new UserDataImporter(), "plainValue", at, "zz");
		assertEquals("zz", result);

		at.setPredefined_(0);
		at.setDataType(DataType.TEXT);
		result = PrivateAccessor.invokePrivateMethod(new UserDataImporter(), "plainValue", at, null);
		assertNull(result);
		result = PrivateAccessor.invokePrivateMethod(new UserDataImporter(), "plainValue", at, "'zz'");
		assertEquals("zz", result);
	}

	public void testGetPredefinedId(){
		assertNull(importer.getPredefinedId(attr1, "labelx"));
		assertEquals(new Integer(11), importer.getPredefinedId(attr1, "label1"));
		assertEquals(new Integer(22), importer.getPredefinedId(attr1, "label2"));

		assertNull(importer.getPredefinedId(attr1, "label1###value2"));
		assertEquals(new Integer(11), importer.getPredefinedId(attr1, "label1###value1"));
	}

	private ObjectAttribute createAttribute(String name, boolean predefined, boolean multivalue, DataType dt){
		return createAttribute(name, predefined, multivalue, dt, null);
	}

	private ObjectAttribute createAttribute(String name, boolean predefined, boolean multivalue, DataType dt, String format){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setName(name);
		attr.setId(111);
		attr.setPredefined(predefined);
		attr.setIsMultivalue(multivalue);
		attr.setDataType(dt);
		attr.setFormat(format);
		if (predefined){
			attr.setPredefinedAttributes(createPredefineds(5, name));
		}
		return attr;
	}

	private List<PredefinedAttribute> createPredefineds(int count, String oName){
		List<PredefinedAttribute> pas = new ArrayList<PredefinedAttribute>();
		for (int i = 0; i < count; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId(i + 1);
			pa.setValue(oName + (i + 1));
			pa.setLabel("L_" + oName + (i + 1));
			pas.add(pa);
		}
		return pas;
	}

}
