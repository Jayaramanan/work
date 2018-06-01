package com.ni3.ag.navigator.client.domain.cache;

import com.ni3.ag.navigator.client.gateway.IconGateway;
import com.ni3.ag.navigator.client.gateway.ServiceFactory;

public class MetaphorCache extends AbstractImageCache{

	private static final MetaphorCache instance = new MetaphorCache();

	public static MetaphorCache getInstance(){
		return instance;
	}

	@Override
	protected IconGateway getImageLoader(){
		return ServiceFactory.getMetaphorProvider();
	}

	@Override
	protected String getName(){
		return "metaphors";
	}

}
