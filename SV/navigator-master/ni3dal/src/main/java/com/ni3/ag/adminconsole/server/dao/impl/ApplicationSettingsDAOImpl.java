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

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.server.dao.ApplicationSettingsDAO;

public class ApplicationSettingsDAOImpl extends HibernateDaoSupport implements ApplicationSettingsDAO{

	@Override
	public List<ApplicationSetting> getSettings(){
		return getHibernateTemplate().loadAll(ApplicationSetting.class);
	}

	@Override
	public void deleteSettings(List<ApplicationSetting> deletableApplicationSettings){
		getHibernateTemplate().deleteAll(deletableApplicationSettings);
	}

	@Override
	public void saveOrUpdate(List<ApplicationSetting> applicationSettings){
		getHibernateTemplate().saveOrUpdateAll(applicationSettings);
	}

	@Override
	public void saveOrUpdateAll(List<ApplicationSetting> settings){
		getHibernateTemplate().saveOrUpdateAll(settings);
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
