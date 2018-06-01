/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.useradmin;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class ConnectionFocusListenerTest extends ACTestCase{
	ConnectionFocusListener ls;

	@Override
	protected void setUp() throws Exception{
		ls = new ConnectionFocusListener(null);
	}

	public void testCorrectStringValueNull(){
		assertEquals("", ls.correctStringValue(null));
	}

	public void testCorrectStringValueEmpty(){
		assertEquals("", ls.correctStringValue(""));
	}

	public void testCorrectStringValueSpaces(){
		assertEquals("", ls.correctStringValue("  "));
	}

	public void testCorrectStringValueText(){
		assertEquals("text", ls.correctStringValue("text"));
	}

	public void testCorrectStringValueTextTrim(){
		assertEquals("text text", ls.correctStringValue(" text text  "));
	}
}
