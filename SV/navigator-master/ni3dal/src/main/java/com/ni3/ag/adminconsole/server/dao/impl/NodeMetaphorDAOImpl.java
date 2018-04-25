/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.server.dao.NodeMetaphorDAO;

public class NodeMetaphorDAOImpl extends HibernateDaoSupport implements NodeMetaphorDAO{

	@Override
	@SuppressWarnings("unchecked")
	public List<Metaphor> getMetaphorsByIcons(final List<Icon> icons){
		if (icons.isEmpty())
			return new ArrayList<Metaphor>();
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Metaphor.class);
				criteria.add(Restrictions.in(Metaphor.ICON, icons));
				return criteria.list();
			}
		};
		return (List<Metaphor>) getHibernateTemplate().execute(callback);
	}

}
