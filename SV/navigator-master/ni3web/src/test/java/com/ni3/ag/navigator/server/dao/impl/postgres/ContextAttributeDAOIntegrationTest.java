package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.domain.ContextAttribute;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.ContextAttributeDAO;

public class ContextAttributeDAOIntegrationTest extends TestCase{

	final ContextAttributeDAO dao = NSpringFactory.getInstance().getContextAttributeDao();

	public void testFindByContextId(){
		final List<ContextAttribute> list = dao.findByContextId(0);
		assertEquals(Integer.valueOf(0), Integer.valueOf(list.size()));
	}

}
