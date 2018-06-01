package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import junit.framework.TestCase;

public class GradientTest extends TestCase{
	public void testGradient(){
		String haloColor1 = "#000000";
		String haloColor2 = "#ffffff";

		String[] result = GradientButtonListener.makeGradient(haloColor1, haloColor2, 3);
		assertEquals(3, result.length);
		assertEquals(haloColor1, result[0].toLowerCase());
		assertEquals(haloColor2, result[2].toLowerCase());
		assertEquals("#7f7f7f", result[1].toLowerCase());

		result = GradientButtonListener.makeGradient(haloColor1, haloColor2, 5);
		assertEquals(haloColor1, result[0].toLowerCase());
		assertEquals(haloColor2, result[4].toLowerCase());
		assertEquals("#3f3f3f", result[1].toLowerCase());
		assertEquals("#7f7f7f", result[2].toLowerCase());
		assertEquals("#bfbfbf", result[3].toLowerCase());
	}
}
