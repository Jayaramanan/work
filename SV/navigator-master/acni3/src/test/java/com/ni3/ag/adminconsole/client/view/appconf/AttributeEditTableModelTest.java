/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class AttributeEditTableModelTest extends TestCase{

	private AttributeEditTableModel tableModel;
	private ObjectDefinition object;

	public void setUp(){
		object = generateObjectDefinition();
		tableModel = new AttributeEditTableModel(object.getObjectAttributes());
	}

	private ObjectDefinition generateObjectDefinition(){
		ObjectDefinition od = new ObjectDefinition();
		List<ObjectAttribute> oaList = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < 10; i++){
			ObjectAttribute oa = new ObjectAttribute();
			oa.setLabel("attr" + i);
			oa.setInMetaphor(false);
			oa.setSort(i);
			oa.setPredefined(false);
			oa.setInFilter(false);
			oa.setInLabel(false);
			oa.setInToolTip(false);
			oa.setInSimpleSearch(false);
			oa.setInAdvancedSearch(false);
			oa.setLabelBold(false);
			oa.setLabelItalic(false);
			oa.setLabelUnderline(false);
			oa.setContentBold(false);
			oa.setContentItalic(false);
			oa.setContentUnderline(false);
			oa.setInExport(false);
			oa.setInPrefilter(false);
			oa.setInContext(false);
			oa.setInMatrix(InMatrixType.Displayed.getValue());
			oa.setMatrixSort(i);
			oa.setLabelSort(i);
			oa.setFilterSort(i);
			oa.setSearchSort(i);
			oa.setAggregable(false);
			oa.setIsMultivalue(false);
			oa.setDataType(DataType.TEXT);
			oaList.add(oa);
		}
		od.setObjectAttributes(oaList);
		return od;
	}

	public void testColumnCount(){
		assertEquals(25, tableModel.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(tableModel.getRowCount(), object.getObjectAttributes().size());
	}

	public void testGetValueAt(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < oaList.size(); i++){
			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.getLabel(), tableModel.getValueAt(i, 0));
			assertEquals(oa.isInMetaphor(), tableModel.getValueAt(i, 1));
			assertEquals(oa.getSort(), tableModel.getValueAt(i, 2));
			assertEquals(oa.isPredefined(), tableModel.getValueAt(i, 3));
			assertEquals(oa.isInFilter(), tableModel.getValueAt(i, 4));
			assertEquals(oa.isInLabel(), tableModel.getValueAt(i, 5));
			assertEquals(oa.isInToolTip(), tableModel.getValueAt(i, 6));
			assertEquals(oa.isInSimpleSearch(), tableModel.getValueAt(i, 7));
			assertEquals(oa.isLabelBold(), tableModel.getValueAt(i, 8));
			assertEquals(oa.isLabelItalic(), tableModel.getValueAt(i, 9));
			assertEquals(oa.isLabelUnderline(), tableModel.getValueAt(i, 10));
			assertEquals(oa.isContentBold(), tableModel.getValueAt(i, 11));
			assertEquals(oa.isContentItalic(), tableModel.getValueAt(i, 12));
			assertEquals(oa.isContentUnderline(), tableModel.getValueAt(i, 13));
			assertEquals(oa.isInExport(), tableModel.getValueAt(i, 14));
			assertEquals(oa.isInAdvancedSearch(), tableModel.getValueAt(i, 15));
			assertEquals(oa.isInPrefilter(), tableModel.getValueAt(i, 16));
			assertEquals(oa.isInContext(), tableModel.getValueAt(i, 17));
			InMatrixType imt = (InMatrixType) tableModel.getValueAt(i, 18);
			assertEquals(oa.getInMatrix(), imt.getValue());
			assertEquals(oa.isAggregable(), tableModel.getValueAt(i, 19));
			assertEquals(oa.getIsMultivalue(), tableModel.getValueAt(i, 20));
			assertEquals(oa.getMatrixSort(), tableModel.getValueAt(i, 21));
			assertEquals(oa.getLabelSort(), tableModel.getValueAt(i, 22));
			assertEquals(oa.getFilterSort(), tableModel.getValueAt(i, 23));
			assertEquals(oa.getSearchSort(), tableModel.getValueAt(i, 24));
		}
	}

	public void testColumnEditable(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < tableModel.getRowCount(); i++){
			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.isPredefined().booleanValue(), tableModel.isCellEditable(i, 4));
			assertEquals(oa.isIntDataType() || oa.isDecimalDataType(), tableModel.isCellEditable(i, 19));
		}
	}

	public void testSetValueAt(){
		List<ObjectAttribute> oaList = object.getObjectAttributes();
		for (int i = 0; i < oaList.size(); i++){
			tableModel.setValueAt("attr" + i, i, 0);
			tableModel.setValueAt(Boolean.FALSE, i, 1);
			tableModel.setValueAt(i, i, 2);
			tableModel.setValueAt(Boolean.TRUE, i, 3);
			tableModel.setValueAt(Boolean.TRUE, i, 4);
			tableModel.setValueAt(Boolean.TRUE, i, 5);
			tableModel.setValueAt(Boolean.TRUE, i, 6);
			tableModel.setValueAt(Boolean.TRUE, i, 7);
			tableModel.setValueAt(Boolean.TRUE, i, 8);
			tableModel.setValueAt(Boolean.TRUE, i, 9);
			tableModel.setValueAt(Boolean.TRUE, i, 10);
			tableModel.setValueAt(Boolean.TRUE, i, 11);
			tableModel.setValueAt(Boolean.TRUE, i, 12);
			tableModel.setValueAt(Boolean.TRUE, i, 13);
			tableModel.setValueAt(Boolean.TRUE, i, 14);
			tableModel.setValueAt(Boolean.TRUE, i, 15);
			tableModel.setValueAt(Boolean.TRUE, i, 16);
			InMatrixType inMatrix = InMatrixType.Displayed;
			tableModel.setValueAt(inMatrix, i, 18);
			tableModel.setValueAt(Boolean.TRUE, i, 19);
			tableModel.setValueAt(Boolean.TRUE, i, 20);
			tableModel.setValueAt(i, i, 21);
			tableModel.setValueAt(i, i, 22);
			tableModel.setValueAt(i, i, 23);
			tableModel.setValueAt(i, i, 24);

			ObjectAttribute oa = oaList.get(i);
			assertEquals(oa.getLabel(), "attr" + i);
			assertEquals(oa.isInMetaphor(), Boolean.FALSE);
			assertEquals(oa.getSort().intValue(), i);
			assertEquals(oa.isPredefined(), Boolean.TRUE);
			assertEquals(oa.isInFilter(), Boolean.TRUE);
			assertEquals(oa.isInLabel(), Boolean.TRUE);
			assertEquals(oa.isInToolTip(), Boolean.TRUE);
			assertEquals(oa.isInSimpleSearch(), Boolean.TRUE);
			assertEquals(oa.isLabelBold(), Boolean.TRUE);
			assertEquals(oa.isLabelItalic(), Boolean.TRUE);
			assertEquals(oa.isLabelUnderline(), Boolean.TRUE);
			assertEquals(oa.isContentBold(), Boolean.TRUE);
			assertEquals(oa.isContentItalic(), Boolean.TRUE);
			assertEquals(oa.isContentUnderline(), Boolean.TRUE);
			assertEquals(oa.isInExport(), Boolean.TRUE);
			assertEquals(oa.isInAdvancedSearch(), Boolean.TRUE);
			assertEquals(oa.isInPrefilter(), Boolean.TRUE);
			assertEquals(oa.getInMatrix(), inMatrix.getValue());
			assertEquals(oa.getMatrixSort().intValue(), i);
			assertEquals(oa.getLabelSort().intValue(), i);
			assertEquals(oa.getFilterSort().intValue(), i);
			assertEquals(oa.getSearchSort().intValue(), i);
			assertEquals(oa.isAggregable(), true);
			assertEquals(oa.getIsMultivalue(), Boolean.TRUE);

			tableModel.setValueAt(Boolean.TRUE, i, 17);
			assertEquals(oa.isInContext(), Boolean.TRUE);
			assertEquals(oa.isInExport(), Boolean.FALSE);

		}
	}

}
