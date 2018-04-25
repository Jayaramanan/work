/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ObjectConnectionDAOImpl extends HibernateDaoSupport implements ObjectConnectionDAO{

	Logger log = Logger.getLogger(ObjectConnectionDAOImpl.class);

	public List<ObjectConnection> getObjectConnections(){
		return (List<ObjectConnection>) getHibernateTemplate().loadAll(ObjectConnection.class);
	}

	public void saveOrUpdate(ObjectConnection objectConnection){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		ObjectConnection mergedObjectConnection = (ObjectConnection) hibernateTemplate.merge(objectConnection);
		hibernateTemplate.saveOrUpdate(mergedObjectConnection);
	}

	@Override
	public void saveOrUpdateNoMerge(ObjectConnection objectConnection){
		getHibernateTemplate().saveOrUpdate(objectConnection);
	}

	public void saveOrUpdateAll(List<ObjectConnection> objectConnections){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.saveOrUpdateAll(objectConnections);
	}

	@Override
	public List<ObjectConnection> getConnectionsByConnectionType(final PredefinedAttribute ct){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectConnection.class);
				criteria.add(Restrictions.eq(ObjectConnection.CONNECTION_TYPE + ".id", ct.getId()));
				List<ObjectConnection> connections = criteria.list();
				return connections;
			}
		};
		return (List<ObjectConnection>) getHibernateTemplate().execute(callback);
	}

	public void delete(ObjectConnection objectConnection){
		getHibernateTemplate().delete(objectConnection);
	}

	public void deleteAll(List<ObjectConnection> objectConnections){
		getHibernateTemplate().deleteAll(objectConnections);
	}

	public List<ObjectConnection> getObjectConnections(final ObjectDefinition object){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectConnection.class);
				criteria.add(Restrictions.eq(ObjectConnection.OBJECT_ID, object.getId()));

				return criteria.list();
			}
		};
		return (List<ObjectConnection>) getHibernateTemplate().execute(callback);
	}

	public void deleteConnectionsByObject(final ObjectDefinition objectDefinition){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "delete ObjectConnection c where c." + ObjectConnection.OBJECT + " = :obj or c."
				        + ObjectConnection.FROM_OBJECT + " = :obj or c." + ObjectConnection.TO_OBJECT + " = :obj";
				Query query = session.createQuery(sql);
				query.setEntity("obj", objectDefinition);
				int count = query.executeUpdate();
				log.debug(count + " ObjectConnections deleted");
				return count;
			}
		};
		getHibernateTemplate().execute(callback);
	}

	public List<ObjectConnection> getConnectionsByObject(final ObjectDefinition objectDefinition){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectConnection.class);
				criteria.add(Restrictions.disjunction()
				        .add(Restrictions.eq(ObjectConnection.OBJECT + ".id", objectDefinition.getId()))
				        .add(Restrictions.eq(ObjectConnection.FROM_OBJECT + ".id", objectDefinition.getId()))
				        .add(Restrictions.eq(ObjectConnection.TO_OBJECT + ".id", objectDefinition.getId())));
				List<ObjectConnection> connections = criteria.list();
				log.debug(connections == null ? 0 : connections.size() + " ObjectConnections found");
				return connections;
			}
		};
		return (List<ObjectConnection>) getHibernateTemplate().execute(callback);
	}
}
