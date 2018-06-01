/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class PredefinedAttributeValueRuleTest extends TestCase{
	private PredefinedAttributeEditModel model;
	private PredefinedAttributeValueRule rule;
	private PredefinedAttribute pa1;
	private PredefinedAttribute pa2;
	private ObjectAttribute attribute;

	@Override
	protected void setUp() throws Exception{
		model = new PredefinedAttributeEditModel();
		attribute = new ObjectAttribute();
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		pa1 = new PredefinedAttribute();
		pa2 = new PredefinedAttribute();
		attribute.getPredefinedAttributes().add(pa1);
		attribute.getPredefinedAttributes().add(pa2);
		model.setCurrentAttribute(attribute);

		rule = new PredefinedAttributeValueRule();
	}

	public void testCheckIntValuesSuccess(){
		attribute.setDataType(DataType.INT);
		pa1.setValue("123");
		pa2.setValue("0");
		final ErrorEntry result = rule.checkIntValues(attribute.getPredefinedAttributes());
		assertNull(result);
	}

	public void testCheckIntValuesFail(){
		attribute.setDataType(DataType.INT);
		pa1.setValue("1.23");
		pa2.setValue("0");
		ErrorEntry result = rule.checkIntValues(attribute.getPredefinedAttributes());
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotInt, result.getId());
		assertEquals("1.23", result.getErrors().get(0));

		pa1.setValue("0,3");
		pa2.setValue("abc");
		result = rule.checkIntValues(attribute.getPredefinedAttributes());
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotInt, result.getId());
		assertEquals("0,3, abc", result.getErrors().get(0));

		pa1.setValue("");
		pa2.setValue(" ");
		result = rule.checkIntValues(attribute.getPredefinedAttributes());
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotInt, result.getId());
		assertEquals(",  ", result.getErrors().get(0));
	}

	public void testCheckDecimalValuesSuccess(){
		attribute.setDataType(DataType.DECIMAL);
		pa1.setValue("123");
		pa2.setValue("0.5");
		ErrorEntry result = rule.checkDecimalValues(attribute.getPredefinedAttributes());
		assertNull(result);
		pa1.setValue(".23");
		pa2.setValue("0");
		result = rule.checkDecimalValues(attribute.getPredefinedAttributes());
		assertNull(result);
	}

	public void testCheckDecimalValuesFail(){
		attribute.setDataType(DataType.DECIMAL);
		pa1.setValue("test");
		pa2.setValue("0");
		ErrorEntry result = rule.checkDecimalValues(attribute.getPredefinedAttributes());
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotDecimal, result.getId());
		assertEquals("test", result.getErrors().get(0));

		pa1.setValue("0,3");
		pa2.setValue("abc");
		result = rule.checkDecimalValues(attribute.getPredefinedAttributes());
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotDecimal, result.getId());
		assertEquals("0,3, abc", result.getErrors().get(0));

		pa1.setValue("");
		pa2.setValue(" ");
		result = rule.checkDecimalValues(attribute.getPredefinedAttributes());
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotDecimal, result.getId());
		assertEquals(",  ", result.getErrors().get(0));
	}

	public void testCheckDateValuesSuccess(){
		attribute.setDataType(DataType.DATE);
		pa1.setValue("15/5/2010");
		pa2.setValue("1/1/2000");
		ErrorEntry result = rule.checkDateValues(attribute.getPredefinedAttributes(), null);
		assertNull(result);

		result = rule.checkDateValues(attribute.getPredefinedAttributes(), "");
		assertNull(result);

		pa1.setValue("2011-12-31");
		pa2.setValue("1999-1-1");
		result = rule.checkDateValues(attribute.getPredefinedAttributes(), "yyyy-MM-dd");
		assertNull(result);
	}

	public void testCheckDateValuesFail(){
		attribute.setDataType(DataType.DATE);
		pa1.setValue("5/2010");
		pa2.setValue("1/1/2000");
		ErrorEntry result = rule.checkDateValues(attribute.getPredefinedAttributes(), null);
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotDate, result.getId());
		assertEquals("dd/MM/yyyy", result.getErrors().get(0));
		assertEquals("5/2010", result.getErrors().get(1));

		pa1.setValue("1/1/2000");
		pa2.setValue("15.2.1999");
		result = rule.checkDateValues(attribute.getPredefinedAttributes(), "yyyy-MM-dd");
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotDate, result.getId());
		assertEquals("yyyy-MM-dd", result.getErrors().get(0));
		assertEquals("1/1/2000, 15.2.1999", result.getErrors().get(1));

		pa1.setValue("");
		pa2.setValue(" ");
		result = rule.checkDateValues(attribute.getPredefinedAttributes(), "dd/MM/yyyy");
		assertNotNull(result);
		assertEquals(TextID.MsgPredefinedValuesNotDate, result.getId());
		assertEquals("dd/MM/yyyy", result.getErrors().get(0));
		assertEquals(",  ", result.getErrors().get(1));
	}

}
