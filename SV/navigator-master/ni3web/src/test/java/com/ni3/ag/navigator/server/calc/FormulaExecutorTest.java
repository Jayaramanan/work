package com.ni3.ag.navigator.server.calc;

import java.math.BigInteger;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.shared.domain.DataType;

public class FormulaExecutorTest extends TestCase{

	public void testMakeValue(){
		Attribute a = new Attribute();

		a.setDatatype(DataType.TEXT);
		assertEquals("aaa", FormulaExecutor.makeValue("aaa", a));
		assertEquals("bbb", FormulaExecutor.makeValue("bbb", a));

		a.setDatatype(DataType.URL);
		assertEquals("aaa", FormulaExecutor.makeValue("aaa", a));
		assertEquals("bbb", FormulaExecutor.makeValue("bbb", a));

		a.setDatatype(DataType.DECIMAL);
		assertEquals(1.2, FormulaExecutor.makeValue("1.2", a));
		assertEquals(.0, FormulaExecutor.makeValue(".0", a));
		assertEquals(1.0, FormulaExecutor.makeValue("1", a));
		assertEquals(1.0, FormulaExecutor.makeValue("1.000", a));

		a.setDatatype(DataType.INT);
		assertEquals(0, FormulaExecutor.makeValue("0", a));
		assertEquals(1, FormulaExecutor.makeValue("1", a));
		assertEquals(2, FormulaExecutor.makeValue("2", a));
		assertEquals(10, FormulaExecutor.makeValue("10", a));
	}

	public void testConvertValue(){
		assertEquals("1", FormulaExecutor.convertValue(1));
		assertEquals("1.0", FormulaExecutor.convertValue(1.0));
		assertEquals("1.2", FormulaExecutor.convertValue(1.2));
		assertEquals("1", FormulaExecutor.convertValue(1L));
		assertEquals("12341234123412341234", FormulaExecutor.convertValue(new BigInteger("12341234123412341234")));
		assertEquals("2.3", FormulaExecutor.convertValue(2.3));
		assertEquals("", FormulaExecutor.convertValue(null));
		assertEquals("hello", FormulaExecutor.convertValue("hello"));
	}
}
