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

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.server.dao.AttributeGroupDAO;

public class AttributeGroupDAOImpl extends HibernateDaoSupport implements AttributeGroupDAO{

	@SuppressWarnings("unchecked")
	public List<AttributeGroup> getAttributeGroups(final int attributeId){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(AttributeGroup.class);
				criteria.add(Restrictions.eq("objectAttribute.id", attributeId));
				return criteria.list();
			}
		};
		return (List<AttributeGroup>) getHibernateTemplate().execute(callback);
	}

	public void deleteAll(List<AttributeGroup> attrGroups){
		getHibernateTemplate().deleteAll(attrGroups);

	}

	public void updateAttributeGroups(List<AttributeGroup> attributeGroups){
		getHibernateTemplate().saveOrUpdateAll(attributeGroups);
	}

	public void updateAttributeGroup(AttributeGroup attributeGroup){
		getHibernateTemplate().saveOrUpdate(attributeGroup);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AttributeGroup> getAttributeGroupsByGroup(final Group group){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(AttributeGroup.class);
				criteria.add(Restrictions.eq("group.id", group.getId()));
				return criteria.list();
			}
		};
		return (List<AttributeGroup>) getHibernateTemplate().execute(callback);
	}

	@Override
	public AttributeGroup getAttributeGroup(final ObjectAttribute attr, final Group group){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(AttributeGroup.class);
				criteria.add(Restrictions.eq("objectAttribute.id", attr.getId()));
				criteria.add(Restrictions.eq("group.id", group.getId()));
				return criteria.uniqueResult();
			}
		};
		return (AttributeGroup) getHibernateTemplate().execute(callback);
	}
}
