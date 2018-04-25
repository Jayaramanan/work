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

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;

public class GroupDAOImpl extends HibernateDaoSupport implements GroupDAO{

	@SuppressWarnings("unchecked")
	public List<Group> getGroups(){
		return getHibernateTemplate().loadAll(Group.class);
	}

	public Group addGroup(Group group){
		getHibernateTemplate().save(group);
		return group;
	}

	public void deleteGroup(Group group){
		getHibernateTemplate().delete(group);
	}

	public Group getGroup(Integer id){
		return (Group) getHibernateTemplate().load(Group.class, id);
	}

	public void saveOrUpdate(Group group){
		getHibernateTemplate().saveOrUpdate(group);
	}

	public void update(Group group){
		getHibernateTemplate().update(group);
	}

	@Override
	public Group getGroupByName(final String name){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Group.class);
				criteria.add(Restrictions.eq(Group.NAME_DB_COLUMN, name));
				return criteria.uniqueResult();
			}
		};
		return (Group) getHibernateTemplate().execute(callback);
	}

	@Override
	public void deleteGroupScope(GroupScope scope){
		getHibernateTemplate().delete(scope);
	}

	@Override
	public void saveOrUpdateAll(List<Group> groups){
		getHibernateTemplate().saveOrUpdateAll(groups);
	}

}
