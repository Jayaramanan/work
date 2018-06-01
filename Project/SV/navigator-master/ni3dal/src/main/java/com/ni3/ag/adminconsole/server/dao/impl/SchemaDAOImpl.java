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

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;

public class SchemaDAOImpl extends HibernateDaoSupport implements SchemaDAO{

	@SuppressWarnings("unchecked")
	public List<Schema> getSchemas(){
		return (List<Schema>) getHibernateTemplate().loadAll(Schema.class);
	}

	@SuppressWarnings("unchecked")
	public List<Schema> getSchemasWithNodesAndInMetaphor(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				session.enableFilter(ObjectDefinition.IN_METAPHOR_FILTER);
				session.enableFilter(ObjectDefinition.WITH_OBJECT_TYPE_FILTER).setParameter(
				        ObjectDefinition.OBJECT_TYPE_PARAM, ObjectType.NODE.toInt());
				Criteria criteria = session.createCriteria(Schema.class);

				return criteria.list();
			}
		};
		return (List<Schema>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Schema> getSchemasWithEdges(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				session.enableFilter(ObjectDefinition.WITH_OBJECT_TYPE_FILTER).setParameterList(
				        ObjectDefinition.OBJECT_TYPE_PARAM,
				        new Object[] { ObjectType.EDGE.toInt(), ObjectType.CONTEXT_EDGE.toInt() });
				Criteria criteria = session.createCriteria(Schema.class);

				return criteria.list();
			}
		};
		return (List<Schema>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Schema> getSchemasWithNodes(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				session.enableFilter(ObjectDefinition.WITH_OBJECT_TYPE_FILTER).setParameter(
				        ObjectDefinition.OBJECT_TYPE_PARAM, ObjectType.NODE.toInt());
				Criteria criteria = session.createCriteria(Schema.class);

				return criteria.list();
			}
		};
		return (List<Schema>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Schema> getSchemasWithPredefinedAttributes(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(Schema.class);
				session.enableFilter(ObjectAttribute.PREDEFINED);
				return criteria.list();
			}
		};
		return (List<Schema>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Schema getSchemaByName(final String value){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Schema.class);
				criteria.add(Restrictions.in(Schema.SCHEMA_NAME_DB_COLUMN, new Object[] { value }));
				return criteria.list();
			}
		};
		List<Schema> list = (List<Schema>) getHibernateTemplate().execute(callback);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	public Schema getSchema(Integer schemaID){
		return (Schema) getHibernateTemplate().load(Schema.class, schemaID);
	}

	@Override
	public Schema saveOrUpdate(Schema schema){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.saveOrUpdate(schema);
		return schema;
	}

	@Override
	public Schema save(Schema schema){
		getHibernateTemplate().persist(schema);
		return schema;
	}

	@Override
	public Schema merge(Schema schema){
		return (Schema) getHibernateTemplate().merge(schema);
	}

	@Override
	public void deleteSchema(Schema schema){
		getHibernateTemplate().delete(schema);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Schema> getSchemasByUser(final User u){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(Schema.class);
				criteria.add(Restrictions.eq(Schema.CREATED_BY, u));
				return criteria.list();
			}
		};
		return (List<Schema>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdateAll(List<Schema> schemas){
		getHibernateTemplate().saveOrUpdateAll(schemas);
	}

	@Override
	public void evictAll(List<Schema> schemas){
		for (Schema s : schemas){
			getHibernateTemplate().evict(s);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Schema> getSchemasWithAggregableAttributes(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(Schema.class);
				session.enableFilter(ObjectAttribute.NUMERIC_ATTRIBUTES_FILTER);
				return criteria.list();
			}
		};
		return (List<Schema>) getHibernateTemplate().execute(callback);
	}

}
