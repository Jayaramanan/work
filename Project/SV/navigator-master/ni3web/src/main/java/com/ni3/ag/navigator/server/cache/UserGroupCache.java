/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.cache;


public interface UserGroupCache{

	Integer getGroup(Integer userId);

	void reload();
}
