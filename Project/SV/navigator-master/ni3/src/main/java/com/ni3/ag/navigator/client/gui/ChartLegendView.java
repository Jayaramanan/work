/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.domain.ChartParams;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class ChartLegendView implements Ni3ItemListener{

	private List<LegendFrame> legends;
	private Ni3Document doc;

	public ChartLegendView(Ni3Document doc){
		this.doc = doc;
	}

	public void setLegends(List<LegendFrame> legends){
		this.legends = legends;
	}

	private List<Entity> getChartEntities(int chartId){
		final List<Entity> entities = new ArrayList<Entity>();
		if (chartId == SNA.SNA_CHART_ID){
			entities.add(doc.DB.schema.getEntity(Entity.COMMON_ENTITY_ID));
		} else{
			final Map<Integer, ChartParams> attrMap = doc.getChartParams();
			for (Integer entityId : attrMap.keySet()){
				Entity ent = doc.DB.schema.getEntity(entityId);
				if (ent != null){
					entities.add(ent);
				}
			}
		}
		return entities;
	}

	private void initLegends(int chartId){
		final List<Entity> entities = getChartEntities(chartId);
		checkCurrentLegends(entities);
		for (Entity entity : entities){
			String title = UserSettings.getWord(doc.getChartParams(entity.ID).getTitle());
			final LegendFrame legend = getLegend(entity);
			legend.initialize(chartId, title, doc.filter);
			legend.setVisible(true);
			doc.setChartLegendVisible(entity.ID, true);
		}
	}

	private LegendFrame getLegend(final Entity entity){
		LegendFrame legend = null;
		if (legends != null){
			for (LegendFrame l : legends){
				if (l.getEntity().ID == entity.ID){
					legend = l;
					break;
				}
			}
		} else{
			legends = new ArrayList<LegendFrame>();
		}
		if (legend == null){
			legend = new LegendFrame(doc, entity);
			legends.add(legend);
		}
		return legend;
	}

	public void checkCurrentLegends(final List<Entity> entities){
		int i = 0;
		while (legends != null && i < legends.size()){
			LegendFrame legend = legends.get(i);
			if (!entities.contains(legend.getEntity())){
				disposeLegend(legend);
				legends.remove(legend);
			} else{
				i++;
			}
		}
	}

	private void updateLegendVisibility(){
		for (Integer entityId : doc.getChartParams().keySet()){
			for (LegendFrame legend : legends){
				final ChartParams chartParams = doc.getChartParams(entityId);
				if (legend.getEntity().ID == entityId && legend.isVisible() != chartParams.isLegendVisible()){
					legend.setVisible(chartParams.isLegendVisible());
					break;
				}
			}
		}
	}

	private void disposeLegends(){
		if (legends != null){
			for (LegendFrame lf : legends){
				disposeLegend(lf);
			}
			legends.clear();
			legends = null;
		}
	}

	private void disposeLegend(LegendFrame legend){
		if (legend != null){
			doc.unregisterListener(legend);
			legend.setVisible(false);
			legend.dispose();
		}
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		switch (eventCode){
			case MSG_ChartLegendVisibilityChanged:
				updateLegendVisibility();
				break;
			case MSG_ChartChanged:
				if (doc.getCurrentChartId() == 0){
					disposeLegends();
				} else{
					initLegends(doc.getCurrentChartId());
				}
				break;
		}
	}

	@Override
	public int getListenerType(){
		return SRC_ChartLegendView;
	}

}
