/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;

public class CustomChecksumEncoderTest extends TestCase{

	public void testEncode(){
		String module = "com.ni3.ag.navigator.base";
		ChecksumEncoder e = new CustomChecksumEncoder();
		List<String> results = new ArrayList<String>();
		for (int u = 1; u < 1000; u++){
			String res = e.encode(u, module);
			assertFalse(results.contains(res));
			results.add(res);
		}
	}
}
