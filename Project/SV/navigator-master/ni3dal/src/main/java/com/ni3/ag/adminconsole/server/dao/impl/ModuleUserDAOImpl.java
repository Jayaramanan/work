/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ModuleUserDAO;

public class ModuleUserDAOImpl extends HibernateDaoSupport implements ModuleUserDAO{

	@Override
	public ModuleUser getModuleUser(Integer id){
		return (ModuleUser) getHibernateTemplate().load(ModuleUser.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ModuleUser> getModuleUsers(){
		return getHibernateTemplate().loadAll(ModuleUser.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ModuleUser> getByUser(final User u){
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(ModuleUser.class);
				c.add(Restrictions.eq("user", u));
				return c.list();
			}
		};
		return (List<ModuleUser>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdate(ModuleUser mu){
		getHibernateTemplate().saveOrUpdate(mu);
	}

	@Override
	public void saveOrUpdateAll(List<ModuleUser> mus){
		getHibernateTemplate().saveOrUpdateAll(mus);
	}

}
