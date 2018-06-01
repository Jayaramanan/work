/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.PredefAttributeValidationRule;

public class PredefinedAttributeServiceImplTest extends TestCase{

	private PredefinedAttributeServiceImpl service;

	protected void setUp() throws Exception{
		super.setUp();
		service = new PredefinedAttributeServiceImpl();
		service.setPredefAttributeValidationRule(new PredefAttributeValidationRule());
	}

	public void testValidatePredefinedAttributesSuccessEmpty(){
		try{
			service.validatePredefinedAttributes(mock(ObjectAttribute.class));
		} catch (ACException e){
			fail("Exception is not expected");
			e.printStackTrace();
		}
	}

	public void testValidatePredefinedAttributesSuccessSingleEntry(){
		final PredefinedAttribute predefinedAttribute = mock(PredefinedAttribute.class);
		when(predefinedAttribute.getValue()).thenReturn("value");
		when(predefinedAttribute.getLabel()).thenReturn("label");
		final LinkedList<PredefinedAttribute> predefinedAttributes = new LinkedList<PredefinedAttribute>();
		predefinedAttributes.add(predefinedAttribute);
		final ObjectAttribute objectAttribute = mock(ObjectAttribute.class);
		when(objectAttribute.getPredefinedAttributes()).thenReturn(predefinedAttributes);

		try{
			service.validatePredefinedAttributes(objectAttribute);
		} catch (ACException e){
			fail("Exception is not expected");
			e.printStackTrace();
		}
	}

	public void testValidatePredefinedAttributesSuccessTwoEntries(){
		final PredefinedAttribute predefinedAttribute1 = mock(PredefinedAttribute.class);
		when(predefinedAttribute1.getValue()).thenReturn("value1");
		when(predefinedAttribute1.getLabel()).thenReturn("label1");
		final PredefinedAttribute predefinedAttribute2 = mock(PredefinedAttribute.class);
		when(predefinedAttribute2.getValue()).thenReturn("value2");
		when(predefinedAttribute2.getLabel()).thenReturn("label2");
		final LinkedList<PredefinedAttribute> predefinedAttributes = new LinkedList<PredefinedAttribute>();
		predefinedAttributes.add(predefinedAttribute1);
		predefinedAttributes.add(predefinedAttribute2);
		final ObjectAttribute objectAttribute = mock(ObjectAttribute.class);
		when(objectAttribute.getPredefinedAttributes()).thenReturn(predefinedAttributes);

		try{
			service.validatePredefinedAttributes(objectAttribute);
		} catch (ACException e){
			fail("Exception is not expected");
			e.printStackTrace();
		}
	}

	public void testValidatePredefinedAttributesFail(){
		final PredefinedAttribute predefinedAttribute1 = mock(PredefinedAttribute.class);
		when(predefinedAttribute1.getValue()).thenReturn("value1");
		when(predefinedAttribute1.getLabel()).thenReturn("label1");
		final PredefinedAttribute predefinedAttribute2 = mock(PredefinedAttribute.class);
		when(predefinedAttribute2.getValue()).thenReturn("value1");
		when(predefinedAttribute2.getLabel()).thenReturn("label1");
		final LinkedList<PredefinedAttribute> predefinedAttributes = new LinkedList<PredefinedAttribute>();
		predefinedAttributes.add(predefinedAttribute1);
		predefinedAttributes.add(predefinedAttribute2);
		final ObjectAttribute objectAttribute = mock(ObjectAttribute.class);
		when(objectAttribute.getPredefinedAttributes()).thenReturn(predefinedAttributes);

		try{
			service.validatePredefinedAttributes(objectAttribute);
			fail("Exception expected");
		} catch (ACException e){
			assertEquals(1, e.getErrors().size());
			assertEquals(TextID.MsgDuplicatePredefinedAttributeValueOrLabel, e.getErrors().get(0).getId());
		}
	}

}
