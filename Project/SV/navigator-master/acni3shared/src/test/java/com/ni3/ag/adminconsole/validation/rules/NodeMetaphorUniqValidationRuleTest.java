/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class NodeMetaphorUniqValidationRuleTest extends TestCase{
	private NodeMetaphorUniqValidationRule rule;
	private NodeMetaphorModel model;
	private List<Metaphor> metaphors;
	private ObjectAttribute attr1;
	private ObjectAttribute attr2;
	private ObjectAttribute attr3;
	private ObjectDefinition object;
	int id = 10;

	@Override
	public void setUp(){
		rule = new NodeMetaphorUniqValidationRule();
		object = new ObjectDefinition();
		attr1 = new ObjectAttribute(object);
		attr1.setName("attr1");
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attr1);
		attr2 = new ObjectAttribute(object);
		attr2.setName("attr2");
		object.getObjectAttributes().add(attr2);
		attr3 = new ObjectAttribute(object);
		attr3.setName("attr3");
		object.getObjectAttributes().add(attr3);
		metaphors = generateMetaphors();
		object.setMetaphors(metaphors);
		model = new NodeMetaphorModel();
		model.setCurrentObjectDefinition(object);
	}

	private List<Metaphor> generateMetaphors(){
		List<Metaphor> metaphors = new ArrayList<Metaphor>();

		for (int i = 1; i <= 2; i++){
			Metaphor metaphor = new Metaphor();
			Icon icon = new Icon();
			icon.setIconName("icon" + i);
			metaphor.setIcon(icon);
			metaphor.setPriority(i);
			metaphor.setMetaphorSet("set1");
			metaphor.setDescription("descr" + i);
			metaphor.setMetaphorData(getMetaphorData(metaphor, i));
			metaphors.add(metaphor);
		}
		return metaphors;
	}

	private List<MetaphorData> getMetaphorData(Metaphor metaphor, int count){
		List<MetaphorData> mdList = new ArrayList<MetaphorData>();
		for (int i = 1; i <= count; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId(id++);
			mdList.add(new MetaphorData(metaphor, object.getObjectAttributes().get(i - 1), pa));
		}
		return mdList;
	}

	public void testNotNull(){
		rule.performCheck(null);
		assertNotNull(rule.getErrorEntries());
	}

	public void testPerformCheckAllDifferent(){
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());

		metaphors.get(1).getMetaphorData().clear();
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckMetaphorSetEqual(){
		metaphors.get(0).getMetaphorData().clear();
		metaphors.get(1).getMetaphorData().clear();
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckMetaphorSetAnd1PredefinedEqual(){
		metaphors.get(1).getMetaphorData().remove(1);
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setId(10);
		metaphors.get(1).getMetaphorData().get(0).setData(pa);

		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckMetaphorSetAnd2PredefinedEqual(){
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setId(11);
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setId(12);
		metaphors.get(0).getMetaphorData().get(0).setData(pa);
		metaphors.get(0).getMetaphorData().add(new MetaphorData(metaphors.get(0), object.getObjectAttributes().get(1), pa1));

		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testIsEqualNotEqual(){
		assertFalse(rule.isEqual(metaphors.get(0), metaphors.get(1)));

		metaphors.get(1).getMetaphorData().clear();
		assertFalse(rule.isEqual(metaphors.get(0), metaphors.get(1)));

		metaphors.get(0).getMetaphorData().clear();
		metaphors.get(0).setMetaphorSet("Differentset");
		assertFalse(rule.isEqual(metaphors.get(0), metaphors.get(1)));
	}

	public void testIsEqualMetaphorSetsEqual(){
		metaphors.get(0).getMetaphorData().clear();
		metaphors.get(1).getMetaphorData().clear();
		assertTrue(rule.isEqual(metaphors.get(0), metaphors.get(1)));
	}

	public void testIsEqualMetaphorSetsAnd1PredefinedEqual(){
		metaphors.get(1).getMetaphorData().remove(1);
		PredefinedAttribute pa = new PredefinedAttribute();
		pa.setId(10);
		metaphors.get(1).getMetaphorData().get(0).setData(pa);

		assertTrue(rule.isEqual(metaphors.get(0), metaphors.get(1)));
	}
}
