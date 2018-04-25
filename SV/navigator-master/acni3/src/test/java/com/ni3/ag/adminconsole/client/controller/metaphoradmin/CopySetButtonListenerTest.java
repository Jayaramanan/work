/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class CopySetButtonListenerTest extends ACTestCase{
	private NodeMetaphorController controller;
	private NodeMetaphorModel model;
	private Metaphor metaphor1;
	private Metaphor metaphor2;
	private String setFrom;
	private String setTo;
	private ObjectDefinition object;
	private ObjectAttribute attr1;
	private ObjectAttribute attr2;

	@Override
	protected void setUp() throws Exception{
		object = new ObjectDefinition();
		setFrom = "set1";
		setTo = "set2";
		controller = (NodeMetaphorController) ACSpringFactory.getInstance().getBean("nodeMetaphorController");
		model = (NodeMetaphorModel) controller.getModel();
		List<Metaphor> metaphors = new ArrayList<Metaphor>();
		metaphor1 = createNodeMetaphor(1, setFrom);
		metaphor2 = createNodeMetaphor(2, setFrom);

		metaphors.add(metaphor1);
		metaphors.add(metaphor2);

		object.setMetaphors(metaphors);
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		attr1 = new ObjectAttribute(object);
		attr1.setName("attr1");
		object.getObjectAttributes().add(attr1);
		attr2 = new ObjectAttribute(object);
		attr2.setName("attr2");
		object.getObjectAttributes().add(attr2);

		model.setCurrentObjectDefinition(object);
	}

	public void testCopyMetaphorSet(){
		CopySetButtonListener lst = new CopySetButtonListener(controller);
		lst.copyMetaphorSet(setFrom, setTo);
		assertEquals(4, object.getMetaphors().size());
		assertEquals(setTo, object.getMetaphors().get(2).getMetaphorSet());
		assertEquals(setTo, object.getMetaphors().get(3).getMetaphorSet());
		assertEquals(2, object.getMetaphors().get(2).getMetaphorData().size());
		assertEquals(2, object.getMetaphors().get(3).getMetaphorData().size());
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
		PredefinedAttribute pattr1 = new PredefinedAttribute();
		pattr1.setValue("value1" + uniq);
		pattr1.setLabel("label1" + uniq);
		newMetaphor.getMetaphorData().add(new MetaphorData(newMetaphor, attr1, pattr1));
		PredefinedAttribute pattr2 = new PredefinedAttribute();
		pattr2.setValue("value2" + uniq);
		pattr2.setLabel("label2" + uniq);
		newMetaphor.getMetaphorData().add(new MetaphorData(newMetaphor, attr1, pattr2));
		return newMetaphor;
	}
}