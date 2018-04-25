/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.DeltaHeader;
import com.ni3.ag.adminconsole.domain.SyncStatus;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.DeltaHeaderDAO;

public class DeltaHeaderDAOImpl extends HibernateDaoSupport implements DeltaHeaderDAO{

	@Override
	public Integer getCountByUser(final User user){
		final HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(final Session session) throws HibernateException, SQLException{
				final Criteria criteria = session.createCriteria(DeltaHeader.class);
				criteria.add(Restrictions.eq(DeltaHeader.COLUMN_CREATOR, user.getId()));
				criteria.setProjection(Projections.count(DeltaHeader.COLUMN_ID));
				return criteria.uniqueResult();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

	@Override
	public Integer getUnprocessedCount(){
		final HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(final Session session) throws HibernateException, SQLException{
				final Criteria criteria = session.createCriteria(DeltaHeader.class);
				criteria.add(Restrictions.or(Restrictions.eq(DeltaHeader.COLUMN_STATUS, SyncStatus.New), Restrictions
				        .isNull(DeltaHeader.COLUMN_STATUS)));
				criteria.setProjection(Projections.count(DeltaHeader.COLUMN_ID));
				return criteria.uniqueResult();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

	@Override
	public DeltaHeader load(final Long id){
		final HibernateTemplate hibernateTemplate = getHibernateTemplate();
		return (DeltaHeader) hibernateTemplate.load(DeltaHeader.class, id);
	}

	@Override
	public void saveOrUpdate(final DeltaHeader header){
		getHibernateTemplate().saveOrUpdate(header);
	}

	@Override
	public void saveOrUpdateAll(List<DeltaHeader> deltaHeaders){
		getHibernateTemplate().saveOrUpdateAll(deltaHeaders);
	}

}
