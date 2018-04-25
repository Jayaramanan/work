/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Font;

import javax.swing.JPanel;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings("serial")
public abstract class Ni3Panel extends JPanel implements Ni3ItemListener{
	protected MainPanel parentMP;
	public Ni3Document Doc;

	public Ni3Panel(){
		parentMP = null;
		Doc = null;
	}

	public Ni3Panel(MainPanel parent){
		this.parentMP = parent;
		this.Doc = parent.Doc;
	}

	public abstract int getListenerType();

	protected String getWord(String englishWord){
		return UserSettings.getWord(englishWord);
	}

	protected Font getFont(String name){
		return UserSettings.getFont(name);
	}

	public void onBeforeSubgraphChange(){

	}

	public void onSubgraphChanged(){

	}

	public void onNewSubgraph(){

	}

	public void onClearSubgraph(){

	}

	public void onGraphDirty(){

	}

	public void onSchemaChanged(int SchemaID){

	}

	public void onFilterChanged(DataFilter filter){

	}

	public void onClearSearchResult(){

	}

	public void onSubgraphObjectsRemoved(){

	}

	public void onDynamicAttributeAdded(Attribute newAttr){
	}

	public void onRemoveDynamicAttributes(){

	}

	public void onChartFilterChanged(){
	}

	public void event(int eventCode, int sourceID, Object source, Object param){
		switch (eventCode){
			case MSG_SubgraphBeforeChange:
				onBeforeSubgraphChange();
				break;

			case MSG_SubgraphChanged:
				onSubgraphChanged();
				break;

			case MSG_NewSubgraph:
				onNewSubgraph();
				break;

			case MSG_ClearSubgraph:
				onClearSubgraph();
				break;

			case MSG_SchemaChanged:
				onSchemaChanged((Integer) param);
				break;

			case MSG_FilterChanged:
				onFilterChanged((DataFilter) param);
				break;

			case MSG_GraphDirty:
				onGraphDirty();
				break;

			case MSG_ClearSearchResult:
				onClearSearchResult();
				break;

			case MSG_SubgraphObjectsRemoved:
				onSubgraphObjectsRemoved();
				break;

			case MSG_DynamicAttributeAdded:
				onDynamicAttributeAdded((Attribute) param);
				break;

			case MSG_DynamicAttributesCleared:
				onRemoveDynamicAttributes();
				break;

			case MSG_ChartFilterChanged:
				onChartFilterChanged();
				break;
		}
	}
}
