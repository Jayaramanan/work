/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.shared.util.passwordencoder;

public class SimplePasswordEncoder implements PasswordEncoder{

	@Override
	public String encode(String login, String pass){
		return pass;
	}

	@Override
	public String generate(String pass){
		return pass;
	}
}
