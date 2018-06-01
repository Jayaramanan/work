/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class AttributeFormatRuleTest extends TestCase{
	private AttributeFormatRule rule;
	private SchemaAdminModel model;
	private ObjectAttribute oa;

	@Override
	protected void setUp() throws Exception{
		rule = new AttributeFormatRule();
		model = new SchemaAdminModel();
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		model.setCurrentObjectDefinition(od);

		oa = new ObjectAttribute();
		oa.setFormat("format");
		oa.setEditFormat("editFormat");
		oa.setMinValue("10");
		oa.setMaxValue("100");
		oa.setFormatValidCharacters("valid");
		oa.setFormatInvalidCharacters("invalid");
	}

	public void testAdjustFormatsPredefined(){
		oa.setPredefined(true);
		rule.adjustFormats(oa);

		assertNull(oa.getFormat());
		assertNull(oa.getEditFormat());
		assertNull(oa.getMinValue());
		assertNull(oa.getMaxValue());
		assertNull(oa.getFormatValidCharacters());
		assertNull(oa.getFormatInvalidCharacters());
	}

	public void testAdjustFormatsURL(){
		oa.setDataType(DataType.URL);
		rule.adjustFormats(oa);

		assertNull(oa.getFormat());
		assertNull(oa.getEditFormat());
		assertNull(oa.getMinValue());
		assertNull(oa.getMaxValue());
		assertNull(oa.getFormatValidCharacters());
		assertNull(oa.getFormatInvalidCharacters());
	}

	public void testAdjustFormatsDate(){
		oa.setDataType(DataType.DATE);
		rule.adjustFormats(oa);

		assertEquals("format", oa.getFormat());
		assertEquals("editFormat", oa.getEditFormat());
		assertNull(oa.getMinValue());
		assertNull(oa.getMaxValue());
		assertNull(oa.getFormatValidCharacters());
		assertNull(oa.getFormatInvalidCharacters());
	}

	public void testAdjustFormatsInt(){
		oa.setDataType(DataType.INT);
		rule.adjustFormats(oa);

		assertEquals("format", oa.getFormat());
		assertEquals("editFormat", oa.getEditFormat());
		assertEquals("10", oa.getMinValue());
		assertEquals("100", oa.getMaxValue());
		assertNull(oa.getFormatValidCharacters());
		assertNull(oa.getFormatInvalidCharacters());
	}

	public void testAdjustFormatsDecimal(){
		oa.setDataType(DataType.DECIMAL);
		rule.adjustFormats(oa);

		assertEquals("format", oa.getFormat());
		assertEquals("editFormat", oa.getEditFormat());
		assertEquals("10", oa.getMinValue());
		assertEquals("100", oa.getMaxValue());
		assertNull(oa.getFormatValidCharacters());
		assertNull(oa.getFormatInvalidCharacters());
	}

	public void testAdjustFormatsText(){
		oa.setDataType(DataType.TEXT);
		rule.adjustFormats(oa);

		assertEquals("format", oa.getFormat());
		assertEquals("editFormat", oa.getEditFormat());
		assertNull(oa.getMinValue());
		assertNull(oa.getMaxValue());
		assertEquals("valid", oa.getFormatValidCharacters());
		assertEquals("invalid", oa.getFormatInvalidCharacters());
	}
}
