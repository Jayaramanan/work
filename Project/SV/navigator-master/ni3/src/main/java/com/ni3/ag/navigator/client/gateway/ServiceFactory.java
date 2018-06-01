package com.ni3.ag.navigator.client.gateway;

import com.ni3.ag.navigator.client.gateway.impl.HttpIconGatewayImpl;
import com.ni3.ag.navigator.client.model.SystemGlobals;

public class ServiceFactory{
	public static IconGateway getMetaphorProvider(){
		return new HttpIconGatewayImpl(SystemGlobals.MetaphorURL);
	}

	public static IconGateway getIconProvider(){
		return new HttpIconGatewayImpl(SystemGlobals.IconURL);
	}
}
