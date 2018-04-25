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

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.server.dao.UserActivityDAO;

public class UserActivityDAOImpl extends HibernateDaoSupport implements UserActivityDAO{

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersWithActivities(final Date from, final Date to, final User user){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				if (from != null)
					session.enableFilter(User.DATE_TIME_GREATER_FILTER).setParameter(UserActivity.DATE_TIME_PROPERTY, from);
				if (to != null)
					session.enableFilter(User.DATE_TIME_LESS_FILTER).setParameter(UserActivity.DATE_TIME_PROPERTY, to);
				session.enableFilter(UserActivity.REMOVE_NOT_LOG_ACTIVITIES_FILTER);

				Criteria criteria = session.createCriteria(User.class);
				if (user != null)
					criteria.add(Restrictions.eq(User.ID_PROPERTY, user.getId()));
				criteria.addOrder(Order.asc(User.USERNAME_PROPERTY));
				return criteria.list();
			}
		};
		return (List<User>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserActivity> getActionsWithUsers(final Date from, final Date to, final UserActivityType activity){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				if (from != null)
					session.enableFilter(UserActivity.DATE_TIME_GREATER_FILTER).setParameter(
							UserActivity.DATE_TIME_PROPERTY, from);
				if (to != null)
					session.enableFilter(UserActivity.DATE_TIME_LESS_FILTER).setParameter(UserActivity.DATE_TIME_PROPERTY,
							to);

				Criteria criteria = session.createCriteria(UserActivity.class);
				criteria.add(Restrictions.ne(UserActivity.ACTIVITY_TYPE_PROPERTY, UserActivity.NOT_A_LOG_ACTIVITY));
				criteria.add(Restrictions.gt(UserActivity.USER_PROPERTY + ".id", 0));
				if (activity != null)
					criteria.add(Restrictions.eq(UserActivity.ACTIVITY_TYPE_PROPERTY, activity.getValueText()));
				criteria.addOrder(Order.asc(UserActivity.DATE_TIME));
				return criteria.list();
			}
		};
		return (List<UserActivity>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdateAll(List<UserActivity> activitiesToUpdate){
		getHibernateTemplate().saveOrUpdateAll(activitiesToUpdate);
	}

}
