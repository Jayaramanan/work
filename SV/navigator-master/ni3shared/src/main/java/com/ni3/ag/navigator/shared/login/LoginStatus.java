/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.login;

import java.io.Serializable;

public enum LoginStatus implements Serializable{
	Ok, InvalidLogin, NoConnectionToServer, NoConnectionToDB, WrongCurrentPassword, CannotResetPassword, UserNotFound, CannotSendEmail, CannotLoginWithSSO;
}
