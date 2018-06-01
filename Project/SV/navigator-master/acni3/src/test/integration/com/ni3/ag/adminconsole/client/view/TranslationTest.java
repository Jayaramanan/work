/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class TranslationTest extends ACTestCase{

	public void testGetParsedMessageNullParams(){
		String text = "Test message";
		assertEquals(text, Translation.getParsedMessage(text, null));
	}

	public void testGetParsedMessageOneParam(){
		String text = "Test {1} message";
		String[] params = { "param" };
		assertEquals("Test param message", Translation.getParsedMessage(text, params));
	}

	public void testGetParsedMessageTwoParams(){
		String text = "Test {1} message {2} one more";
		String[] params = { "param1", "param2" };
		assertEquals("Test param1 message param2 one more", Translation.getParsedMessage(text, params));
	}

	public void testGetParsedMessageNoParamsInMessage(){
		String text = "Test no param in message";
		String[] params = { "param1", "param2" };
		assertEquals(text, Translation.getParsedMessage(text, params));
	}

	public void testGetParsedMessageSameParams(){
		String text = "Test {1} message {2} one more and {1}";
		String[] params = { "param1", "param2" };
		assertEquals("Test param1 message param2 one more and param1", Translation.getParsedMessage(text, params));
	}
}
