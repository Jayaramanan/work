package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.PredefinedAttributesDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.PredefinedAttribute;
import com.ni3.ag.navigator.server.domain.Schema;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class PredefinedAttributesDAOImpl extends HibernateDaoSupport implements PredefinedAttributesDAO{

	@Override
	@SuppressWarnings("unchecked")
	public Map<Integer, Integer> getNumericPredefinedValues(final int schemaId){
		final Schema schema = (Schema) getHibernateTemplate().load(Schema.class, schemaId);
		Map<Integer, Integer> results = new HashMap<Integer, Integer>();

		for (ObjectDefinition od : schema.getDefinitions())
			for (Attribute a : od.getAttributes())
				for (PredefinedAttribute pa : a.getValues())
					try{
						if (pa.getValue() == null)
							continue;
						int i = Integer.parseInt(pa.getValue());
						results.put(pa.getId(), i);
					} catch (NumberFormatException ignore){
					}
		return results;
	}
}
