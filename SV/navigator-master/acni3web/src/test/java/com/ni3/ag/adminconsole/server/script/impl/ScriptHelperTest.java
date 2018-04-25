/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.script.impl;

import java.util.List;

import junit.framework.TestCase;

public class ScriptHelperTest extends TestCase{
	public void testGetParams(){
		String formula = "=begin {@param1}asdf {@param2}=end code";
		List<String> params = ScriptHelper.getParams(formula);
		assertEquals(2, params.size());
		assertEquals("@param1", params.get(0));
		assertEquals("@param2", params.get(1));
	}

	public void testGetParamsNotFound(){
		String formula = "{param1}asdf {param2} code";
		List<String> params = ScriptHelper.getParams(formula);
		assertNull(params);
	}

	public void testGetParamsFromCode(){
		String code = "if @AgeRange == '<20'\nvalue = @PatientCount/2\n elsif @AgeRange ==  '21-30'"
		        + "value = @PatientCount + 100 \n else value = @PatientCount**2\n end \nvalue";
		List<String> params = ScriptHelper.getParamsFromCode(code);
		assertEquals(2, params.size());
		assertEquals("AgeRange", params.get(0));
		assertEquals("PatientCount", params.get(1));
	}

	public void testGetParamsFromCode2(){
		String code = "@AgeRange == '<20'\nvalue = @PatientCount/2\n elsif @AgeRange ==  '21-30'"
		        + "value = @PatientCount_ + 100 \n else value = @PatientCount2**2\n end \nvalue";
		List<String> params = ScriptHelper.getParamsFromCode(code);
		assertEquals(4, params.size());
		assertEquals("AgeRange", params.get(0));
		assertEquals("PatientCount", params.get(1));
		assertEquals("PatientCount_", params.get(2));
		assertEquals("PatientCount2", params.get(3));
	}

	public void testGetParamsFromCode3(){
		String code = "@AgeRange";
		List<String> params = ScriptHelper.getParamsFromCode(code);
		assertEquals(1, params.size());
		assertEquals("AgeRange", params.get(0));
	}
}
