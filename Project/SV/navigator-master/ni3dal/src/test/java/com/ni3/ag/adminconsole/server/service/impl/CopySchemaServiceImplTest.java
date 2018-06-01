/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;

public class CopySchemaServiceImplTest extends TestCase{
	CopySchemaServiceImpl impl;

	@Override
	protected void setUp() throws Exception{
		impl = new CopySchemaServiceImpl();
	}

	public void testGetNewObject(){
		ObjectDefinition origObject = new ObjectDefinition();
		origObject.setName("name");

		Schema cloneSchema = new Schema();
		cloneSchema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		ObjectDefinition cloneObject1 = new ObjectDefinition();
		cloneObject1.setName("name1");
		ObjectDefinition cloneObject2 = new ObjectDefinition();
		cloneObject2.setName("name");

		cloneSchema.getObjectDefinitions().add(cloneObject1);
		cloneSchema.getObjectDefinitions().add(cloneObject2);

		ObjectDefinition result = impl.getNewObject(origObject, cloneSchema);
		assertSame(cloneObject2, result);
	}

	public void testGetNewObjectNotFound(){
		ObjectDefinition origObject = new ObjectDefinition();
		origObject.setName("name");

		Schema cloneSchema = new Schema();
		cloneSchema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		ObjectDefinition cloneObject1 = new ObjectDefinition();
		cloneObject1.setName("name1");
		ObjectDefinition cloneObject2 = new ObjectDefinition();
		cloneObject2.setName("name2");

		cloneSchema.getObjectDefinitions().add(cloneObject1);
		cloneSchema.getObjectDefinitions().add(cloneObject2);

		ObjectDefinition result = impl.getNewObject(origObject, cloneSchema);
		assertNull(result);
	}

	public void testGetNewConnectionType(){
		PredefinedAttribute origPa = new PredefinedAttribute();
		origPa.setValue("value");

		ObjectDefinition cloneObject = new ObjectDefinition();
		cloneObject.setObjectAttributes(new ArrayList<ObjectAttribute>());

		ObjectAttribute cloneAttr1 = new ObjectAttribute(cloneObject);
		cloneAttr1.setName("name");

		ObjectAttribute cloneAttr2 = new ObjectAttribute(cloneObject);
		cloneAttr2.setName("ConnectionType");
		cloneAttr2.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setValue("value1");
		cloneAttr2.getPredefinedAttributes().add(pa1);

		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setValue("value");
		cloneAttr2.getPredefinedAttributes().add(pa2);

		cloneObject.getObjectAttributes().add(cloneAttr1);
		cloneObject.getObjectAttributes().add(cloneAttr2);

		PredefinedAttribute result = impl.getNewConnectionType(origPa, cloneObject);
		assertSame(pa2, result);
	}

	public void testGetNewConnectionTypeNotFound(){
		PredefinedAttribute origPa = new PredefinedAttribute();
		origPa.setValue("value");

		ObjectDefinition cloneObject = new ObjectDefinition();
		cloneObject.setObjectAttributes(new ArrayList<ObjectAttribute>());

		ObjectAttribute cloneAttr1 = new ObjectAttribute(cloneObject);
		cloneAttr1.setName("name");

		ObjectAttribute cloneAttr2 = new ObjectAttribute(cloneObject);
		cloneAttr2.setName("ConnectionType");
		cloneAttr2.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setValue("value1");
		cloneAttr2.getPredefinedAttributes().add(pa1);

		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setValue("value2");
		cloneAttr2.getPredefinedAttributes().add(pa2);

		cloneObject.getObjectAttributes().add(cloneAttr1);
		cloneObject.getObjectAttributes().add(cloneAttr2);

		PredefinedAttribute result = impl.getNewConnectionType(origPa, cloneObject);
		assertNull(result);
	}

	public void testCopyChartAttributes() throws CloneNotSupportedException{
		CopySchemaServiceImpl impl = new CopySchemaServiceImpl();
		Chart origChart = new Chart();
		ObjectDefinition od = new ObjectDefinition();
		ObjectChart origObjectChart = generateObjectChart(origChart, od);
		ObjectAttribute attr = new ObjectAttribute();
		attr.setName("attr 1");
		attr.setObjectDefinition(od);
		List<ChartAttribute> chartAttributes = new ArrayList<ChartAttribute>();
		for (int i = 1; i <= 5; i++){
			ChartAttribute cca = new ChartAttribute();
			cca.setId(i);
			cca.setObjectChart(origObjectChart);
			cca.setAttribute(attr);
			cca.setRgb("000000" + i);
			chartAttributes.add(cca);
		}

		origObjectChart.setChartAttributes(chartAttributes);

		Chart newChart = new Chart();
		ObjectDefinition newOd = new ObjectDefinition();
		ObjectChart newObjectChart = generateObjectChart(newChart, newOd);
		ObjectAttribute newAttr0 = new ObjectAttribute();
		newAttr0.setName("attr 0");
		newAttr0.setObjectDefinition(newOd);

		ObjectAttribute newAttr1 = new ObjectAttribute();
		newAttr1.setName("attr 1");
		newAttr1.setObjectDefinition(newOd);
		newOd.setObjectAttributes(new ArrayList<ObjectAttribute>());
		newOd.getObjectAttributes().add(newAttr0);
		newOd.getObjectAttributes().add(newAttr1);

		impl.copyChartAttributes(origObjectChart, newObjectChart);
		assertEquals(5, newObjectChart.getChartAttributes().size());
		for (int i = 0; i < 5; i++){
			ChartAttribute ca = newObjectChart.getChartAttributes().get(i);
			assertSame(newObjectChart, ca.getObjectChart());
			assertSame(newAttr1, ca.getAttribute());
			assertEquals(origObjectChart.getChartAttributes().get(i).getRgb(), ca.getRgb());
		}
	}

	public void testCopyObjectCharts() throws CloneNotSupportedException{
		CopySchemaServiceImpl impl = new CopySchemaServiceImpl();
		Chart origChart = new Chart();
		ObjectDefinition od = new ObjectDefinition();
		od.setId(1);
		od.setName("odname");

		List<ObjectChart> objectCharts = generateObjectCharts(origChart, od);
		origChart.setObjectCharts(objectCharts);

		Chart newChart = new Chart();
		Schema schema = new Schema();
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		ObjectDefinition od1 = new ObjectDefinition();
		od1.setId(2);
		od1.setName("odname");
		schema.getObjectDefinitions().add(od1);
		newChart.setSchema(schema);

		impl.copyObjectCharts(origChart, newChart);
		assertEquals(5, newChart.getObjectCharts().size());
		for (int i = 0; i < 5; i++){
			ObjectChart oc = newChart.getObjectCharts().get(i);
			assertSame(newChart, oc.getChart());
			assertSame(origChart.getObjectCharts().get(i).getChartType(), oc.getChartType());
			assertSame(origChart.getObjectCharts().get(i).getDisplayOperation(), oc.getDisplayOperation());
			assertSame(od1, oc.getObject());

			assertEquals(origChart.getObjectCharts().get(i).getLabelFontSize(), oc.getLabelFontSize());
			assertEquals(origChart.getObjectCharts().get(i).getMinValue(), oc.getMinValue());
			assertEquals(origChart.getObjectCharts().get(i).getMaxValue(), oc.getMaxValue());
			assertEquals(origChart.getObjectCharts().get(i).getNumberFormat(), oc.getNumberFormat());
			assertEquals(origChart.getObjectCharts().get(i).getIsValueDisplayed(), oc.getIsValueDisplayed());
			assertEquals(origChart.getObjectCharts().get(i).getLabelInUse(), oc.getLabelInUse());
			assertEquals(origChart.getObjectCharts().get(i).getMinScale(), oc.getMinScale());
			assertEquals(origChart.getObjectCharts().get(i).getMaxScale(), oc.getMaxScale());
		}
	}

	public void testGetNewPredefinedAttribute(){
		PredefinedAttribute origPa = new PredefinedAttribute();
		origPa.setValue("value");

		ObjectAttribute cloneAttr = new ObjectAttribute(new ObjectDefinition());
		cloneAttr.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setValue("value1");
		cloneAttr.getPredefinedAttributes().add(pa1);

		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setValue("value");
		cloneAttr.getPredefinedAttributes().add(pa2);

		PredefinedAttribute result = impl.getNewPredefinedAttribute(origPa, cloneAttr);
		assertSame(pa2, result);
	}

	public void testGetNewPredefinedAttributeNoMatch(){
		PredefinedAttribute origPa = new PredefinedAttribute();
		origPa.setValue("value");

		ObjectAttribute cloneAttr = new ObjectAttribute(new ObjectDefinition());
		cloneAttr.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setValue("value1");
		cloneAttr.getPredefinedAttributes().add(pa1);

		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setValue("value2");
		cloneAttr.getPredefinedAttributes().add(pa1);

		PredefinedAttribute result = impl.getNewPredefinedAttribute(origPa, cloneAttr);
		assertNull(result);
	}

	public void testCopyPredefinedAttributePrivileges(){
		PredefinedAttribute pa1 = new PredefinedAttribute();
		Group group1 = new Group();
		group1.setId(1);
		Group group2 = new Group();
		group2.setId(2);
		GroupPrefilter gp1 = new GroupPrefilter(group1, pa1);
		GroupPrefilter gp2 = new GroupPrefilter(group2, pa1);
		List<GroupPrefilter> gps = new ArrayList<GroupPrefilter>();
		gps.add(gp1);
		gps.add(gp2);

		PredefinedAttribute pa2 = new PredefinedAttribute();
		impl.copyPredefinedAttributePrivileges(pa1, pa2);
		assertNull(pa2.getPredefAttributeGroups());

		pa1.setPredefAttributeGroups(gps);
		impl.copyPredefinedAttributePrivileges(pa1, pa2);

		assertEquals(2, pa2.getPredefAttributeGroups().size());
		assertNotSame(group1, pa2.getPredefAttributeGroups().get(0));
		assertNotSame(group2, pa2.getPredefAttributeGroups().get(1));

	}

	public void testCopyAttributePrivileges() throws CloneNotSupportedException{
		ObjectAttribute attr1 = new ObjectAttribute();
		Group group1 = new Group();
		group1.setId(1);
		Group group2 = new Group();
		group2.setId(2);
		AttributeGroup gp1 = new AttributeGroup(attr1, group1);
		gp1.setCanRead(false);
		gp1.setEditingOption(EditingOption.NotVisible);
		gp1.setEditingOptionLocked(EditingOption.ReadOnly);
		AttributeGroup gp2 = new AttributeGroup(attr1, group2);
		gp2.setCanRead(true);
		gp2.setEditingOption(EditingOption.Editable);
		gp2.setEditingOptionLocked(EditingOption.Mandatory);
		List<AttributeGroup> gps = new ArrayList<AttributeGroup>();
		gps.add(gp1);
		gps.add(gp2);

		ObjectAttribute attr2 = new ObjectAttribute();
		impl.copyAttributePrivileges(attr1, attr2);
		assertNull(attr2.getAttributeGroups());

		attr1.setAttributeGroups(gps);
		impl.copyAttributePrivileges(attr1, attr2);

		assertEquals(2, attr2.getAttributeGroups().size());

		AttributeGroup resultAG0 = attr2.getAttributeGroups().get(0);
		AttributeGroup resultAG1 = attr2.getAttributeGroups().get(1);

		assertNotSame(group1, resultAG0);
		assertNotSame(group2, resultAG1);
		assertFalse(resultAG0.isCanRead());
		assertEquals(EditingOption.NotVisible, resultAG0.getEditingOption());
		assertEquals(EditingOption.ReadOnly, resultAG0.getEditingOptionLocked());
		assertTrue(resultAG1.isCanRead());
		assertEquals(EditingOption.Editable, resultAG1.getEditingOption());
		assertEquals(EditingOption.Mandatory, resultAG1.getEditingOptionLocked());

	}

	public void testCopyObjectPrivileges() throws CloneNotSupportedException{
		ObjectDefinition obj1 = new ObjectDefinition();
		Group group1 = new Group();
		group1.setId(1);
		Group group2 = new Group();
		group2.setId(2);
		ObjectGroup gp1 = new ObjectGroup(obj1, group1);
		gp1.setCanRead(false);
		gp1.setCanUpdate(false);
		gp1.setCanCreate(false);
		gp1.setCanDelete(false);
		ObjectGroup gp2 = new ObjectGroup(obj1, group2);
		gp2.setGroup(group2);
		gp2.setObject(obj1);
		gp2.setCanRead(true);
		gp2.setCanUpdate(true);
		gp2.setCanCreate(true);
		gp2.setCanDelete(true);
		List<ObjectGroup> gps = new ArrayList<ObjectGroup>();
		gps.add(gp1);
		gps.add(gp2);

		ObjectDefinition obj2 = new ObjectDefinition();
		impl.copyObjectPrivileges(obj1, obj2);
		assertNull(obj2.getObjectGroups());

		obj1.setObjectGroups(gps);
		impl.copyObjectPrivileges(obj1, obj2);

		assertEquals(2, obj2.getObjectGroups().size());
		assertNotSame(group1, obj2.getObjectGroups().get(0));
		assertNotSame(group2, obj2.getObjectGroups().get(1));
		assertFalse(obj2.getObjectGroups().get(0).isCanRead());
		assertFalse(obj2.getObjectGroups().get(0).isCanUpdate());
		assertFalse(obj2.getObjectGroups().get(0).isCanCreate());
		assertFalse(obj2.getObjectGroups().get(0).isCanDelete());
		assertTrue(obj2.getObjectGroups().get(1).isCanRead());
		assertTrue(obj2.getObjectGroups().get(1).isCanUpdate());
		assertTrue(obj2.getObjectGroups().get(1).isCanCreate());
		assertTrue(obj2.getObjectGroups().get(1).isCanDelete());

	}

	private List<ObjectChart> generateObjectCharts(Chart ch, ObjectDefinition od){
		List<ObjectChart> ocharts = new ArrayList<ObjectChart>();
		for (int i = 1; i <= 5; i++){
			ObjectChart oc = new ObjectChart();
			oc.setChart(ch);
			oc.setChartType(ChartType.PIE);
			oc.setDisplayOperation(ChartDisplayOperation.MIN);
			oc.setIsValueDisplayed(Boolean.TRUE);
			oc.setLabelFontSize("10");
			oc.setLabelInUse(Boolean.TRUE);
			oc.setMaxScale(BigDecimal.ZERO);
			oc.setMaxValue(i * 5);
			oc.setMinScale(BigDecimal.ZERO);
			oc.setMinValue(i);
			oc.setNumberFormat("%d" + i);
			oc.setObject(od);
			ocharts.add(oc);
		}
		return ocharts;
	}

	private ObjectChart generateObjectChart(Chart ch, ObjectDefinition od){
		ObjectChart oc = new ObjectChart();
		oc.setChart(ch);
		oc.setChartType(ChartType.PIE);
		oc.setDisplayOperation(ChartDisplayOperation.MIN);
		oc.setIsValueDisplayed(Boolean.TRUE);
		oc.setLabelFontSize("10");
		oc.setLabelInUse(Boolean.TRUE);
		oc.setMaxScale(BigDecimal.ZERO);
		oc.setMaxValue(5);
		oc.setMinScale(BigDecimal.ZERO);
		oc.setMinValue(1);
		oc.setNumberFormat("%d");
		oc.setObject(od);
		return oc;
	}
}
