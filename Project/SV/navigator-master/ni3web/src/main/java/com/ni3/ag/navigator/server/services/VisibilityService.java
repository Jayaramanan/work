/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.Schema;

public interface VisibilityService{

	/**
	 * filter schema according to accesses
	 * 
	 * @param initialSchema
	 *            - initial schema
	 * @param groupId
	 *            - id of the group
	 * @return filtered schema with additional privileges options
	 */
	Schema getSchemaWithPrivileges(Schema initialSchema, int groupId);

}
