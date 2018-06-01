package com.ni3.ag.navigator.client.controller.graph;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.navigator.client.domain.Value;

public class ValueUsageStatistics{
	private Map<Value, Integer> usageCount = new HashMap<Value, Integer>();
	private Map<Value, Integer> displayedCount = new HashMap<Value, Integer>();

	public void increment(Value v){
		incrementForMap(usageCount, v);
	}

	public void incrementDisplayed(Value v){
		incrementForMap(displayedCount, v);
	}

	private void incrementForMap(Map<Value, Integer> map, Value v){
		int count = 0;
		if(map.containsKey(v))
			count = map.get(v);
		count++;
		map.put(v, count);
	}

	public boolean isUsed(Value predefinedValue){
		return isUsedInternal(usageCount, predefinedValue);
	}

	private boolean isUsedInternal(Map<Value, Integer> map, Value v){
		return map.containsKey(v) && map.get(v) > 0;
	}

	public int getUsage(Value predefinedValue){
		return getUsageInternal(usageCount, predefinedValue);
	}

	public int getDisplayUsage(Value predefinedValue){
		return getUsageInternal(displayedCount, predefinedValue);
	}

	private int getUsageInternal(Map<Value, Integer> map, Value v){
		if(map.containsKey(v))
			return map.get(v);
		return 0;
	}

	public void update(ValueUsageStatistics statistics){
		usageCount.clear();
		displayedCount.clear();
		usageCount.putAll(statistics.usageCount);
		displayedCount.putAll(statistics.displayedCount);
	}

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (!(o instanceof ValueUsageStatistics)) return false;

		ValueUsageStatistics that = (ValueUsageStatistics) o;

		if (displayedCount != null ? !displayedCount.equals(that.displayedCount) : that.displayedCount != null)
			return false;
		if (usageCount != null ? !usageCount.equals(that.usageCount) : that.usageCount != null) return false;

		return true;
	}
}
