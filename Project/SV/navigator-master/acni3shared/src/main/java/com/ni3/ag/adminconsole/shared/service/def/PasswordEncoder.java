/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

public interface PasswordEncoder{
	public String generate(String src);

	public String encode(String login, String password);
}
