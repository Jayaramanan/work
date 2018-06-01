/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.server.dao.ObjectGroupDAO;

public class ObjectGroupDAOImpl extends HibernateDaoSupport implements ObjectGroupDAO{

	Logger log = Logger.getLogger(ObjectGroupDAOImpl.class);

	public void deleteGroupsByObject(final ObjectDefinition object){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Query query = session.createQuery("delete ObjectGroup og where og." + ObjectGroup.OBJECT + " = ?");
				query.setEntity(0, object);
				int count = query.executeUpdate();
				log.debug(count + " ObjectGroups deleted");
				return count;
			}
		};
		getHibernateTemplate().execute(callback);
	}

	public void updateObjectGroups(List<ObjectGroup> objectGroups){
		getHibernateTemplate().saveOrUpdateAll(objectGroups);
	}

	public void deleteGroupsByGroup(final Group group){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Query query = session.createQuery("delete ObjectGroup og where og." + ObjectGroup.GROUP + " = ?");
				query.setEntity(0, group);
				int count = query.executeUpdate();
				log.debug(count + " ObjectGroups deleted");
				return count;
			}
		};
		getHibernateTemplate().execute(callback);
	}

	@Override
	public void updateObjectGroup(ObjectGroup objectGroup){
		getHibernateTemplate().saveOrUpdate(objectGroup);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectGroup> getByGroup(final Group group){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectGroup.class);
				criteria.add(Restrictions.eq("group.id", group.getId()));
				return criteria.list();
			}
		};
		return (List<ObjectGroup>) getHibernateTemplate().execute(callback);
	}

	@Override
	public ObjectGroup getObjectGroup(final ObjectDefinition od, final Group group){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectGroup.class);
				criteria.add(Restrictions.eq("group.id", group.getId()));
				criteria.add(Restrictions.eq("object.id", od.getId()));
				return criteria.uniqueResult();
			}
		};
		return (ObjectGroup) getHibernateTemplate().execute(callback);
	}

}
