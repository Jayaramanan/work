package com.ni3.ag.navigator.server.cache;

public class SSOCacheMock implements SSOCache{

	@Override
	public String getSSOUsername(String token){
		return "u1";
	}

}
