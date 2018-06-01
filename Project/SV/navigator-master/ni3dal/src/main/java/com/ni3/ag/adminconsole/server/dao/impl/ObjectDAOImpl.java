/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.CisObject;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectStatus;
import com.ni3.ag.adminconsole.server.dao.ObjectDAO;

public class ObjectDAOImpl extends HibernateDaoSupport implements ObjectDAO{
	private static final Logger log = Logger.getLogger(ObjectDAOImpl.class);

	@Override
	public CisObject get(Integer id){
		return (CisObject) getHibernateTemplate().get(CisObject.class, id);
	}

	@Override
	public void delete(CisObject object){
		getHibernateTemplate().delete(object);
	}

	@Override
	public Object getData(final String sql, final Object[] params){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				log.debug("Try execute: " + sql);
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				for (int i = 0; i < params.length; i++){
					query.setParameter(i, params[i]);
				}
				return query.list();
			}
		};
		return getHibernateTemplate().execute(callback);
	}

	@Override
	public void executeUpdate(final String sql, final Object[] params, final DataType[] types){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				for (int i = 0; i < params.length; i++){
					query.setParameter(i, params[i], getType(types[i]));
				}
				return query.executeUpdate();
			}
		};
		getHibernateTemplate().execute(callback);
	}

	@Override
	@SuppressWarnings("deprecation")
	public Connection getConnection(){
		return getSession().connection();
	}

	@Override
	public int getMaxIdForRange(final int userRangeStart, final int userRangeEnd){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(CisObject.class);
				c.add(Restrictions.between(CisObject.ID, userRangeStart, userRangeEnd));
				c.setProjection(Projections.max(CisObject.ID));
				return c.list();
			}
		};
		List<?> l = (List<?>) getHibernateTemplate().execute(callback);
		if (l == null || l.size() == 0)
			return userRangeStart;
		else if (l.get(0) == null)
			return userRangeStart;
		else
			return (Integer) l.get(0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getIDsForUserTable(final String tableName){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select ut.id, ut.srcid from " + tableName
						+ " ut inner join cis_objects o on (ut.id = o.id and o.status in (" + ObjectStatus.Normal.toInt()
						+ "," + ObjectStatus.Locked.toInt() + "))";
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				return query.list();
			}
		};
		return (List<Object[]>) getHibernateTemplate().execute(callback);
	}

	// used by API
	@Override
	public int executeUpdate(final String updateSql){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(updateSql);
				return query.executeUpdate();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

	// used by API
	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getEdgesByNode(final int nodeId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select distinct id from cis_edges where fromid = ? or toid = ?";
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				query.setInteger(0, nodeId);
				query.setInteger(1, nodeId);
				return query.list();
			}
		};
		return (List<Integer>) getHibernateTemplate().execute(callback);
	}

	private Type getType(DataType dt){
		Type result = Hibernate.STRING;
		switch (dt){
			case INT:
				result = Hibernate.INTEGER;
				break;
			case DECIMAL:
				result = Hibernate.DOUBLE;
				break;
		}
		return result;
	}
}
