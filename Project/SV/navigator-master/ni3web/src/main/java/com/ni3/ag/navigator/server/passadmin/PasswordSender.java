/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.passadmin;

import com.ni3.ag.navigator.shared.domain.User;

public interface PasswordSender{
	boolean sendEmail(User user, String newPass);
}
