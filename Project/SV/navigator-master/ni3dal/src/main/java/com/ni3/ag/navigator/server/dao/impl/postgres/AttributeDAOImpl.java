package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.server.dao.AttributeDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class AttributeDAOImpl extends HibernateDaoSupport implements AttributeDAO{

	@Override
	public Attribute getAttribute(int attributeId){
		Attribute attribute = (Attribute) getHibernateTemplate().load(Attribute.class, attributeId);
		Hibernate.initialize(attribute.getEntity());
		Hibernate.initialize(attribute.getEntity().getAttributes());
		Hibernate.initialize(attribute.getEntity().getSchema());
		return attribute;
	}
}
