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

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.server.dao.LicenseDAO;

public class LicenseDAOImpl extends HibernateDaoSupport implements LicenseDAO{

	@SuppressWarnings("unchecked")
	@Override
	public List<License> getLicenses(){
		return getHibernateTemplate().loadAll(License.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<License> getLicenseByProduct(final String product){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(License.class);
				criteria.add(Restrictions.eq(License.PRODUCT_DB_COLUMN_NAME, product));
				return criteria.list();
			}
		};
		return (List<License>) getHibernateTemplate().execute(callback);
	}

	@Override
	public License merge(License l){
		return (License) getHibernateTemplate().merge(l);
	}

	@Override
	public void saveOrUpdateAll(List<License> licenses){
		getHibernateTemplate().saveOrUpdateAll(licenses);
	}

	@Override
	public void delete(License license){
		getHibernateTemplate().delete(license);
	}

	@Override
	public License getLicense(License license){
		return (License) getHibernateTemplate().get(License.class, license.getId());
	}

	@Override
	public License saveOrUpdate(License license){
		getHibernateTemplate().persist(license);
		return license;
	}

	@Override
	public void deleteAll(List<License> licenses){
		getHibernateTemplate().deleteAll(licenses);
	}

}
