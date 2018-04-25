package com.ni3.ag.navigator.client.graph;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.util.PrivateAccesor;
import junit.framework.TestCase;

public class ValueUsageStatisticsTest extends TestCase{

	public void testIncrement() throws Exception{
		ValueUsageStatistics statistics = new ValueUsageStatistics();
		Value v = new Value(1, 0, "", "");
		statistics.increment(v);
		Map<Value, Integer> usage = new HashMap<Value, Integer>();
		usage.put(v, 1);
		assertEquals(usage, PrivateAccesor.getPrivateField(statistics, "usageCount"));

		statistics.increment(v);
		usage.put(v, 2);
		assertEquals(usage, PrivateAccesor.getPrivateField(statistics, "usageCount"));
	}

	public void testIncrementDisplayed() throws Exception{
		ValueUsageStatistics statistics = new ValueUsageStatistics();
		Value v = new Value(1, 0, "", "");
		statistics.incrementDisplayed(v);
		Map<Value, Integer> usage = new HashMap<Value, Integer>();
		usage.put(v, 1);
		assertEquals(usage, PrivateAccesor.getPrivateField(statistics, "displayedCount"));

		statistics.incrementDisplayed(v);
		usage.put(v, 2);
		assertEquals(usage, PrivateAccesor.getPrivateField(statistics, "displayedCount"));
	}

	public void testIncrementForMap() throws Exception{
		ValueUsageStatistics statistics = new ValueUsageStatistics();
		Value v = new Value(1, 0, "", "");

		Map<Value, Integer> affected = new HashMap<Value, Integer>();
		PrivateAccesor.invokePrivateMethod(statistics, "incrementForMap", affected, v);

		Map<Value, Integer> usage = new HashMap<Value, Integer>();
		usage.put(v, 1);
		assertEquals(usage, affected);

		PrivateAccesor.invokePrivateMethod(statistics, "incrementForMap", affected, v);
		usage.put(v, 2);
		assertEquals(usage, affected);
	}

	public void testIsUsed() throws Exception{
		ValueUsageStatistics statistics = new ValueUsageStatistics();
		Value v = new Value(1, 0, "", "");

		assertFalse(statistics.isUsed(v));
		assertEquals(0, statistics.getUsage(v));
		assertEquals(0, statistics.getDisplayUsage(v));

		statistics.increment(v);
		statistics.incrementDisplayed(v);
		assertTrue(statistics.isUsed(v));
		assertEquals(1, statistics.getUsage(v));
		assertEquals(1, statistics.getDisplayUsage(v));

		statistics.increment(v);
		statistics.incrementDisplayed(v);
		assertTrue(statistics.isUsed(v));
		assertEquals(2, statistics.getUsage(v));
		assertEquals(2, statistics.getDisplayUsage(v));

		statistics.increment(v);
		assertTrue(statistics.isUsed(v));
		assertEquals(3, statistics.getUsage(v));
		assertEquals(2, statistics.getDisplayUsage(v));

		statistics.incrementDisplayed(v);
		statistics.incrementDisplayed(v);
		assertTrue(statistics.isUsed(v));
		assertEquals(3, statistics.getUsage(v));
		assertEquals(4, statistics.getDisplayUsage(v));
	}

	public void testUpdateEquals() throws Exception{
		ValueUsageStatistics first = new ValueUsageStatistics();
		ValueUsageStatistics second = new ValueUsageStatistics();
		assertEQStatistics(first, second);

		Value v = new Value(1, 0, "", "");
		first.increment(v);
		assertNEQStatistics(first, second);

		second.increment(v);
		assertEQStatistics(first, second);

		second.increment(v);
		second.incrementDisplayed(v);

		first.update(second);
		assertEQStatistics(first, second);
	}

	private void assertNEQStatistics(ValueUsageStatistics first, ValueUsageStatistics second){
		assertFalse(first.equals(second));
		assertFalse(second.equals(first));
	}

	private void assertEQStatistics(ValueUsageStatistics first, ValueUsageStatistics second){
		assertEquals(first, second);
		assertEquals(second, first);
		assertTrue(first.equals(second));
		assertTrue(second.equals(first));
	}
}