package com.ni3.ag.navigator.shared.util.passwordencoder;

import com.ni3.ag.navigator.shared.util.passwordencoder.bcrypt.BCrypt;

public class BlowFishPasswordEncoder implements PasswordEncoder{

	private PasswordSaltGetter passwordSaltGetter;

	public void setPasswordSaltGetter(PasswordSaltGetter passwordSaltGetter){
		this.passwordSaltGetter = passwordSaltGetter;
	}

	private String getSalt(String login){
		return passwordSaltGetter.getSalt(login);
	}

	@Override
	public String encode(String login, String pass){
		String salt = getSalt(login);
		return BCrypt.hashpw(pass, salt);
	}

	@Override
	public String generate(String pass){
		String salt = BCrypt.gensalt(12);
		return BCrypt.hashpw(pass, salt);
	}
}
