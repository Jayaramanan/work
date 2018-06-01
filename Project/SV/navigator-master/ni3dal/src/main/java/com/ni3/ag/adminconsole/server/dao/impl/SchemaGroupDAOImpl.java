/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.server.dao.SchemaGroupDAO;

public class SchemaGroupDAOImpl extends HibernateDaoSupport implements SchemaGroupDAO{

	@Override
	public SchemaGroup getSchemaGroup(final Schema schema, final Group group){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(SchemaGroup.class);
				criteria.add(Restrictions.eq("group.id", group.getId()));
				criteria.add(Restrictions.eq("schema.id", schema.getId()));
				return criteria.uniqueResult();
			}
		};
		return (SchemaGroup) getHibernateTemplate().execute(callback);
	}

	@Override
	public void updateSchemaGroup(SchemaGroup sg){
		getHibernateTemplate().saveOrUpdate(sg);

	}

}
