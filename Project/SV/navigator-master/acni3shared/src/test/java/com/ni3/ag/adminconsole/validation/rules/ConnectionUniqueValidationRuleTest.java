/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.validation.rules.ConnectionUniqueValidationRule;

public class ConnectionUniqueValidationRuleTest extends TestCase{

	ObjectConnectionModel model;
	ConnectionUniqueValidationRule rule;

	ObjectConnection conn1;
	ObjectConnection conn2;

	PredefinedAttribute ct1;
	PredefinedAttribute ct2;

	ObjectDefinition object1;
	ObjectDefinition object2;

	@Override
	protected void setUp() throws Exception{
		ct1 = new PredefinedAttribute();
		ct1.setId(1);
		ct2 = new PredefinedAttribute();
		ct2.setId(2);

		object1 = new ObjectDefinition();
		object1.setId(11);
		object2 = new ObjectDefinition();
		object2.setId(12);

		conn1 = new ObjectConnection();
		conn1.setConnectionType(ct1);
		conn1.setFromObject(object1);
		conn1.setToObject(object2);

		conn2 = new ObjectConnection();
		conn2.setConnectionType(ct2);
		conn2.setFromObject(object1);
		conn2.setToObject(object2);

		List<ObjectConnection> connections = new ArrayList<ObjectConnection>();
		ObjectDefinition od = new ObjectDefinition();
		od.setId(1);
		od.setObjectConnections(connections);
		connections.add(conn1);
		connections.add(conn2);

		model = new ObjectConnectionModel();

		model.setCurrentObject(od);

		rule = new ConnectionUniqueValidationRule();
	}

	public void testPerformCheckSuccess(){
		rule.performCheck(model);
		assertEquals(0, rule.getErrorEntries().size());

		conn2.setConnectionType(ct1);
		conn2.setToObject(object1);

		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckDuplicate(){
		conn2.setConnectionType(ct1);
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}
}
