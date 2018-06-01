/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.OfflineJobDAO;
import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;

public class OfflineJobDAOImpl extends HibernateDaoSupport implements OfflineJobDAO{

	@SuppressWarnings("unchecked")
	@Override
	public List<OfflineJob> getScheduledExportJobs(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(OfflineJob.class);
				criteria.add(Restrictions.in(OfflineJob.STATUS, new Integer[] { OfflineJobStatus.Scheduled.getValue(),
				        OfflineJobStatus.ScheduledWaiting.getValue() }));
				criteria.add(Restrictions.or(Restrictions.isNull(OfflineJob.TIME_START),
				        Restrictions.le(OfflineJob.TIME_START, new Date())));
				criteria.addOrder(Order.asc(OfflineJob.TIME_START));
				criteria.setMaxResults(1);
				return criteria.list();
			}
		};
		return (List<OfflineJob>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdate(OfflineJob job){
		getHibernateTemplate().saveOrUpdate(job);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OfflineJob> getAllJobs(){
		return (List<OfflineJob>) getHibernateTemplate().loadAll(OfflineJob.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getThickClientUsers(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.eq(User.HAS_OFFLINE_CLIENT, new Integer(1)));
				return criteria.list();
			}
		};
		return (List<User>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void delete(OfflineJob job){
		getHibernateTemplate().delete(job);
	}

	@Override
	public OfflineJob getOfflineJob(Integer id){
		return (OfflineJob) getHibernateTemplate().get(OfflineJob.class, id);
	}

	@Override
	public void saveAndFlush(OfflineJob job){
		getHibernateTemplate().saveOrUpdate(job);
		getHibernateTemplate().flush();
	}

	@Override
	public OfflineJob merge(OfflineJob job){
		return (OfflineJob) getHibernateTemplate().merge(job);
	}

	@Override
	public Integer getJobCountByUser(final User u){
		HibernateCallback callback = new HibernateCallback(){

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(OfflineJob.class);
				Criterion leftBorderMatch = Restrictions.like(OfflineJob.USER_ID, u.getId() + OfflineJob.USER_ID_SEPARATOR
				        + "%");
				Criterion rightBorderMatch = Restrictions.like(OfflineJob.USER_ID,
				        "%" + OfflineJob.USER_ID_SEPARATOR + u.getId());
				Criterion borderMatch = Restrictions.or(leftBorderMatch, rightBorderMatch);
				Criterion exactMatch = Restrictions.like(OfflineJob.USER_ID, "" + u.getId());
				Criterion patternMatch = Restrictions.or(
				        borderMatch,
				        Restrictions.like(OfflineJob.USER_ID, "%" + OfflineJob.USER_ID_SEPARATOR + u.getId()
				                + OfflineJob.USER_ID_SEPARATOR + "%"));
				Criterion exactOrPatternMatch = Restrictions.or(exactMatch, patternMatch);
				criteria.add(exactOrPatternMatch);
				criteria.setProjection(Projections.count(OfflineJob.ID));
				return criteria.uniqueResult();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group> getGroupsWithOfflineUsers(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				session.enableFilter(Group.USERS_WITH_OFFLINE_CLIENT_FILTER);
				Criteria criteria = session.createCriteria(Group.class);
				criteria.add(Restrictions.isNotEmpty(Group.USERS_PROPERTY));
				return criteria.list();
			}
		};
		return (List<Group>) getHibernateTemplate().execute(callback);
	}
}
