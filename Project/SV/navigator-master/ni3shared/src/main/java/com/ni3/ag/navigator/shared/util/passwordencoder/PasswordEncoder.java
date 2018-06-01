/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.shared.util.passwordencoder;

public interface PasswordEncoder{
	public static final String PASSWORD_ENCODER_PROPERTY = "com.ni3.ag.navigator.common.passwordEncodingClass";
	public static final String DEFAULT_PASSWORD_ENCODER = "com.ni3.ag.navigator.shared.util.passwordencoder.SimplePasswordEncoder";

	String encode(String login, String password);

	String generate(String pass);
}
