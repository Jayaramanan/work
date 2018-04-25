/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class NodeMetaphorTableModelTest extends TestCase{
	private NodeMetaphorTableModel model;
	private List<Metaphor> metaphors;
	private ObjectAttribute attr1;
	private ObjectAttribute attr2;
	private ObjectAttribute attr3;
	private ObjectDefinition object;

	public void setUp(){
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
		model = new NodeMetaphorTableModel(object.getObjectAttributes(), metaphors);
	}

	private List<Metaphor> generateMetaphors(){
		List<Metaphor> metaphors = new ArrayList<Metaphor>();
		for (int i = 0; i < 10; i++){
			Metaphor metaphor = new Metaphor();
			Icon icon = new Icon();
			icon.setIconName("icon" + i);
			metaphor.setIcon(icon);
			metaphor.setPriority(i);
			metaphor.setMetaphorSet("set1");
			metaphor.setDescription("descr" + i);
			metaphor.setMetaphorData(getMetaphorData(metaphor));
			metaphors.add(metaphor);
		}
		return metaphors;
	}

	private List<MetaphorData> getMetaphorData(Metaphor metaphor){
		List<MetaphorData> mdList = new ArrayList<MetaphorData>();
		for (int i = 1; i <= 3; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId(i * metaphor.getPriority() * 10);
			mdList.add(new MetaphorData(metaphor, object.getObjectAttributes().get(i - 1), pa));
		}
		return mdList;
	}

	public void testColumnCount(){
		assertEquals(7, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), metaphors.size());
	}

	public void testGetValueAt(){
		for (int i = 0; i < metaphors.size(); i++){
			Metaphor metaphor = metaphors.get(i);
			assertEquals(metaphor.getIcon(), model.getValueAt(i, 0));
			assertEquals(metaphor.getPriority(), model.getValueAt(i, 1));
			assertEquals(metaphor.getMetaphorSet(), model.getValueAt(i, 2));
			assertEquals(metaphor.getDescription(), model.getValueAt(i, 3));
			List<MetaphorData> mdList = metaphor.getMetaphorData();
			for (int j = 0; j < mdList.size(); j++){
				MetaphorData md = mdList.get(j);
				assertEquals(md.getData(), model.getValueAt(i, 4 + j));
			}
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			assertTrue(model.isCellEditable(i, 0));
			assertTrue(model.isCellEditable(i, 1));
			assertTrue(model.isCellEditable(i, 2));
			assertTrue(model.isCellEditable(i, 3));
			assertTrue(model.isCellEditable(i, 4));
			assertTrue(model.isCellEditable(i, 5));
			assertTrue(model.isCellEditable(i, 6));
		}
	}

	public void testSetValueAt(){
		for (int i = 0; i < metaphors.size(); i++){
			Icon icon = new Icon();
			icon.setId(i * 2);
			model.setValueAt(icon, i, 0);
			model.setValueAt(i * 3, i, 1);
			model.setValueAt("newSet", i, 2);
			model.setValueAt("newDescr" + i, i, 3);
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId((i + 1) * 4);
			model.setValueAt(pa, i, 4);

			Metaphor nm = metaphors.get(i);
			assertEquals(nm.getIcon(), icon);
			assertEquals(nm.getPriority().intValue(), i * 3);
			assertEquals(nm.getMetaphorSet(), "newSet");
			assertEquals(nm.getDescription(), "newDescr" + i);
			assertEquals(nm.getMetaphorData().get(0).getData(), pa);
		}
	}

	public void testGetPredefinedAttribute(){
		for (int i = 0; i < metaphors.size(); i++){
			Metaphor metaphor = metaphors.get(i);
			List<MetaphorData> mdList = metaphor.getMetaphorData();
			for (int j = 0; j < mdList.size(); j++){
				MetaphorData md = mdList.get(j);
				assertEquals(md.getData(), model.getPredefinedAttribute(metaphor, j + 4));
			}
		}
	}

	public void testSetPredefinedAttribute(){

		for (int i = 0; i < metaphors.size(); i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId((i + 1) * 4);

			Metaphor nm = metaphors.get(i);
			model.setPredefinedAttribute(pa, metaphors.get(i), 4);
			assertEquals(nm.getMetaphorData().get(0).getData(), pa);
		}
	}

	public void testSetPredefinedAttributeSetNull(){
		for (int i = 0; i < metaphors.size(); i++){
			Metaphor nm = metaphors.get(i);

			assertEquals(3, nm.getMetaphorData().size());

			for (int j = 0; j < 3; j++){
				model.setPredefinedAttribute(null, metaphors.get(i), j + 4);
			}

			assertEquals(0, nm.getMetaphorData().size());
		}
	}
}
