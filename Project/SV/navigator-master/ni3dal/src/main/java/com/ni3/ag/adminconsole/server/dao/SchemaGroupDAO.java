/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;

public interface SchemaGroupDAO{

	SchemaGroup getSchemaGroup(Schema schema, Group group);

	void updateSchemaGroup(SchemaGroup sg);

}
