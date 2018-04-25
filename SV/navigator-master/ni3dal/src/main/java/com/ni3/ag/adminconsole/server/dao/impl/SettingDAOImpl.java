/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.server.dao.SettingDAO;

public class SettingDAOImpl extends HibernateDaoSupport implements SettingDAO{

	@Override
	public String getSetting(final Integer userId, final String section, final String prop){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select distinct value from sys_user_settings where userId = :userid"
				        + " and lower(section) = :section and lower(prop) = :prop";
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				query.addScalar("value", Hibernate.STRING);
				query.setInteger("userid", userId);
				query.setString("section", section.toLowerCase());
				query.setString("prop", prop.toLowerCase());
				return query.uniqueResult();
			}
		};
		return (String) getHibernateTemplate().execute(callback);
	}

	@Override
	public GroupSetting getGroupSetting(final Integer groupId, final String section, final String prop){

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(GroupSetting.class);
				criteria.add(Restrictions.eq("group.id", groupId));
				criteria.add(Restrictions.ilike("section", section));
				criteria.add(Restrictions.ilike("prop", prop));
				return criteria.uniqueResult();
			}
		};
		return (GroupSetting) getHibernateTemplate().execute(callback);
	}

	@Override
	public ApplicationSetting getApplicationSetting(final String section, final String prop){

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ApplicationSetting.class);
				criteria.add(Restrictions.ilike("section", section));
				criteria.add(Restrictions.ilike("prop", prop));
				return criteria.uniqueResult();
			}
		};
		return (ApplicationSetting) getHibernateTemplate().execute(callback);
	}
}
