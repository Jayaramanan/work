package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import junit.framework.TestCase;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DeltaParamDAO;

public class DeltaParamDAOIntegrationTest extends TestCase{
	private NSpringFactory daoFactory = NSpringFactory.getInstance();
	private DeltaParamDAO deltaParamDAO = daoFactory.getDeltaParamDAO();

	public void testGet(){
		DeltaParam deltaParam = deltaParamDAO.get(1);
		assertNotNull(deltaParam);
		assertEquals(1, deltaParam.getId());
		assertEquals(DeltaParamIdentifier.UpdateSettingsPropertyName, deltaParam.getName());
		assertEquals("Schema", deltaParam.getValue());
	}
}
