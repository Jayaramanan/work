/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.UserDAO;

public class UserDAOImpl extends HibernateDaoSupport implements UserDAO{

	public User getUser(final String userName, final String password){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.eq("password", password)).add(Restrictions.eq("userName", userName).ignoreCase());
				return criteria.uniqueResult();
			}
		};
		return (User) getHibernateTemplate().execute(callback);
	}

	public User getById(int id){
		return (User) getHibernateTemplate().get(User.class, id);
	}

	public User saveOrUpdate(final User user){
		getHibernateTemplate().saveOrUpdate(user);
		return user;
	}

	public void saveOrUpdateAll(List<User> users){
		getHibernateTemplate().saveOrUpdateAll(users);
	}

	public void deleteAll(List<User> users){
		getHibernateTemplate().deleteAll(users);
	}

	public Integer addUser(User user){
		return (Integer) getHibernateTemplate().save(user);
	}

	@SuppressWarnings("unchecked")
	public List<User> getUnassignedUsers(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.isEmpty("groups"));
				return criteria.list();
			}
		};
		return (List<User>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getUser(final String userName){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.eq("userName", userName));
				return criteria.list();
			}
		};
		List<User> list = (List<User>) getHibernateTemplate().execute(callback);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsers(){
		return (List<User>) getHibernateTemplate().loadAll(User.class);
	}

	@Override
	public void merge(User u){
		getHibernateTemplate().merge(u);
	}

	@Override
	public User getUserByEmail(final String eMail){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.eq("eMail", eMail));
				return criteria.uniqueResult();
			}
		};
		User user = (User) getHibernateTemplate().execute(callback);
		return user;
	}

	@Override
	public void redirectSequence(final String seqName, final int start){
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String fixSql = "alter sequence " + seqName + " restart with " + (start + 1);
				Query fixQuery = session.createSQLQuery(fixSql);
				fixQuery.executeUpdate();
				if ("seq_objectcount".equalsIgnoreCase(seqName)){
					fixSql = "update sys_sequence set seqno = " + start + " where name = 'ObjectCount'";
					fixQuery = session.createSQLQuery(fixSql);
					fixQuery.executeUpdate();
				}
				return null;
			}
		};
		getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getActiveAdministrators(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.eq("_active", 1));
				return criteria.list();
			}
		};
		return (List<User>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersByIds(final Integer[] ids){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(User.class);
				criteria.add(Restrictions.in(User.ID_PROPERTY, ids));
				return criteria.list();
			}
		};
		return (List<User>) getHibernateTemplate().execute(callback);
	}
}
