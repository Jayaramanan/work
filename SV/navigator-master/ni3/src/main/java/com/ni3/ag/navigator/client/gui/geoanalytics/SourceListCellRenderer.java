package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.shared.domain.GeoObjectSource;

public class SourceListCellRenderer extends DefaultListCellRenderer{
	private static final long serialVersionUID = -127370649510316820L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object toShow = value;
		if (value instanceof GeoObjectSource){
			toShow = UserSettings.getWord(((GeoObjectSource) value).getValue());
		}
		return super.getListCellRendererComponent(list, toShow, index, isSelected, cellHasFocus);
	}
}