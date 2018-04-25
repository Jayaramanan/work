package com.ni3.ag.adminconsole.shared.service.impl;

import com.ni3.ag.adminconsole.shared.service.def.LoginService;
import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;
import com.ni3.ag.adminconsole.shared.service.impl.bcrypt.BCrypt;

public class BlowFishPasswordEncoder implements PasswordEncoder{

	private LoginService loginService;

	public void setLoginService(LoginService loginService){
		this.loginService = loginService;
	}

	@Override
	public String generate(String src){
		String salt = BCrypt.gensalt(12);
		return BCrypt.hashpw(src, salt);
	}

	public String encode(String login, String pass){
		String salt = getSalt(login);
		if(salt == null || salt.isEmpty())
			return "";
		return BCrypt.hashpw(pass, salt);
	}

	private String getSalt(String login){
		return loginService.getSaltForUser(login);
	}
}
