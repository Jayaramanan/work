/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.schemaadmin;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class AttributeFilterTest extends ACTestCase{

	AttributeFilter f;

	@Override
	protected void setUp() throws Exception{
		f = new AttributeFilter();
	}

	public void testGetFormattedTextNotLettersOrDigits(){
		assertEquals("", f.getFormattedText(0, "~!@#$%^&*(){}|?/"));
	}

	public void testGetFormattedTextZeroOffset(){
		assertEquals("", f.getFormattedText(0, "1~!2@#$34%^&*5(){}6|?/"));
		assertEquals("abc456", f.getFormattedText(0, "1~!2@#$3abc4%^&*5(){}6|?/"));
		assertEquals("abc456de_", f.getFormattedText(0, "123abc456de_"));
	}

	public void testGetFormattedTextNotZeroOffset(){
		assertEquals("123abc456de", f.getFormattedText(1, "1~!2@#$3abc4%^&*5(){}6|?/de"));
		assertEquals("123456", f.getFormattedText(1, "1~!2@#$34%^&*5(){}6|?/"));
	}

	public void testGetFormattedTextAllAllowedZeroOffset(){
		assertEquals("a123BC456de_", f.getFormattedText(0, "a123BC456de_"));
		assertEquals("abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789_", f.getFormattedText(0,
		        "abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789_"));
	}

	public void testGetFormattedTextAllAllowedNotZeroOffset(){
		assertEquals("123ABC456de_", f.getFormattedText(1, "123ABC456de_"));
		assertEquals("0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ_", f.getFormattedText(1,
		        "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ_"));
	}
}
