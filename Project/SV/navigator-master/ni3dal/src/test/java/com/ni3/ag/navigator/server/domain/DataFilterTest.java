package com.ni3.ag.navigator.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.type.PredefinedType;
import com.ni3.ag.navigator.shared.domain.Prefilter;

public class DataFilterTest extends TestCase{
	public void testDataFilter(){
		List<Prefilter> pfList = new ArrayList<Prefilter>();

		DataFilter df = new DataFilter(pfList);
		assertTrue(df.getFilter().isEmpty());
		assertTrue(df.getFilteredValues().isEmpty());

		Prefilter pf1 = new Prefilter();
		pf1.setObjectDefinitionId(11);
		pf1.setAttributeId(101);
		pf1.setPredefinedId(1001);
		pfList.add(pf1);
		df = new DataFilter(pfList);
		assertFalse(df.getFilter().isEmpty());
		assertFalse(df.getFilteredValues().isEmpty());
		final Map<Integer, Set<Integer>> attrMap = df.getFilter().get(11);
		assertNotNull(attrMap);
		assertEquals(1, attrMap.get(101).size());
		assertTrue(attrMap.get(101).contains(1001));
		assertTrue(df.getFilteredValues().contains(1001));

		Prefilter pf2 = new Prefilter();
		pf2.setObjectDefinitionId(12);
		pf2.setAttributeId(102);
		pf2.setPredefinedId(1002);
		pfList.add(pf2);
		Prefilter pf3 = new Prefilter();
		pf3.setObjectDefinitionId(11);
		pf3.setAttributeId(101);
		pf3.setPredefinedId(1003);
		pfList.add(pf3);
		Prefilter pf4 = new Prefilter();
		pf4.setObjectDefinitionId(11);
		pf4.setAttributeId(103);
		pf4.setPredefinedId(1004);
		pfList.add(pf4);
		df = new DataFilter(pfList);
		assertFalse(df.getFilter().isEmpty());
		assertFalse(df.getFilteredValues().isEmpty());
		final Map<Integer, Set<Integer>> attrMap1 = df.getFilter().get(11);
		final Map<Integer, Set<Integer>> attrMap2 = df.getFilter().get(12);
		assertNotNull(attrMap1);
		assertNotNull(attrMap2);
		assertEquals(2, attrMap1.get(101).size());
		assertEquals(1, attrMap1.get(103).size());
		assertEquals(1, attrMap2.get(102).size());
		assertTrue(attrMap1.get(101).contains(1001));
		assertTrue(attrMap1.get(101).contains(1003));
		assertTrue(attrMap1.get(103).contains(1004));
		assertTrue(attrMap2.get(102).contains(1002));
		assertTrue(df.getFilteredValues().contains(1001));
		assertTrue(df.getFilteredValues().contains(1002));
		assertTrue(df.getFilteredValues().contains(1003));
		assertTrue(df.getFilteredValues().contains(1004));
	}

	// 1
	// -> 11
	// ->-> 110
	// ->->-> 1100
	// ->->-> 1101
	// ->->-> 1102
	// ->-> 111
	// ->->-> 1110
	// ->->-> 1111
	// ->->-> 1112
	// ->-> 112
	// ->->-> 1120
	// ->->-> 1121
	// ->->-> 1122
	// -> 12
	// ->-> 120
	// ->->-> 1200
	// ->->-> 1201
	// ->->->1202
	// ->->121
	// ->->-> 1210
	// ->->-> 1211
	// ->->-> 1212
	// ->-> 122
	// ->->-> 1220
	// ->->-> 1221
	// ->->-> 1222
	public void testDataFilter2(){
		Schema schema = new Schema();
		schema.setId(1);
		schema.setDefinitions(new ArrayList<ObjectDefinition>());
		schema.getDefinitions().add(createEntity(11));
		schema.getDefinitions().add(createEntity(12));

		List<Integer> filteredValues = new ArrayList<Integer>();

		DataFilter df = new DataFilter(schema, filteredValues);
		assertTrue(df.getFilter().isEmpty());
		assertTrue(df.getFilteredValues().isEmpty());

		filteredValues.add(1101);
		df = new DataFilter(schema, filteredValues);
		assertFalse(df.getFilter().isEmpty());
		assertFalse(df.getFilteredValues().isEmpty());
		final Map<Integer, Set<Integer>> attrMap = df.getFilter().get(11);
		assertNotNull(attrMap);
		assertEquals(1, attrMap.get(110).size());
		assertTrue(attrMap.get(110).contains(1101));
		assertTrue(df.getFilteredValues().contains(1101));

		filteredValues.add(1102);
		filteredValues.add(1120);
		filteredValues.add(1212);
		df = new DataFilter(schema, filteredValues);
		assertFalse(df.getFilter().isEmpty());
		assertFalse(df.getFilteredValues().isEmpty());
		final Map<Integer, Set<Integer>> attrMap1 = df.getFilter().get(11);
		final Map<Integer, Set<Integer>> attrMap2 = df.getFilter().get(12);
		assertNotNull(attrMap1);
		assertNotNull(attrMap2);
		assertEquals(2, attrMap1.get(110).size());
		assertEquals(1, attrMap1.get(112).size());
		assertEquals(1, attrMap2.get(121).size());
		assertTrue(attrMap1.get(110).contains(1101));
		assertTrue(attrMap1.get(110).contains(1102));
		assertTrue(attrMap1.get(112).contains(1120));
		assertTrue(attrMap2.get(121).contains(1212));
		assertTrue(df.getFilteredValues().contains(1101));
		assertTrue(df.getFilteredValues().contains(1102));
		assertTrue(df.getFilteredValues().contains(1120));
		assertTrue(df.getFilteredValues().contains(1212));
	}

	private ObjectDefinition createEntity(int id){
		ObjectDefinition e = new ObjectDefinition();
		e.setId(id);
		e.setAttributes(new ArrayList<Attribute>());
		for (int i = 0; i < 3; i++){
			e.getAttributes().add(createAttribute(id * 10 + i));
		}
		return e;
	}

	private Attribute createAttribute(int id){
		Attribute a = new Attribute();
		a.setId(id);
		a.setPredefined(PredefinedType.Predefined);
		a.setValues(new ArrayList<PredefinedAttribute>());
		for (int i = 0; i < 3; i++){
			a.getValues().add(createValue(a, id * 10 + i));
		}
		return a;
	}

	private PredefinedAttribute createValue(Attribute a, int id){
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setAttribute(a);
		pa.setId(id);
		return pa;
	}

}
