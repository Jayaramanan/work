/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

import junit.framework.TestCase;

public class PredefinedAttributeDAOImplTest extends TestCase{

	private PredefinedAttributeDAOImpl impl;
	private PredefinedAttribute pa;

	@Override
	protected void setUp() throws Exception{
		impl = new PredefinedAttributeDAOImpl();
		pa = new PredefinedAttribute();
		pa.setId(111);

		ObjectAttribute oa = new ObjectAttribute();
		oa.setName("column1");
		oa.setInTable("tablename1");

		oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		oa.getPredefinedAttributes().add(pa);
		pa.setObjectAttribute(oa);

	}

	public void testGetUpdateValueSql(){
		pa.getObjectAttribute().setIsMultivalue(false);
		Integer newValue = 200;
		String sql = "update tablename1 set column1 = 200 where column1 = 111";
		assertEquals(sql, impl.getUpdateValueSql(pa, newValue));
	}

	public void testGetUpdateValueSqlNewValueNull(){
		pa.getObjectAttribute().setIsMultivalue(false);
		Integer newValue = null;
		String sql = "update tablename1 set column1 = null where column1 = 111";
		assertEquals(sql, impl.getUpdateValueSql(pa, newValue));
	}

	public void testGetUpdateValueSqlMultivalue(){
		pa.getObjectAttribute().setIsMultivalue(true);
		Integer newValue = 200;
		String sql = "update tablename1 set column1 = replace(column1, '{111}','{200}') where column1 like '%{111}%'";
		assertEquals(sql, impl.getUpdateValueSql(pa, newValue));
	}

	public void testGetUpdateValueSqlMultivalueNewValueNull(){
		pa.getObjectAttribute().setIsMultivalue(true);
		Integer newValue = null;
		String sql = "update tablename1 set column1 = replace(column1, '{111}','') where column1 like '%{111}%'";
		assertEquals(sql, impl.getUpdateValueSql(pa, newValue));
	}
}
