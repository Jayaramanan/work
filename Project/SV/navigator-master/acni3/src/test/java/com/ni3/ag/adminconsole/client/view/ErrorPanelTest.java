/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class ErrorPanelTest extends ACTestCase{

	public void testCreateMessageText(){
		ErrorPanel panel = new ErrorPanel();

		List<String> messages = new ArrayList<String>();

		// empty list
		assertEquals("", panel.createMessageText(messages));

		// null
		assertEquals("", panel.createMessageText(null));

		// one message
		messages.add("message1");
		assertEquals("<html><body>message1<br></body></html>", panel.createMessageText(messages));

		// two messages
		messages.add("message2");
		assertEquals("<html><body>message1<br>message2<br></body></html>", panel.createMessageText(messages));
	}
}
