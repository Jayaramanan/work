/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.GisTerritory;

public class GisTerritoryTableModelTest extends TestCase{
	private GisTerritoryTableModel model;
	private List<GisTerritory> territories;

	public void setUp(){
		territories = generateTerritories();
		model = new GisTerritoryTableModel(territories);
	}

	private List<GisTerritory> generateTerritories(){
		List<GisTerritory> territories = new ArrayList<GisTerritory>();
		for (int i = 0; i < 10; i++){
			GisTerritory gt = new GisTerritory(i, "terr" + i, "terrL" + i);
			gt.setSort(i);
			territories.add(gt);
		}
		return territories;
	}

	public void testColumnCount(){
		assertEquals(6, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), territories.size());
	}

	public void testValueAt(){
		for (int i = 0; i < territories.size(); i++){
			GisTerritory gt = territories.get(i);
			assertEquals(gt.getTerritory(), model.getValueAt(i, 0));
			assertEquals(gt.getLabel(), model.getValueAt(i, 1));
			assertEquals(gt.getSort(), model.getValueAt(i, 2));
			assertEquals(gt.getTableName(), model.getValueAt(i, 3));
			assertEquals(gt.getDisplayColumn(), model.getValueAt(i, 4));
			assertEquals(gt.getVersion(), model.getValueAt(i, 5));
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
		}
	}

	public void testSetValueAt(){
		for (int i = 0; i < territories.size(); i++){
			model.setValueAt("newTerr" + i, i, 0);
			model.setValueAt("newLab" + i, i, 1);
			model.setValueAt(i + 1, i, 2);
			model.setValueAt("table" + i, i, 3);
			model.setValueAt("column" + i, i, 4);
			model.setValueAt(1 + i, i, 5);

			GisTerritory gt = territories.get(i);
			assertEquals(gt.getTerritory(), "newTerr" + i);
			assertEquals(gt.getLabel(), "newLab" + i);
			assertEquals(gt.getSort().intValue(), i + 1);
			assertEquals(gt.getTableName(), "table" + i);
			assertEquals(gt.getDisplayColumn(), "column" + i);
			assertEquals(gt.getVersion().intValue(), 1 + i);
		}
	}

}
