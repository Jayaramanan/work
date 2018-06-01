/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class UserDataExporterTest extends TestCase{

	private List<ObjectAttribute> attributes;
	private UserDataExporter exporter;
	private List<Object[]> data;

	@Override
	protected void setUp() throws Exception{
		attributes = new ArrayList<ObjectAttribute>();
		exporter = new UserDataExporter();
		data = new ArrayList<Object[]>();
	}

	public void testPrepareDataFromToIds(){

		Map<Integer, String> idMap = new HashMap<Integer, String>();
		idMap.put(1, "src1");
		idMap.put(2, "src2");
		idMap.put(3, "src3");
		idMap.put(4, "src4");
		idMap.put(5, "src5");
		exporter.setIdMap(idMap);

		data.add(new Object[] { "xx", 1, 2 });
		data.add(new Object[] { "yy", 3, 4 });
		data.add(new Object[] { "zz", 5, 1 });
		ObjectAttribute attr1 = createAttribute("attr1", false, false);
		attr1.setInTable("test");
		attributes.add(attr1);
		ObjectAttribute attr2 = createAttribute("fromid", false, false);
		attr2.setInTable("cis_edges");
		attributes.add(attr2);
		ObjectAttribute attr3 = createAttribute("toid", false, false);
		attr3.setInTable("cis_edges");
		attributes.add(attr3);
		exporter.prepareData(data, attributes);
		assertEquals("src1", data.get(0)[1]);
		assertEquals("src2", data.get(0)[2]);
		assertEquals("src3", data.get(1)[1]);
		assertEquals("src4", data.get(1)[2]);
		assertEquals("src5", data.get(2)[1]);
		assertEquals("src1", data.get(2)[2]);
	}

	public void testGetPredefinedLabel(){
		attributes.add(createAttribute("attr1", true, false));
		attributes.add(createAttribute("attr2", true, false));
		attributes.add(createAttribute("attr3", true, false));
		String label = exporter.getPredefinedLabel(1, attributes.get(0));
		assertEquals("L_attr11", label);
		label = exporter.getPredefinedLabel(2, attributes.get(1));
		assertEquals("L_attr22", label);
		label = exporter.getPredefinedLabel(3, attributes.get(2));
		assertEquals("L_attr33", label);
	}

	public void testParseMultivalue(){
		ObjectAttribute attr = createAttribute("attr", true, true);
		String multivalue = "{1}{3}{5}";
		String parsed = exporter.parseMultivaluePredefined(multivalue, attr);
		assertEquals("L_attr1;L_attr3;L_attr5", parsed);

		multivalue = "{2}";
		parsed = exporter.parseMultivaluePredefined(multivalue, attr);
		assertEquals("L_attr2", parsed);

		multivalue = "{33}";
		parsed = exporter.parseMultivaluePredefined(multivalue, attr);
		assertEquals("", parsed);
	}

	public void testPrepareData(){
		data.add(new Object[] { "xx", 1, "{1}{3}{5}", "{abc}{def}", "20101115", "{20101115}{20100925}" });
		data.add(new Object[] { "yy", 3, "{2}", "{abc}", "20201", "{20101}{20100925}" });
		data.add(new Object[] { "zz", 4, "{33}", "", "", "{20100925}" });
		attributes.add(createAttribute("attr1", false, false));
		attributes.add(createAttribute("attr2", true, false));
		attributes.add(createAttribute("attr3", true, true));
		attributes.add(createAttribute("attr4", false, true));
		attributes.add(createAttribute("attr5", false, false, DataType.DATE, "dd.MM.yyyy"));
		attributes.add(createAttribute("attr6", false, true, DataType.DATE, "yyyy/MM/dd"));

		exporter.prepareData(data, attributes);

		assertEquals("xx", data.get(0)[0]);
		assertEquals("L_attr21", data.get(0)[1]);
		assertEquals("L_attr31;L_attr33;L_attr35", data.get(0)[2]);
		assertEquals("abc;def", data.get(0)[3]);
		assertEquals("15.11.2010", data.get(0)[4]);
		assertEquals("2010/11/15;2010/09/25", data.get(0)[5]);

		assertEquals("yy", data.get(1)[0]);
		assertEquals("L_attr23", data.get(1)[1]);
		assertEquals("L_attr32", data.get(1)[2]);
		assertEquals("abc", data.get(1)[3]);
		assertEquals("", data.get(1)[4]);
		assertEquals("2010/09/25", data.get(1)[5]);

		assertEquals("zz", data.get(2)[0]);
		assertEquals("L_attr24", data.get(2)[1]);
		assertEquals("", data.get(2)[2]);
		assertEquals("", data.get(2)[3]);
		assertEquals("", data.get(2)[4]);
		assertEquals("2010/09/25", data.get(2)[5]);

	}

	public void testGetFormattedDate(){
		DateFormat formatter = exporter.getDateFormat("dd/MM/yyyy");
		assertEquals("20/10/2010", exporter.getFormattedDate("20101020", formatter));
		assertEquals("02/01/2000", exporter.getFormattedDate("20000102", formatter));
		assertEquals("", exporter.getFormattedDate("20013", formatter));
		assertEquals("", exporter.getFormattedDate(null, formatter));
	}

	public void testParseMultivalueDate(){
		DateFormat formatter = exporter.getDateFormat("dd/MM/yyyy");
		assertEquals("20/10/2010;02/01/2000", exporter.parseMultivalueDate("{20101020}{20000102}", formatter));
		assertEquals("02/01/2000", exporter.parseMultivalueDate("{20000102}", formatter));
		assertEquals("", exporter.parseMultivalueDate("20013", formatter));
		assertEquals("", exporter.parseMultivalueDate(null, formatter));
	}

	public void testFormatNumberDecimalDataType(){
		final DataType dt = DataType.DECIMAL;
		final Integer intg = new Integer(12);
		assertEquals(intg, exporter.formatNumber(12, dt));
		assertEquals(intg, exporter.formatNumber(new Integer(12), dt));
		assertEquals(intg, exporter.formatNumber(new Short("12"), dt));
		assertEquals(intg, exporter.formatNumber(new Long(12), dt));
		assertEquals(intg, exporter.formatNumber(new BigInteger("12"), dt));

		final Double dbl = new Double(12.345);
		assertEquals(dbl, exporter.formatNumber(12.345, dt));
		assertEquals(dbl, exporter.formatNumber(new Double(12.345), dt));
		assertEquals(dbl, exporter.formatNumber(new BigDecimal("12.345"), dt));
		assertEquals(dbl, exporter.formatNumber(new Float("12.345"), dt));

		assertEquals(new Double(12), exporter.formatNumber(new Double(12), dt));
		assertEquals(new Double(12), exporter.formatNumber(new BigDecimal("12"), dt));
		assertEquals(new Double(12), exporter.formatNumber(new Float("12"), dt));
	}

	public void testFormatNumberIntDataType(){
		final DataType dt = DataType.INT;
		final Integer intg = new Integer(12);
		assertEquals(intg, exporter.formatNumber(12, dt));
		assertEquals(intg, exporter.formatNumber(new Integer(12), dt));
		assertEquals(intg, exporter.formatNumber(new Short("12"), dt));
		assertEquals(intg, exporter.formatNumber(new Long(12), dt));
		assertEquals(intg, exporter.formatNumber(new BigInteger("12"), dt));

		assertEquals(intg, exporter.formatNumber(12.345, dt));
		assertEquals(intg, exporter.formatNumber(new Double(12.345), dt));
		assertEquals(intg, exporter.formatNumber(new BigDecimal("12.345"), dt));
		assertEquals(intg, exporter.formatNumber(new Float("12.345"), dt));
		assertEquals(intg, exporter.formatNumber(new Float(12), dt));

		assertEquals(intg, exporter.formatNumber(new Double(12), dt));
		assertEquals(intg, exporter.formatNumber(new BigDecimal("12"), dt));
		assertEquals(intg, exporter.formatNumber(new Float("12"), dt));
	}

	private ObjectAttribute createAttribute(String name, boolean predefined, boolean multivalue){
		return createAttribute(name, predefined, multivalue, DataType.TEXT, null);
	}

	private ObjectAttribute createAttribute(String name, boolean predefined, boolean multivalue, DataType dt, String format){
		ObjectAttribute attr = new ObjectAttribute();
		attr.setName(name);
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
