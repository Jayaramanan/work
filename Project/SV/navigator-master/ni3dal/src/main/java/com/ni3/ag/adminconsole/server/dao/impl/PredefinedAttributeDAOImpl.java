/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.server.dao.PredefinedAttributeDAO;
import com.ni3.ag.adminconsole.util.IntParser;

public class PredefinedAttributeDAOImpl extends HibernateDaoSupport implements PredefinedAttributeDAO{

	@SuppressWarnings("unchecked")
	public List<PredefinedAttribute> getPredefinedAttributes(final ObjectAttribute attribute){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(PredefinedAttribute.class);
				criteria.add(Restrictions.eq(PredefinedAttribute.OBJECT_ATTRIBUTE_ID, attribute.getId()));
				return criteria.list();
			}
		};
		return (List<PredefinedAttribute>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	public List<PredefinedAttribute> getPredefinedAttributes(final Integer attributeId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(PredefinedAttribute.class);
				criteria.add(Restrictions.eq(PredefinedAttribute.OBJECT_ATTRIBUTE_ID, attributeId));
				return criteria.list();
			}
		};
		return (List<PredefinedAttribute>) getHibernateTemplate().execute(callback);
	}

	@Override
	public Collection<PredefinedAttribute> saveOrUpdateAll(Collection<PredefinedAttribute> update){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.saveOrUpdateAll(update);
		return update;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PredefinedAttribute> getPredefinedAttributes(){
		return getHibernateTemplate().loadAll(PredefinedAttribute.class);
	}

	@Override
	public PredefinedAttribute saveOrUpdate(PredefinedAttribute attr){
		getHibernateTemplate().saveOrUpdate(attr);
		return attr;
	}

	@Override
	public PredefinedAttribute getById(Integer id){
		return (PredefinedAttribute) getHibernateTemplate().get(PredefinedAttribute.class, id);
	}

	@Override
	public PredefinedAttribute getPredefinedAttributeByValue(final ObjectAttribute attr, final String value){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(PredefinedAttribute.class);
				criteria.add(Restrictions.eq(PredefinedAttribute.OBJECT_ATTRIBUTE_ID, attr.getId()));
				criteria.add(Restrictions.eq(PredefinedAttribute.VALUE, value));
				criteria.setMaxResults(1);
				return criteria.uniqueResult();
			}
		};
		return (PredefinedAttribute) getHibernateTemplate().execute(callback);
	}

	@Override
	public Object merge(Object entity){
		return getHibernateTemplate().merge(entity);
	}

	@Override
	public PredefinedAttribute getPredefinedAttributeByLabel(final ObjectAttribute attr, final String label){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(PredefinedAttribute.class);
				criteria.add(Restrictions.eq(PredefinedAttribute.OBJECT_ATTRIBUTE_ID, attr.getId()));
				criteria.add(Restrictions.eq(PredefinedAttribute.LABEL, label));
				criteria.setMaxResults(1);
				return criteria.uniqueResult();
			}
		};
		return (PredefinedAttribute) getHibernateTemplate().execute(callback);
	}

	@Override
	public boolean isUsedInUserTable(final PredefinedAttribute pa){
		final ObjectAttribute attr = pa.getObjectAttribute();
		final StringBuilder sql = new StringBuilder();
		sql.append("select count(id) from ").append(attr.getInTable());
		sql.append(" where ").append(attr.getName());
		if (attr.getIsMultivalue()){
			sql.append(" like '%{").append(pa.getId()).append("}%'");
		} else{
			sql.append(" = ").append(pa.getId());
		}
		final HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString());
				return query.uniqueResult();
			}
		};
		final Object cnt = getHibernateTemplate().execute(callback);
		final Integer count = IntParser.getInt(cnt, 0);
		return count > 0;
	}

	@Override
	public void updateValuesInUserTable(final PredefinedAttribute pa, final Integer newValue){
		final String sql = getUpdateValueSql(pa, newValue);

		final HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				return query.executeUpdate();
			}
		};
		getHibernateTemplate().execute(callback);
	}

	String getUpdateValueSql(final PredefinedAttribute pa, final Integer newValue){
		final ObjectAttribute attr = pa.getObjectAttribute();
		final StringBuilder sql = new StringBuilder();
		sql.append("update ").append(attr.getInTable());
		sql.append(" set ").append(attr.getName());
		if (attr.getIsMultivalue()){
			sql.append(" = replace(").append(attr.getName()).append(", '{").append(pa.getId()).append("}','");
			if (newValue != null){
				sql.append("{").append(newValue).append("}");
			}
			sql.append("')");
		} else{
			sql.append(" = ").append(newValue);
		}
		sql.append(" where ").append(attr.getName());
		if (attr.getIsMultivalue()){
			sql.append(" like '%{").append(pa.getId()).append("}%'");
		} else{
			sql.append(" = ").append(pa.getId());
		}
		return sql.toString();
	}
}
