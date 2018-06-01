package com.ni3.ag.navigator.server.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListIdCriteria extends Criteria{

	private Map<Integer, List<Integer>> requestIdMap = new HashMap<Integer, List<Integer>> ();
	private boolean withDeleted;
	private ContextData contextData;

	public void setContextData(ContextData contextData){
		this.contextData = contextData;
	}

	public ContextData getContextData(){
		return contextData;
	}

	public void add(int entityId, List<Integer> ids){
		requestIdMap.put(entityId, ids);
	}

	public Map<Integer, List<Integer>> getRequestIdMap(){
		return requestIdMap;
	}

	public boolean isEmpty(){
		return requestIdMap.isEmpty();
	}

	public void setWithDeleted(boolean withDeleted){
		this.withDeleted = withDeleted;
	}

	public boolean isWithDeleted(){
		return withDeleted;
	}

	public static class ContextData{
		private int contextId;
		private String key;

		public ContextData(int contextId, String key){
			this.contextId = contextId;
			this.key = key;
		}

		public int getContextId(){
			return contextId;
		}

		public String getKey(){
			return key;
		}
	}
}
