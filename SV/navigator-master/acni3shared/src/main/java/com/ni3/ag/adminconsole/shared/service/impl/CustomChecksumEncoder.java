/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;

public class CustomChecksumEncoder implements ChecksumEncoder{

	@Override
	public String encode(Integer userId, String entityId){
		byte[] bytes = entityId.getBytes();
		String x = "";
		for (int i = 0; i < bytes.length; i++){
			int n = (bytes[i] + userId) << (i % 3);
			if (i == 11){
				x += "x3n";
			} else{
				x += n;
			}
		}
		PasswordEncoder e = new MD5PasswordEncoder();
		String encodedX = e.generate(x);
		return encodedX;
	}
}
