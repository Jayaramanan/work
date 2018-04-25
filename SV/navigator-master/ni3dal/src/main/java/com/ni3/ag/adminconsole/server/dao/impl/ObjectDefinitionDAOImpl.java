/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ObjectDefinitionDAOImpl extends HibernateDaoSupport implements ObjectDefinitionDAO{

	@SuppressWarnings("unchecked")
	public List<ObjectDefinition> getObjectDefinitions(){
		return (List<ObjectDefinition>) getHibernateTemplate().loadAll(ObjectDefinition.class);
	}

	public ObjectDefinition getObjectDefinition(int id){
		return (ObjectDefinition) getHibernateTemplate().load(ObjectDefinition.class, id);
	}

	public ObjectDefinition saveOrUpdate(final ObjectDefinition objectDefinition){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.saveOrUpdate(objectDefinition);
		return objectDefinition;
	}

	public void deleteObject(ObjectDefinition objectToDelete){
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		hibernateTemplate.delete(objectToDelete);
	}

	public ObjectDefinition save(ObjectDefinition clone){
		getHibernateTemplate().persist(clone);
		return clone;
	}

	@SuppressWarnings("unchecked")
	public List<ObjectDefinition> getNodeLikeObjectDefinitions(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions
						.in(ObjectDefinition.OBJECT_TYPE_PROPERTY, new Object[]{ObjectType.NODE.toInt()}));
				return criteria.list();
			}
		};
		return (List<ObjectDefinition>) getHibernateTemplate().execute(callback);
	}

	public ObjectDefinition merge(ObjectDefinition od){
		return (ObjectDefinition) getHibernateTemplate().merge(od);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ObjectDefinition getObjectDefinitionByName(final String objectName, final Integer schemaId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions.eq(ObjectDefinition.SCHEMA + ".id", schemaId));
				criteria.add(Restrictions.like(ObjectDefinition.OBJECT_NAME_DB_COLUMN, objectName));
				return criteria.list();
			}
		};
		List<ObjectDefinition> list = (List<ObjectDefinition>) getHibernateTemplate().execute(callback);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectDefinition> getObjectDefinitionsByUser(final User u){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions.eq(ObjectDefinition.CREATED_BY, u));
				return criteria.list();
			}
		};
		return (List<ObjectDefinition>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectDefinition> getSchemaEdgeLikeObjects(final Integer schemaId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions.in(ObjectDefinition.OBJECT_TYPE_PROPERTY, new Object[]{ObjectType.EDGE,
						ObjectType.CONTEXT_EDGE}));
				criteria.add(Restrictions.eq(ObjectDefinition.SCHEMA + ".id", schemaId));
				return criteria.list();
			}
		};
		return (List<ObjectDefinition>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectDefinition> getSchemaNodeLikeObjects(final Integer schemaId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions
						.in(ObjectDefinition.OBJECT_TYPE_PROPERTY, new Object[]{ObjectType.NODE.toInt()}));
				criteria.add(Restrictions.eq(ObjectDefinition.SCHEMA + ".id", schemaId));
				return criteria.list();
			}
		};
		return (List<ObjectDefinition>) getHibernateTemplate().execute(callback);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectDefinition> getNodeObjectsWithNotFixedAttributes(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{

				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions
						.in(ObjectDefinition.OBJECT_TYPE_PROPERTY, new Object[]{ObjectType.NODE.toInt()}));
				session.enableFilter(ObjectAttribute.NOT_FIXED_NODE_ATTRIBUTE_FILTER);
				return criteria.list();
			}
		};
		return (List<ObjectDefinition>) getHibernateTemplate().execute(callback);
	}

	@Override
	public ObjectDefinition getObjectDefinitionWithInMetaphor(final Integer id){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				session.enableFilter(ObjectDefinition.IN_METAPHOR_FILTER);
				Criteria criteria = session.createCriteria(ObjectDefinition.class);
				criteria.add(Restrictions.eq(ObjectDefinition.ID, id));
				return criteria.uniqueResult();
			}
		};
		return (ObjectDefinition) getHibernateTemplate().execute(callback);
	}

	@Override
	public void deleteObjectChartsByObject(final ObjectDefinition object){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String str = "delete ObjectChart oc where oc.object = :obj";
				Query query = session.createQuery(str);
				query.setEntity("obj", object);
				int count = query.executeUpdate();
				return count;
			}
		};
		getHibernateTemplate().execute(callback);
	}

	@Override
	public void evict(ObjectDefinition od){
		getHibernateTemplate().evict(od);
	}
}
