/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;

public class ObjectAttributeDAOImpl extends HibernateDaoSupport implements ObjectAttributeDAO{

	public void saveOrUpdate(ObjectAttribute oa){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.saveOrUpdate(oa);
	}

	public ObjectAttribute getObjectAttribute(int id){
		return (ObjectAttribute) getHibernateTemplate().load(ObjectAttribute.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ObjectAttribute getObjectAttributeByName(final String colName, final Integer objectId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectAttribute.class);
				criteria.add(Restrictions.in(ObjectAttribute.NAME_DB_COLUMN, new Object[] { colName }));
				criteria.add(Restrictions.eq(ObjectAttribute.OBJECT_DEFINITION + ".id", objectId));
				return criteria.list();
			}
		};
		List<ObjectAttribute> list = (List<ObjectAttribute>) getHibernateTemplate().execute(callback);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectAttribute> getPredefinedObjectAttributes(final ObjectDefinition object){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectAttribute.class);
				criteria.add(Restrictions.in(ObjectAttribute.PREDEFINED_DB_COLUMN, new Object[] { Formula.PREDEFINED,
				        Formula.FORMULA_PREDEFINED }));
				criteria.add(Restrictions.eq(ObjectAttribute.OBJECT_DEFINITION + ".id", object.getId()));
				return criteria.list();
			}
		};
		return (List<ObjectAttribute>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdateAll(List<ObjectAttribute> oaList){
		getHibernateTemplate().saveOrUpdateAll(oaList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectAttribute> getObjectAttributesWithFormulas(final ObjectDefinition od){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectAttribute.class);
				criteria.add(Restrictions.in(ObjectAttribute.PREDEFINED_DB_COLUMN, new Object[] { Formula.FORMULA_BASED,
				        Formula.FORMULA_PREDEFINED }));
				criteria.add(Restrictions.eq(ObjectAttribute.OBJECT_DEFINITION + ".id", od.getId()));
				return criteria.list();
			}
		};
		return (List<ObjectAttribute>) getHibernateTemplate().execute(callback);
	}

	@Override
	public ObjectAttribute merge(ObjectAttribute oa){
		return (ObjectAttribute) getHibernateTemplate().merge(oa);
	}

}
