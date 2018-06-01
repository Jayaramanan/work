package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.navigator.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.type.PredefinedType;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ObjectDefinitionDAOImpl extends HibernateDaoSupport implements ObjectDefinitionDAO{

	@Override
	public List<ObjectDefinition> getObjectDefinitions(){
		return getHibernateTemplate().loadAll(ObjectDefinition.class);
	}

	@Override
	public ObjectDefinition get(Integer id){
		return (ObjectDefinition) getHibernateTemplate().load(ObjectDefinition.class, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getEntitiesWithValueListAttributes(int schemaId){
		List<ObjectDefinition> all = getObjectDefinitions();
		Set<Integer> result = new HashSet<Integer>();
		for(ObjectDefinition od : all){
			if(od.getSchema().getId() != schemaId)
				continue;
			for(Attribute attribute : od.getAttributes()){
				if(PredefinedType.Predefined.equals(attribute.getPredefined()) || PredefinedType.FormulaPredefined.equals(attribute.getPredefined())){
					result.add(od.getId());
					break;
				}
			}
		}
		return Arrays.asList(result.toArray(new Integer[result.size()]));
	}

}
