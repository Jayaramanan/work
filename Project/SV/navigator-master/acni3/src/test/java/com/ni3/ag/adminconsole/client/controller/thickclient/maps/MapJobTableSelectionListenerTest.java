/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class MapJobTableSelectionListenerTest extends ACTestCase{
	private MapJobTableSelectionListener listener;

	public void setUp(){
		listener = new MapJobTableSelectionListener(null);
	}

	public void testParseScale(){
		double[] ret = listener.parseScale("123, 321, 0.1");
		assertEquals(ret[0], 123.0);
		assertEquals(ret[1], 321.0);
		assertEquals(ret[2], 0.1);
	}

}
