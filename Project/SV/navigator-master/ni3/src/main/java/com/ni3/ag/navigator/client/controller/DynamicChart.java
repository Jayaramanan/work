/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.ChartParams;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.ChartAttributeDescriptor;
import com.ni3.ag.navigator.client.gui.dynamiccharts.DynamicChartDialog;

public class DynamicChart{
	public static final int DYNAMIC_CHART_ID = -3;
	private DynamicChartDialog dlg;
	private Palette colorPalette;

	private Map<Entity, List<DynamicChartAttribute>> attributeMap;

	public DynamicChart(final Map<Integer, ChartParams> paramsMap, List<Entity> allEntities){
		colorPalette = new Palette(1);
		if (dlg == null){
			dlg = new DynamicChartDialog();
			dlg.attTabChangeListener(new TabChangeListener());
		}

		fillAttributeMap(allEntities);

		if (paramsMap != null && !paramsMap.isEmpty()){
			restoreSelection(paramsMap);
		}
		dlg.initTabs(attributeMap.keySet());
		refreshTableModel();
	}

	public boolean showDynamicChartDialog(){
		boolean result = false;

		dlg.showDialog();

		if (dlg.isOkPressed() && hasSelectedAttributes()){
			result = true;
		}
		return result;
	}

	private void fillAttributeMap(List<Entity> entities){
		attributeMap = new LinkedHashMap<Entity, List<DynamicChartAttribute>>();
		for (Entity entity : entities){
			if (entity.CanRead && entity.isNode()){
				List<DynamicChartAttribute> attributes = new ArrayList<DynamicChartAttribute>();
				for (Attribute attr : entity.getReadableAttributes()){
					if (!attr.predefined && attr.isNumericAttribute() && !attr.isSystemAttribute() && attr.isAggregable()
					        && !attr.inContext){
						DynamicChartAttribute dAttr = new DynamicChartAttribute(attr);
						attributes.add(dAttr);
					}
				}
				attributeMap.put(entity, attributes);
			}
		}
	}

	private void restoreSelection(Map<Integer, ChartParams> paramsMap){
		for (Integer entityId : paramsMap.keySet()){
			final ChartParams chartParams = paramsMap.get(entityId);
			final List<ChartAttributeDescriptor> chartAttributes = chartParams.getChartAttributes();
			for (ChartAttributeDescriptor ca : chartAttributes){
				selectAttribute(ca.getAttribute(), ca.getColor());
			}
		}
	}

	private boolean hasSelectedAttributes(){
		boolean hasSelected = false;
		for (List<DynamicChartAttribute> attributes : attributeMap.values()){
			for (DynamicChartAttribute attr : attributes){
				if (attr.isSelected()){
					hasSelected = true;
					break;
				}
			}
		}
		return hasSelected;
	}

	private void setCurrentEntity(Entity entity){
		final List<DynamicChartAttribute> attributes = attributeMap.get(entity);
		dlg.getTableModel().setData(attributes, colorPalette);
	}

	public Map<Integer, List<DynamicChartAttribute>> getSelectedAttributeMap(){
		Map<Integer, List<DynamicChartAttribute>> map = new LinkedHashMap<Integer, List<DynamicChartAttribute>>();
		for (Entity entity : attributeMap.keySet()){
			List<DynamicChartAttribute> selectedAttributes = new ArrayList<DynamicChartAttribute>();
			final List<DynamicChartAttribute> attributes = attributeMap.get(entity);
			if (attributes != null){
				for (DynamicChartAttribute attr : attributes){
					if (attr.isSelected()){
						selectedAttributes.add(attr);
					}
				}
			}
			if (!selectedAttributes.isEmpty()){
				map.put(entity.ID, selectedAttributes);
			}
		}
		return map;
	}

	public void reset(){
		attributeMap = null;
		colorPalette.resetPalette();
	}

	private void selectAttribute(Attribute attribute, Color color){
		final List<DynamicChartAttribute> list = attributeMap.get(attribute.ent);
		for (DynamicChartAttribute dAttr : list){
			if (dAttr.getAttribute().ID == attribute.ID){
				dAttr.setSelected(true);
				dAttr.setColor(color);
			}
		}
	}

	private void refreshTableModel(){
		int index = dlg.getSelectedTabIndex();
		if (index >= 0){
			final Set<Entity> entities = attributeMap.keySet();
			final Entity entity = (Entity) entities.toArray()[index];
			setCurrentEntity(entity);
		}
	}

	private class TabChangeListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e){
			final JTabbedPane tp = (JTabbedPane) e.getSource();
			final int index = tp.getSelectedIndex();
			if (index >= 0){
				final Set<Entity> entities = attributeMap.keySet();
				final Entity entity = (Entity) entities.toArray()[index];
				setCurrentEntity(entity);
			}
		}
	}
}
