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

import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.server.dao.LineWeightDAO;

public class LineWeightDAOImpl extends HibernateDaoSupport implements LineWeightDAO{

	public List<LineWeight> getLineWeights(){
		return (List<LineWeight>) getHibernateTemplate().loadAll(LineWeight.class);
	}

	@Override
	public LineWeight getDefaultLineWeight(){
		return (LineWeight) getHibernateTemplate().load(LineWeight.class, new Integer(0));
	}

	@Override
	public LineWeight getLineWeightByName(final String name){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(LineWeight.class);
				criteria.add(Restrictions.eq(LineWeight.LABEL_DB_COLUMN, name));
				return criteria.uniqueResult();
			}
		};
		return (LineWeight) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdateAll(List<LineWeight> lineWeights){
		getHibernateTemplate().saveOrUpdateAll(lineWeights);
	}

}
