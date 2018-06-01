/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ObjectConnectionValidationRule;

public class ObjectConnectionValidationRuleTest extends TestCase{

	public void testPerformCheck(){

		ObjectConnection conn1 = new ObjectConnection();
		conn1.setConnectionType(new PredefinedAttribute());
		conn1.setFromObject(new ObjectDefinition());
		conn1.setToObject(new ObjectDefinition());
		conn1.setObject(new ObjectDefinition());
		conn1.setRgb("#000000");
		conn1.setLineStyle(LineStyle.FULL);
		conn1.setLineWeight(new LineWeight());

		List<ObjectConnection> connections = new ArrayList<ObjectConnection>();
		ObjectDefinition od = new ObjectDefinition();
		od.setId(1);
		od.setObjectConnections(connections);
		ObjectConnectionModel model = new ObjectConnectionModel();
		model.setCurrentObject(od);
		connections.add(conn1);

		ACValidationRule rule = new ObjectConnectionValidationRule();

		// success
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(errors.size(), 0);

		// connection type empty
		conn1.setConnectionType(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		// fromobject empty
		conn1.setConnectionType(new PredefinedAttribute());
		conn1.setFromObject(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		// toobject empty
		conn1.setFromObject(new ObjectDefinition());
		conn1.setToObject(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		// object empty
		conn1.setToObject(new ObjectDefinition());
		conn1.setObject(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		// linestyle empty
		conn1.setObject(new ObjectDefinition());
		conn1.setLineStyle(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		// linewidth empty
		conn1.setLineStyle(LineStyle.FULL);
		conn1.setLineWeight(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		// linecolor empty
		conn1.setLineWeight(new LineWeight());
		conn1.setRgb(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

	}
}
