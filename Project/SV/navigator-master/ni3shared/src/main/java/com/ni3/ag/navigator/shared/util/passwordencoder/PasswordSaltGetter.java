package com.ni3.ag.navigator.shared.util.passwordencoder;

public interface PasswordSaltGetter{
	String getSalt(String login);
}
