package com.ni3.ag.adminconsole.client.view.charts;

import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionListener;

import com.ni3.ag.adminconsole.client.controller.charts.ObjectChartTableSelectionListener;

public class ObjectChartTableSelectionModel extends DefaultListSelectionModel{

	private static final long serialVersionUID = 1L;

	private int oldIndex = -1;

	private ObjectChartTableSelectionListener listener;

	public void addListSelectionListener(ListSelectionListener listener){
		if (listener instanceof ObjectChartTableSelectionListener)
			this.listener = (ObjectChartTableSelectionListener) listener;
		super.addListSelectionListener(listener);
	}

	public void setSelectionInterval(int index0, int index1){
		if (oldIndex == index0 || listener == null)
			super.setSelectionInterval(index0, index0);
		else{
			if (listener.canSwitch()){
				super.setSelectionInterval(index0, index0);
				oldIndex = index0;
			}
		}
	}
}