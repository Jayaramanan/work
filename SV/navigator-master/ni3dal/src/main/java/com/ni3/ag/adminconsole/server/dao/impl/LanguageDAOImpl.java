/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.server.dao.LanguageDAO;

public class LanguageDAOImpl extends HibernateDaoSupport implements LanguageDAO{

	@SuppressWarnings("unchecked")
	public List<Language> getLanguages(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Language.class);
				criteria.addOrder(Order.asc("id"));
				return criteria.list();
			}
		};
		return (List<Language>) getHibernateTemplate().execute(callback);
	}

	public Language saveOrUpdate(Language language){
		getHibernateTemplate().saveOrUpdate(language);
		return language;
	}

	public Language getLanguage(int languageID){
		return (Language) getHibernateTemplate().get(Language.class, languageID);
	}

	public void deleteLanguage(Language language){
		getHibernateTemplate().delete(language);
	}

	public Language getByName(final String name){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Language.class);
				criteria.add(Restrictions.eq(Language.NAME, name));
				return criteria.uniqueResult();
			}
		};
		return (Language) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdateAll(List<Language> languages){
		getHibernateTemplate().saveOrUpdateAll(languages);
	}

}
