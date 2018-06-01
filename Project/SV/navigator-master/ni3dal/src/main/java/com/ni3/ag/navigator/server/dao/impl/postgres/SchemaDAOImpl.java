package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.navigator.server.dao.SchemaDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Metaphor;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;

public class SchemaDAOImpl extends HibernateDaoSupport implements SchemaDAO{

	@Override
	public Schema getSchema(final int id){
		Schema sch = (Schema) getHibernateTemplate().load(Schema.class, id);
		initSchema(sch);
		return sch;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Schema> getSchemas(){
		List<Schema> schemas = getHibernateTemplate().loadAll(Schema.class);
		for (Schema schema : schemas)
			initSchema(schema);
		return schemas;
	}

	private void initSchema(Schema schema){
		Hibernate.initialize(schema);
		Hibernate.initialize(schema.getDefinitions());
		for (ObjectDefinition od : schema.getDefinitions()){
			Hibernate.initialize(od);
			Hibernate.initialize(od.getUrlOperations());
			Hibernate.initialize(od.getMetaphors());
			for (Metaphor m : od.getMetaphors()){
				Hibernate.initialize(m.getMetaphorData());
			}
			Hibernate.initialize(od.getContexts());
			Hibernate.initialize(od.getAttributes());
			Hibernate.initialize(od.getObjectPermissions());
			for (Attribute attribute : od.getAttributes())
				Hibernate.initialize(attribute.getValues());
		}
	}
}
