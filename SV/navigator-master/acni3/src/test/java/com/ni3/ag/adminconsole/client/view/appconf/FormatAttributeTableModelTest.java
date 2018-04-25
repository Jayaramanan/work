/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class FormatAttributeTableModelTest extends TestCase{
	private FormatAttributeTableModel tableModel;
	private ObjectDefinition object;

	public void setUp(){
		object = generateObjectDefinition();
		tableModel = new FormatAttributeTableModel();
		tableModel.setData(object.getObjectAttributes());
	}

	private ObjectDefinition generateObjectDefinition(){
		ObjectDefinition od = new ObjectDefinition();
		List<ObjectAttribute> oaList = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < 10; i++){
			ObjectAttribute oa = new ObjectAttribute();
			oa.setLabel("attr" + i);
			final DataType dataType = DataType.TEXT;
			oa.setDataType(dataType);
			oa.setSort(i);
			oa.setFormat("#" + i);
			oa.setMinValue("" + i);
			oa.setMaxValue("" + i);
			oa.setEditFormat("f" + i);
			oa.setFormatValidCharacters("fv" + i);
			oa.setFormatInvalidCharacters("fiv" + i);
			oaList.add(oa);
		}
		od.setObjectAttributes(oaList);
		return od;
	}

	public void testColumnCount(){
		assertEquals(9, tableModel.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(tableModel.getRowCount(), object.getObjectAttributes().size());
	}

	public void testValueAt(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < oaList.size(); i++){
			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.getLabel(), tableModel.getValueAt(i, 0));
			assertEquals(oa.getDataType().getTextId().getKey(), tableModel.getValueAt(i, 1));
			assertEquals(oa.getSort(), tableModel.getValueAt(i, 2));
			assertEquals(oa.getFormat(), tableModel.getValueAt(i, 3));
			assertEquals(oa.getMinValue(), tableModel.getValueAt(i, 4));
			assertEquals(oa.getMaxValue(), tableModel.getValueAt(i, 5));
			assertEquals(oa.getEditFormat(), tableModel.getValueAt(i, 6));
			assertEquals(oa.getFormatValidCharacters(), tableModel.getValueAt(i, 7));
			assertEquals(oa.getFormatInvalidCharacters(), tableModel.getValueAt(i, 8));
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < tableModel.getRowCount(); i++){
			assertEquals(false, tableModel.isCellEditable(i, 0));
			assertEquals(false, tableModel.isCellEditable(i, 1));
			assertEquals(false, tableModel.isCellEditable(i, 2));
			assertEquals(true, tableModel.isCellEditable(i, 3));
			assertEquals(false, tableModel.isCellEditable(i, 4));
			assertEquals(false, tableModel.isCellEditable(i, 5));
			assertEquals(true, tableModel.isCellEditable(i, 6));
			assertEquals(true, tableModel.isCellEditable(i, 7));
			assertEquals(true, tableModel.isCellEditable(i, 8));
		}
	}

	public void testSetValueAt(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < oaList.size(); i++){
			tableModel.setValueAt("frmt", i, 3);
			tableModel.setValueAt("0", i, 4);
			tableModel.setValueAt("200", i, 5);
			tableModel.setValueAt("editFrmt", i, 6);
			tableModel.setValueAt("frmVC", i, 7);
			tableModel.setValueAt("frmIC", i, 8);

			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.getFormat(), "frmt");
			assertEquals(oa.getMinValue(), "0");
			assertEquals(oa.getMaxValue(), "200");
			assertEquals(oa.getEditFormat(), "editFrmt");
			assertEquals(oa.getFormatValidCharacters(), "frmVC");
			assertEquals(oa.getFormatInvalidCharacters(), "frmIC");
		}
	}

}
