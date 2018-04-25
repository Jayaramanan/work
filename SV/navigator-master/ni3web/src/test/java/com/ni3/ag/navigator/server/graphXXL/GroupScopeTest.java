/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.graphXXL;

import com.ni3.ag.navigator.server.type.Scope;
import junit.framework.TestCase;

public class GroupScopeTest extends TestCase{

	private GroupScope gs;

	@Override
	protected void setUp() throws Exception{
		gs = new GroupScope();
		gs.getAllowedEntities().add(1);
		gs.getAllowedEntities().add(2);
	}

	public void testGetNodeScope(){
		assertEquals(Scope.Allow, gs.getNodeScope(11, 1));
		assertEquals(Scope.Allow, gs.getNodeScope(12, 2));
		assertEquals(Scope.Denied, gs.getNodeScope(13, 3));

		gs.setHasNodeScope("S");
		gs.getAllowedNodes().add(11);
		gs.getDeniedNodes().add(12);

		assertEquals(Scope.Allow, gs.getNodeScope(11, 1));
		assertEquals(Scope.Denied, gs.getNodeScope(12, 2));
		assertEquals(Scope.Denied, gs.getNodeScope(13, 3));
	}

	public void testGetEdgeScope(){
		assertEquals(Scope.Allow, gs.getEdgeScope(11, 1));
		assertEquals(Scope.Allow, gs.getEdgeScope(12, 2));
		assertEquals(Scope.Denied, gs.getEdgeScope(13, 3));

		gs.setHasEdgeScope("S");
		gs.getAllowedEdges().add(11);
		gs.getDeniedEdges().add(12);

		assertEquals(Scope.Allow, gs.getEdgeScope(11, 1));
		assertEquals(Scope.Denied, gs.getEdgeScope(12, 2));
		assertEquals(Scope.Denied, gs.getEdgeScope(13, 3));
	}
}
