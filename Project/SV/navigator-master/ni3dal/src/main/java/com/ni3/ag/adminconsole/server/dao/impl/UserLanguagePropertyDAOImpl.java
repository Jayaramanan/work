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

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.server.dao.UserLanguagePropertyDAO;

public class UserLanguagePropertyDAOImpl extends HibernateDaoSupport implements UserLanguagePropertyDAO{

	public List<UserLanguageProperty> getPropertiesByName(final String name){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(UserLanguageProperty.class);
				criteria.add(Restrictions.eq(UserLanguageProperty.PROPERTY, name));
				List<UserLanguageProperty> properties = criteria.list();
				return properties;
			}
		};
		return (List<UserLanguageProperty>) getHibernateTemplate().execute(callback);
	}

	public void deleteAll(List<UserLanguageProperty> properties){
		getHibernateTemplate().deleteAll(properties);
	}

	public List<UserLanguageProperty> getPropertiesByLanguage(final Language language){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(UserLanguageProperty.class);
				criteria.add(Restrictions.eq(UserLanguageProperty.LANGUAGE, language));
				List<UserLanguageProperty> properties = criteria.list();
				return properties;
			}
		};
		return (List<UserLanguageProperty>) getHibernateTemplate().execute(callback);
	}

	public UserLanguageProperty getProperty(final String name, final Language language){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(UserLanguageProperty.class);
				criteria.add(Restrictions.eq(UserLanguageProperty.LANGUAGE, language)).add(
				        Restrictions.eq(UserLanguageProperty.PROPERTY, name));
				UserLanguageProperty property = (UserLanguageProperty) criteria.uniqueResult();
				return property;
			}
		};
		return (UserLanguageProperty) getHibernateTemplate().execute(callback);
	}
}
