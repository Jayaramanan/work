/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectConnection;

import junit.framework.TestCase;

public class ObjectConnectionTableModelTest extends TestCase{
	public void testGetSelectedConnection(){
		ObjectConnection conn1 = new ObjectConnection();
		ObjectConnection conn2 = new ObjectConnection();

		List<ObjectConnection> objectConnections = new ArrayList<ObjectConnection>();
		objectConnections.add(conn1);
		objectConnections.add(conn2);

		ObjectConnectionTableModel model = new ObjectConnectionTableModel(objectConnections);
		assertSame(conn1, model.getSelectedConnection(0));
		assertSame(conn2, model.getSelectedConnection(1));

		assertNull(model.getSelectedConnection(2));
		assertNull(model.getSelectedConnection(-1));
	}
}
