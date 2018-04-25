/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class NodeMetaphorControllerTest extends ACTestCase{

	private NodeMetaphorController controller;
	private NodeMetaphorModel model;
	private Metaphor metaphor1;
	private Metaphor metaphor2;
	private Metaphor metaphor3;
	private String set1;
	private String set2;
	private ObjectDefinition object;

	@Override
	protected void setUp() throws Exception{
		object = new ObjectDefinition();
		set1 = "set1";
		set2 = "set2";
		controller = (NodeMetaphorController) ACSpringFactory.getInstance().getBean("nodeMetaphorController");
		model = (NodeMetaphorModel) controller.getModel();
		List<Metaphor> metaphors = new ArrayList<Metaphor>();
		metaphor1 = createNodeMetaphor(1, set1);
		metaphor2 = createNodeMetaphor(2, set2);
		metaphor3 = createNodeMetaphor(3, set2);

		metaphors.add(metaphor1);
		metaphors.add(metaphor2);
		metaphors.add(metaphor3);

		object.setMetaphors(metaphors);

		List<Metaphor> currentTableData = new ArrayList<Metaphor>();
		currentTableData.add(metaphor2);
		currentTableData.add(metaphor3);
		model.setCurrentObjectDefinition(object);
	}

	public void testGetMetaphorSets(){
		metaphor1.setMetaphorSet("Default");
		List<String> result = controller.getMetaphorSets(object.getMetaphors());
		assertEquals(2, result.size());
		assertEquals("Default", result.get(0));
		assertEquals(set2, result.get(1));
	}

	public void testGetMetaphorSetsNoDefault(){
		List<String> result = controller.getMetaphorSets(object.getMetaphors());
		assertEquals(3, result.size());
		assertEquals(set1, result.get(0));
		assertEquals(set2, result.get(1));
		assertEquals("Default", result.get(2));
	}

	private Metaphor createNodeMetaphor(int uniq, String metaphorSet){
		Metaphor newMetaphor = new Metaphor();
		newMetaphor.setIcon(new Icon());
		newMetaphor.setMetaphorSet(metaphorSet);
		newMetaphor.setPriority(0);
		newMetaphor.setDescription("description" + uniq);
		newMetaphor.setObjectDefinition(object);
		newMetaphor.setSchema(new Schema());
		newMetaphor.setMetaphorData(new ArrayList<MetaphorData>());
		return newMetaphor;
	}

}