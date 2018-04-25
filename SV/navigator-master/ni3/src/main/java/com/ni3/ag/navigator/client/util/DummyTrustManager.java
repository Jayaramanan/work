/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.util;

import java.security.cert.X509Certificate;

import com.sun.net.ssl.X509TrustManager;

@SuppressWarnings("deprecation")
public class DummyTrustManager implements X509TrustManager{
	public boolean isClientTrusted(X509Certificate[] cert){
		return true;
	}

	public boolean isServerTrusted(X509Certificate[] cert){
		return true;
	}

	public X509Certificate[] getAcceptedIssuers(){
		return new X509Certificate[0];
	}
}
