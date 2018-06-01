package com.ni3.ag.navigator.client.util.passwordencoder;

import com.ni3.ag.navigator.client.gateway.LoginGatewayEx;
import com.ni3.ag.navigator.client.gateway.impl.HttpLoginGatewayImpl;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordSaltGetter;

public class RemotePasswordSaltGetterImpl implements PasswordSaltGetter{
	private LoginGatewayEx loginGateway = new HttpLoginGatewayImpl();

	@Override
	public String getSalt(String login){
		return loginGateway.getSaltForUser(login);
	}
}
