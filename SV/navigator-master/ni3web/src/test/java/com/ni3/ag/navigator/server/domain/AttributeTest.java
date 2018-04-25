package com.ni3.ag.navigator.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.type.PredefinedType;
import com.ni3.ag.navigator.shared.domain.DataType;
import junit.framework.TestCase;

public class AttributeTest extends TestCase{

	public void setUp(){
		NSpringFactory.init();
	}

	public void testCanGroupsRead(){
		Attribute a = new Attribute();
		assertFalse(a.canGroupsRead(null));
		assertFalse(a.canGroupRead(1));
		assertFalse(a.canGroupRead(2));
		assertFalse(a.canGroupRead(3));
		assertFalse(a.canGroupRead(4));
		assertFalse(a.canGroupsRead(new int[0]));
		assertFalse(a.canGroupsRead(new int[] { 1, 2, 3 }));

		a.setAttributeGroups(new ArrayList<AttributeGroup>());
		AttributeGroup ag = new AttributeGroup();
		ag.setAttribute(a);
		ag.setGroupId(1);
		ag.setCanRead(true);
		a.getAttributeGroups().add(ag);

		ag = new AttributeGroup();
		ag.setAttribute(a);
		ag.setGroupId(2);
		ag.setCanRead(true);
		a.getAttributeGroups().add(ag);

		assertTrue(a.canGroupsRead(new int[] { 1 }));
		assertTrue(a.canGroupsRead(new int[] { 1, 2 }));
		assertTrue(a.canGroupsRead(new int[] { 2 }));
		assertTrue(a.canGroupsRead(new int[] { 2, 1 }));
		assertFalse(a.canGroupsRead(new int[] { 3 }));
		assertFalse(a.canGroupsRead(new int[] { 4 }));
		assertFalse(a.canGroupsRead(new int[] { 5 }));
	}

	public void testformatValueBindOperatorText(){
		Attribute a = new Attribute();
		a.setDatatype(DataType.TEXT);
		String[] result = a.formatValueBind("", "");
		assertEquals(result[0], " IS NULL");

		result = a.formatValueBind(null, "");
		assertEquals(result[0], " IS NULL");

		result = a.formatValueBind("a", "~");
		assertEquals(" ILIKE ?", result[0]);
		assertEquals("%a%", result[1]);

		result = a.formatValueBind("a", "<>");
		assertEquals(" NOT ILIKE ?", result[0]);
		assertEquals("a", result[1]);

		result = a.formatValueBind("a", "=");
		assertEquals(" ILIKE ?", result[0]);
		assertEquals("a", result[1]);

		a.setMultivalue(true);

		result = a.formatValueBind(null, "");
		assertEquals(result[0], " IS NULL");

		result = a.formatValueBind("a", "~");
		assertEquals(" ILIKE ?", result[0]);
		assertEquals("%{%a%}%", result[1]);

		result = a.formatValueBind("a", "<>");
		assertEquals(" NOT ILIKE ?", result[0]);
		assertEquals("%{a}%", result[1]);

		result = a.formatValueBind("a", "=");
		assertEquals(" ILIKE ?", result[0]);
		assertEquals("%{a}%", result[1]);
	}

	public void testformatValueBindOperatorInt(){
		Attribute a = new Attribute();
		a.setDatatype(DataType.INT);
		String[] result = a.formatValueBind(null, "=");
		assertEquals("=0", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("0", "=");
		assertEquals("=0", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("1", "=");
		assertEquals("=1", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("1", "<>");
		assertEquals("<>1", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("0,1,2,3", "=");
		assertEquals(" IN (0,1,2,3)", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("1,2,3,4", "<>");
		assertEquals(" NOT IN (1,2,3,4)", result[0]);
		assertNull(result[1]);
	}

	public void testformatValueBindOperatorDecimal(){
		Attribute a = new Attribute();
		a.setDatatype(DataType.DECIMAL);
		String[] result = a.formatValueBind(null, "=");
		assertEquals("=0", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("0", "=");
		assertEquals("=0.0", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("1.1", "=");
		assertEquals("=1.1", result[0]);
		assertNull(result[1]);

		result = a.formatValueBind("1.2", "<>");
		assertEquals("<>1.2", result[0]);
		assertNull(result[1]);
	}

	public void testgetValue(){
		List<PredefinedAttribute> values = new ArrayList<PredefinedAttribute>();
		Attribute a = new Attribute();
		a.setValues(values);
		a.setPredefined(PredefinedType.Predefined);
		Object[] data = new Object[] { new Object[] { 1, 1, "val1", "label1", "1", null },
		        new Object[] { 2, 1, "val2", "label2", "2", null }, new Object[] { 3, 1, "val3", "label3", "3", null },
		        new Object[] { 4, 1, "val4", "label4", "4", null } };
		for (Object aData : data){
			Object[] cData = (Object[]) aData;
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId((Integer) cData[0]);
			//pa.setParent((Integer) cData[1]);
			pa.setValue((String) cData[2]);
			pa.setLabel((String) cData[3]);
			cData[5] = pa;
			values.add(pa);
		}
		for (Object aData : data){
			Object[] cData = (Object[]) aData;
			assertEquals(cData[5], a.getValue((Integer) cData[0]));
		}
	}

	public void testisPredefined(){
		Attribute a = new Attribute();
		a.setPredefined(PredefinedType.Predefined);
		assertTrue(a.isPredefined());
		a.setPredefined(PredefinedType.FormulaPredefined);
		assertTrue(a.isPredefined());
		a.setPredefined(PredefinedType.Formula);
		assertFalse(a.isPredefined());
		a.setPredefined(PredefinedType.NotPredefined);
		assertFalse(a.isPredefined());
	}

}
