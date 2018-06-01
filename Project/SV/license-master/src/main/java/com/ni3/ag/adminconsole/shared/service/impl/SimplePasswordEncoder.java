/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;

public class SimplePasswordEncoder implements PasswordEncoder{

	@Override
	public String generate(String src){
		return src;
	}

	@Override
	public String encode(String login, String password){
		return password;
	}
}
