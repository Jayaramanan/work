/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.server.dao.ModuleDAO;

public class ModuleDAOImpl extends HibernateDaoSupport implements ModuleDAO{

	@Override
	public Module getModule(Integer id){
		return (Module) getHibernateTemplate().load(Module.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Module> getModules(){
		return getHibernateTemplate().loadAll(Module.class);
	}

	@Override
	public void saveOrUpdate(Module m){
		getHibernateTemplate().saveOrUpdate(m);
	}

	@Override
	public void saveOrUpdateAll(List<Module> modules){
		getHibernateTemplate().saveOrUpdateAll(modules);
	}

	@Override
	public void deleteAll(List<Module> toDelete){
		getHibernateTemplate().deleteAll(toDelete);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getModuleNames(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Module.class);
				criteria.setProjection(Projections.property(Module.NAME_COLUMN));
				return criteria.list();
			}
		};
		return (List<String>) getHibernateTemplate().execute(callback);
	}

}
