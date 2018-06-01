/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.server.dao.GisTerritoryDAO;

public class GisTerritoryDAOImpl extends HibernateDaoSupport implements GisTerritoryDAO{

	@SuppressWarnings("unchecked")
	@Override
	public List<GisTerritory> getGisTerritories(){
		return getHibernateTemplate().loadAll(GisTerritory.class);
	}

	@Override
	public void deleteAll(List<GisTerritory> territories){
		getHibernateTemplate().deleteAll(territories);
	}

	@Override
	public void saveOrUpdate(GisTerritory territory){
		getHibernateTemplate().saveOrUpdate(territory);
	}

	@Override
	public void saveOrUpdateAll(List<GisTerritory> territories){
		getHibernateTemplate().saveOrUpdateAll(territories);
	}

}
