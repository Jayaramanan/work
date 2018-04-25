/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.Activity;

public interface ActivityStreamGateway{

	List<Activity> getLastActivities(int count, int schemaID, long lastId);

}
