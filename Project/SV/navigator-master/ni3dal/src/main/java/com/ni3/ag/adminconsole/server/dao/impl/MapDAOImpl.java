/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.server.dao.MapDAO;

public class MapDAOImpl extends HibernateDaoSupport implements MapDAO{

	@Override
	@SuppressWarnings("unchecked")
	public List<Map> getMaps(){
		return (List<Map>) getHibernateTemplate().loadAll(Map.class);
	}

	@Override
	public void saveOrUpdate(Map map){
		getHibernateTemplate().saveOrUpdate(map);
	}

	@Override
	public void delete(Map map){
		getHibernateTemplate().delete(map);
	}

}
