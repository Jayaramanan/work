/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.gateway;

import com.ni3.ag.navigator.shared.login.LoginResult;

public interface LoginGateway{

	boolean logout(String url, String session);

	LoginResult loginWithUserNamePassword(String url, String userName, String password, boolean sync);
}
