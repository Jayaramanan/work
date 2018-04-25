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

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.server.dao.IconDAO;

public class IconDAOImpl extends HibernateDaoSupport implements IconDAO{

	@Override
	public Integer save(Icon icon){
		return (Integer) getHibernateTemplate().save(icon);
	}

	@Override
	public List<Icon> loadAll(){
		return (List<Icon>) getHibernateTemplate().loadAll(Icon.class);
	}

	@Override
	public void delete(Icon icon){
		getHibernateTemplate().delete(icon);
	}

	@Override
	public void saveAll(List<Icon> icons){
		getHibernateTemplate().saveOrUpdateAll(icons);
	}

	@Override
	public Icon getIconByName(final String iconAttr){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Icon.class);
				criteria.add(Restrictions.in(Icon.NAME_DB_COLUMN, new Object[] { iconAttr }));
				return criteria.list();
			}
		};
		List<Icon> list = (List<Icon>) getHibernateTemplate().execute(callback);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	public void deleteAll(List<Icon> iconsToDelete){
		getHibernateTemplate().deleteAll(iconsToDelete);
	}

}
