/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Color;
import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorContainer;

public class ObjectConnectionValidationRuleTest extends ACTestCase{
	public void testPerformCheck(){

		String errorMsg = "MsgConnectionFieldsEmpty";

		ObjectConnection conn1 = new ObjectConnection();
		conn1.setConnectionType(new PredefinedAttribute());
		conn1.setFromObject(new ObjectDefinition());
		conn1.setToObject(new ObjectDefinition());
		conn1.setObject(new ObjectDefinition());
		conn1.setLineColor(new Color());
		conn1.setLineStyle(new LineStyle());
		conn1.setLineWeight(new LineWeight());

		List<ObjectConnection> objectConnections = new ArrayList<ObjectConnection>();
		objectConnections.add(conn1);

		ObjectConnectionValidationRule rule = new ObjectConnectionValidationRule(objectConnections);

		// success
		ErrorContainer ec = rule.performCheck();
		assertNull(ec);

		// connection type empty
		conn1.setConnectionType(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// fromobject empty
		conn1.setConnectionType(new PredefinedAttribute());
		conn1.setFromObject(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// toobject empty
		conn1.setFromObject(new ObjectDefinition());
		conn1.setToObject(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// object empty
		conn1.setToObject(new ObjectDefinition());
		conn1.setObject(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// linestyle empty
		conn1.setObject(new ObjectDefinition());
		conn1.setLineStyle(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// linewidth empty
		conn1.setLineStyle(new LineStyle());
		conn1.setLineWeight(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// linecolor empty
		conn1.setLineWeight(new LineWeight());
		conn1.setLineColor(null);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertEquals(errorMsg, ec.getErrors().get(0));

		// success
		conn1.setLineColor(new Color());
		ec = rule.performCheck();
		assertNull(ec);
	}
}
