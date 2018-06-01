package com.ni3.ag.navigator.server.graphXXL;

import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.UserDataService;
import org.apache.log4j.Logger;

public class LazyFilter{
	private static final Logger log = Logger.getLogger(LazyFilter.class);
	private Schema schema;
	private Map<Integer, Set<Integer>> objects;

	public LazyFilter(Schema schema){
		this.schema = schema;
		objects = new HashMap<Integer, Set<Integer>>();
	}

	public boolean isFiltered(Node node, DataFilter dataFilter, DataFilter sysFilter){
		if (node == null)
			return false;
		if (dataFilter.getFilteredValues().isEmpty() && sysFilter.getFilteredValues().isEmpty())
			return false;
		if (!objects.containsKey(node.getID()))
			loadData(node.getID(), node.getType());
		return isFiltered(objects.get(node.getID()), dataFilter.getFilteredValues(), sysFilter.getFilteredValues());
	}

	public boolean isFiltered(Edge edge, DataFilter dataFilter, DataFilter sysFilter){
		if (edge == null)
			return false;
		if (dataFilter.getFilteredValues().isEmpty() && sysFilter.getFilteredValues().isEmpty())
			return false;
		if (!objects.containsKey(edge.getID()))
			loadData(edge.getID(), edge.getType());
		Set<Integer> ids = new HashSet<Integer>();
		ids.addAll(sysFilter.getFilteredValues());
		ids.addAll(dataFilter.getFilteredValues());
		return isFiltered(objects.get(edge.getID()), dataFilter.getFilteredValues(), sysFilter.getFilteredValues());
	}

	private boolean isFiltered(Set<Integer> objectValues, Collection<Integer> ids, Collection<Integer> ids2){
		if (containsAny(objectValues, ids))
			return true;
		else if (containsAny(objectValues, ids2))
			return true;
		return false;
	}

	private boolean containsAny(Set<Integer> objectValues, Collection<Integer> ids){
		for (Integer id : ids)
			if (objectValues.contains(id))
				return true;
		return false;
	}

	private void loadData(int id, int type){
		UserDataService userDataService = NSpringFactory.getInstance().getUserDataService();
		ObjectDefinition od = schema.getEntity(type);
		Map<Integer, Set<Integer>> result = userDataService.getDataOfPredefinedForIdList(od, Arrays.asList(id));
		if (!result.containsKey(id)){
			log.warn("Error loading object: " + id);
			return;
		}
		objects.put(id, result.get(id));
	}

	public void updateObject(Node node){
		loadData(node.getID(), node.getType());
	}

	public void updateObject(Edge edge){
		loadData(edge.getID(), edge.getType());
	}

	public void removeObject(Edge edge){
		objects.remove(edge.getID());
	}

	public void removeNode(Node node){
		objects.remove(node.getID());
	}
}
