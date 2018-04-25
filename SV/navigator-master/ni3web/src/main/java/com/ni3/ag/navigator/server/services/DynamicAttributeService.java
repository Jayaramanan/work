/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DynamicAttributeDescriptor;
import com.ni3.ag.navigator.shared.domain.User;

public interface DynamicAttributeService{

	List<DBObject> getDynamicValues(User user, DynamicAttributeDescriptor descriptor);
}
