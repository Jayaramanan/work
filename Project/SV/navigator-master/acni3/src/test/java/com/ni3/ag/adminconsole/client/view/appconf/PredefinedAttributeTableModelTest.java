/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

import junit.framework.TestCase;

public class PredefinedAttributeTableModelTest extends TestCase{

	private PredefinedAttributeTableModel model;
	private ObjectAttribute attribute;

	public void setUp(){
		attribute = generateObjectAttribute();
		model = new PredefinedAttributeTableModel();
		model.setData(attribute, attribute.getPredefinedAttributes());
	}

	private ObjectAttribute generateObjectAttribute(){
		ObjectAttribute attr = new ObjectAttribute();
		List<PredefinedAttribute> paList = new ArrayList<PredefinedAttribute>();
		for (int i = 0; i < 10; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId(i);
			pa.setValue("val" + i);
			pa.setLabel("pa" + i);
			pa.setTranslation("trans" + i);
			pa.setToUse(true);
			pa.setSort(i);
			PredefinedAttribute parent = new PredefinedAttribute();
			parent.setId(i - 1);
			pa.setParent(parent);
			pa.setSrcID("pa" + i);
			pa.setHaloColor("A");
			paList.add(pa);
		}
		attr.setPredefinedAttributes(paList);
		attr.setInFilter(Boolean.TRUE);
		return attr;
	}

	public void testColumnCount(){
		assertEquals(9, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), attribute.getPredefinedAttributes().size());
	}

	public void testValueAt(){
		List<PredefinedAttribute> paList = attribute.getPredefinedAttributes();
		for (int i = 0; i < paList.size(); i++){
			PredefinedAttribute pa = paList.get(i);
			assertEquals(pa.getId(), model.getValueAt(i, 0));
			assertEquals(pa.getValue(), model.getValueAt(i, 1));
			assertEquals(pa.getLabel(), model.getValueAt(i, 2));
			assertEquals(pa.getTranslation(), model.getValueAt(i, 3));
			assertEquals(pa.getToUse(), model.getValueAt(i, 4));
			assertEquals(pa.getSort(), model.getValueAt(i, 5));
			assertEquals(pa.getParent().getId(), model.getValueAt(i, 6));
			assertEquals(pa.getSrcID(), model.getValueAt(i, 7));
			assertEquals(pa.getHaloColor(), model.getValueAt(i, 8));
		}
	}

	public void testSetValueAt(){
		List<PredefinedAttribute> paList = attribute.getPredefinedAttributes();
		for (int i = 0; i < paList.size(); i++){
			model.setValueAt("aaa", i, 1);
			model.setValueAt("bbb", i, 2);
			model.setValueAt(false, i, 4);
			model.setValueAt(i + 5, i, 5);
			model.setValueAt(null, i, 6);
			model.setValueAt("ddd", i, 7);
			model.setValueAt("R", i, 8);

			PredefinedAttribute pa = paList.get(i);
			assertEquals(pa.getValue(), "aaa");
			assertEquals(pa.getLabel(), "bbb");
			assertEquals(pa.getToUse(), Boolean.FALSE);
			assertEquals(pa.getSort().intValue(), i + 5);
			assertEquals(pa.getParent(), null);
			assertEquals(pa.getSrcID(), "ddd");
			assertEquals(pa.getHaloColor(), "R");
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			assertEquals(false, model.isCellEditable(i, 0));
			assertEquals(true, model.isCellEditable(i, 1));
			assertEquals(true, model.isCellEditable(i, 2));
			assertEquals(false, model.isCellEditable(i, 3));
			assertEquals(true, model.isCellEditable(i, 4));
			assertEquals(true, model.isCellEditable(i, 5));
			assertEquals(true, model.isCellEditable(i, 6));
			assertEquals(true, model.isCellEditable(i, 7));
			assertEquals(true, model.isCellEditable(i, 8));
		}
	}

}
