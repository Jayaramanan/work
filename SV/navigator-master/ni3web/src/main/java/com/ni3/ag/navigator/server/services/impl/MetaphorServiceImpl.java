/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.MetaphorSetDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Metaphor;
import com.ni3.ag.navigator.server.domain.MetaphorData;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.MetaphorService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.MetaphorIcon;
import com.ni3.ag.navigator.shared.domain.NodeMetaphor;

public class MetaphorServiceImpl extends JdbcDaoSupport implements MetaphorService{
	private static final Logger log = Logger.getLogger(MetaphorServiceImpl.class);
	private final static String ICONNAME_ATTRIBUTE = "iconname";
	private final static String DEFAULT_METAPHOR_NAME = "all.png";

	private MetaphorSetDAO metaphorSetDAO;
	private SchemaLoaderService schemaLoaderService;

	public void setMetaphorSetDAO(MetaphorSetDAO metaphorSetDAO){
		this.metaphorSetDAO = metaphorSetDAO;
	}

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	@Override
	public List<String> getMetaphorSets(int schemaId){
		return metaphorSetDAO.getMetaphorSets(schemaId);
	}

	@Override
	public void fillMetaphors(Collection<DBObject> objects, int schemaId){
		if (log.isDebugEnabled()){
			log.debug("Fill metaphor data for dbobjects, count=" + objects.size() + ", schemaId=" + schemaId);
		}
		Schema schema = schemaLoaderService.getSchema(schemaId);
		Map<Integer, Set<String>> msMap = new HashMap<Integer, Set<String>>();
		for (DBObject object : objects){
			ObjectDefinition od = schema.getEntity(object.getEntityId());
			if (od.isEdge()){
				continue;
			}
			NodeMetaphor nm = new NodeMetaphor();
			String iconName = getAssignedIconName(object, od);
			if (iconName != null){
				MetaphorIcon icon = new MetaphorIcon(iconName, 0);
				nm.setAssignedMetaphor(icon);
			}

			if (!msMap.containsKey(od.getId())){
				msMap.put(od.getId(), getMetaphorSets(od));
			}
			Set<String> metaphorSets = msMap.get(od.getId());
			Map<String, MetaphorIcon> map = new HashMap<String, MetaphorIcon>();
			for (String metaphorSet : metaphorSets){
				MetaphorIcon icon = getMetaphor(object, od, metaphorSet);
				map.put(metaphorSet, icon);
			}
			nm.setMetaphors(map);
			object.setMetaphor(nm);
		}
	}

	MetaphorIcon getMetaphor(DBObject dbObject, ObjectDefinition od, String metaphorSet){
		MetaphorIcon result = null;
		String iconName = getAssignedIconName(dbObject, od);
		if (iconName == null){
			Metaphor met = null;
			List<Metaphor> metaphors = od.getMetaphors();
			for (Metaphor metaphor : metaphors){
				if (metaphor.getObjectDefinitionId().intValue() != dbObject.getEntityId()
						|| !metaphorSet.equalsIgnoreCase(metaphor.getMetaphorSet()))
					continue;
				// search for exactly matched metaphor
				if (isExactMatch(metaphor.getMetaphorData(), dbObject)
						&& (met == null || met.getPriority() > metaphor.getPriority() || (met.getPriority() == metaphor
								.getPriority() && met.getMetaphorData().size() < metaphor.getMetaphorData().size()))){
					met = metaphor;
				}
			}
			if (met == null){ // if not found, try to find maximally matched
				log.debug("Couldn't find exactly matched metaphor for node" + dbObject.getId()
						+ ", trying to find maximally matched one");
				met = getMaxMatchMetaphor(dbObject, metaphors, metaphorSet);
			}
			if (met != null){
				result = new MetaphorIcon(met.getIconName(), met.getPriority());
			} else{
				log.info("Couldn't find metaphor for node " + dbObject.getId() + "; default is taken");
				result = new MetaphorIcon(DEFAULT_METAPHOR_NAME, 100);
			}
		} else{
			result = new MetaphorIcon(iconName, 0);
		}
		return result;
	}

	String getAssignedIconName(DBObject dbObject, ObjectDefinition od){
		String iconName = null;

		Attribute a = od.getAttribute(ICONNAME_ATTRIBUTE);
		if (a != null){
			String iName = dbObject.getData().get(a.getId());
			if (iName != null && !iName.isEmpty()){
				iconName = iName;
			}
		}
		return iconName;
	}

	boolean isExactMatch(List<MetaphorData> metaphorData, DBObject dbObject){
		boolean result = true;
		for (MetaphorData dataRow : metaphorData){
			String value = dbObject.getData().get(dataRow.getAttributeId());
			if (value == null || !value.equals(String.valueOf(dataRow.getData()))){
				result = false;
				break;
			}
		}
		return result;
	}

	Metaphor getMaxMatchMetaphor(DBObject object, List<Metaphor> metaphors, String metaphorSet){
		Metaphor result = null;
		int maxMatch = 0;
		for (Metaphor metaphor : metaphors){
			if (metaphor.getObjectDefinitionId().intValue() != object.getEntityId()
					|| !metaphorSet.equalsIgnoreCase(metaphor.getMetaphorSet()))
				continue;
			int matchCount = getMatchCount(metaphor.getMetaphorData(), object);
			if (matchCount > maxMatch){
				maxMatch = matchCount;
				result = metaphor;
			}
		}
		return result;
	}

	int getMatchCount(List<MetaphorData> metaphorData, DBObject dbObject){
		int matchCount = 0;
		for (MetaphorData dataRow : metaphorData){
			String value = dbObject.getData().get(dataRow.getAttributeId());
			if (value != null && value.equals(String.valueOf(dataRow.getData()))){
				matchCount++;
			}
		}
		return matchCount;
	}

	Set<String> getMetaphorSets(ObjectDefinition entity){
		Set<String> set = new HashSet<String>();
		for (Metaphor metaphor : entity.getMetaphors()){
			if (!set.contains(metaphor.getMetaphorSet())){
				set.add(metaphor.getMetaphorSet());
			}
		}
		return set;
	}
}
