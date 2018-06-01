/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class ObjectConnectionModel extends AbstractModel{
	private ObjectDefinition currentObject;
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private Map<DatabaseInstance, List<ObjectDefinition>> nodeObjectMap = new HashMap<DatabaseInstance, List<ObjectDefinition>>();
	private Map<DatabaseInstance, List<LineWeight>> lineWeightMap = new HashMap<DatabaseInstance, List<LineWeight>>();
	private Map<DatabaseInstance, Set<Integer>> hierarchicalEdgeMap = new HashMap<DatabaseInstance, Set<Integer>>();

	public ObjectConnectionModel(){
	}

	public List<ObjectDefinition> getNodeObjects(){
		return nodeObjectMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<ObjectDefinition>> getNodeObjectMap(){
		return nodeObjectMap;
	}

	public void setNodeObjects(List<ObjectDefinition> objectDefinitions){
		nodeObjectMap.put(currentDatabaseInstance, objectDefinitions);
	}

	public List<LineWeight> getLineWeights(){
		return lineWeightMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<LineWeight>> getLineWeightMap(){
		return lineWeightMap;
	}

	public void setLineWeights(List<LineWeight> lineWeights){
		lineWeightMap.put(currentDatabaseInstance, lineWeights);
	}

	public ObjectDefinition getCurrentObject(){
		return currentObject;
	}

	public void setCurrentObject(ObjectDefinition currentObject){
		this.currentObject = currentObject;
	}

	public List<Schema> getSchemas(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public void setSchemas(List<Schema> schemas){
		schemaMap.put(currentDatabaseInstance, schemas);
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return schemaMap.containsKey(instance);
	}

	public void addHierarchicalEdges(Set<Integer> edges){
		hierarchicalEdgeMap.put(currentDatabaseInstance, edges);
	}

	public Map<DatabaseInstance, Set<Integer>> getHierarchicalEdgeMap(){
		return hierarchicalEdgeMap;
	}

	public Set<Integer> getHierarchicalEdges(){
		Set<Integer> set = hierarchicalEdgeMap.get(currentDatabaseInstance);
		if (set == null){
			set = new HashSet<Integer>();
			hierarchicalEdgeMap.put(currentDatabaseInstance, set);
		}
		return set;
	}

	public boolean isHierarchicalEdge(Integer id){
		Set<Integer> set = hierarchicalEdgeMap.get(currentDatabaseInstance);
		return set != null && set.contains(id);
	}

	public void setHierarchicalEdge(Integer id){
		Set<Integer> set = hierarchicalEdgeMap.get(currentDatabaseInstance);
		if (set == null){
			set = new HashSet<Integer>();
			hierarchicalEdgeMap.put(currentDatabaseInstance, set);
		}
		set.add(id);
	}
}
