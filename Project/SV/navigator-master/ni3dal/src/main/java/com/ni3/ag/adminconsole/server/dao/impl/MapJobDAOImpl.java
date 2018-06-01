/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.server.dao.MapJobDAO;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;

public class MapJobDAOImpl extends HibernateDaoSupport implements MapJobDAO{

	@Override
	public void saveOrUpdate(MapJob job){
		getHibernateTemplate().saveOrUpdate(job);
	}

	@Override
	public MapJob merge(MapJob job){
		return (MapJob) getHibernateTemplate().merge(job);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MapJob> getAllJobs(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(MapJob.class);
				criteria.addOrder(Order.asc(MapJob.STATUS)).addOrder(Order.desc(MapJob.TIME_START));
				return criteria.list();
			}
		};
		return (List<MapJob>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void delete(MapJob job){
		getHibernateTemplate().delete(job);
	}

	@Override
	public MapJob getMapJob(Integer id){
		return (MapJob) getHibernateTemplate().get(MapJob.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MapJob> getScheduledMapJobs(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(MapJob.class);
				criteria.add(Restrictions.eq(MapJob.STATUS, MapJobStatus.Scheduled.getValue()));
				criteria.add(Restrictions.or(Restrictions.isNull(MapJob.TIME_START),
				        Restrictions.le(MapJob.TIME_START, new Date())));
				return criteria.list();
			}
		};
		return (List<MapJob>) getHibernateTemplate().execute(callback);
	}

	@Override
	public Map getMap(Integer mapId){
		return (Map) getHibernateTemplate().get(Map.class, mapId);
	}

	@Override
	public void saveAndFlush(MapJob job){
		getHibernateTemplate().saveOrUpdate(job);
		getHibernateTemplate().flush();
	}
}
