/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.shared.domain.User;
import java.util.List;

public interface UserService{

	List<User> getOfflineUsers();
}
