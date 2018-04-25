package com.ni3.ag.navigator.server.services.impl;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.services.ObjectManagementService;

public class ObjectManagementServiceIntegrationTest extends TestCase{
	ObjectManagementService service = NSpringFactory.getInstance().getObjectManagementService();

	public void testGetEdgesByNodeId(){
//		try{
//			List<DBObject> edgesByNodeId = service.getEdgesByNodeId(294446, new Schema(2));
//			assertNotNull(edgesByNodeId);
//			assertEquals(3, edgesByNodeId.size());
//		} catch (SQLException e){
//			fail();
//		}
	}
}
