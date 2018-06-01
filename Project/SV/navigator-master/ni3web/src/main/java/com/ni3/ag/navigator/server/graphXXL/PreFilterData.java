/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.graphXXL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.navigator.server.domain.DataFilter;

public class PreFilterData{
	private Map<Integer, PreFilterObjectData> allData;

	PreFilterData(){
		allData = new HashMap<Integer, PreFilterObjectData>();
	}

	public PreFilterData(PreFilterData pf){
		allData = pf.allData;
	}

	public PreFilterData(int schemaId){
		allData = new HashMap<Integer, PreFilterObjectData>();

		final ObjectDefinitionDAO objectDefinitionDAO = NSpringFactory.getInstance().getObjectDefinitionDAO();
		List<Integer> odIds = objectDefinitionDAO.getEntitiesWithValueListAttributes(schemaId);
		if (odIds != null){
			for (Integer odId : odIds)
				allData.put(odId, new PreFilterObjectData(odId));
		}
	}

	public boolean checkObject(int objectId, int entityId, DataFilter filterData){
		boolean result = false;
		if (filterData != null && filterData.getFilter() != null && !filterData.getFilter().isEmpty()){
			PreFilterObjectData pfData = allData.get(entityId);
			final Map<Integer, Set<Integer>> fData = filterData.getFilter().get(entityId);
			if (fData != null && pfData != null){
				result = pfData.checkObject(objectId, fData);
			}
		}
		return result;
	}

	public void updateObject(int ID, int entityId){
		PreFilterObjectData pfData = allData.get(entityId);
		if (pfData == null)
			return;
		pfData.updateObject(ID);
	}

}
