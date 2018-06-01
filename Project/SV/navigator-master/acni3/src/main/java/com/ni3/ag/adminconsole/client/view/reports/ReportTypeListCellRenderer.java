package com.ni3.ag.adminconsole.client.view.reports;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.ReportType;

public class ReportTypeListCellRenderer extends DefaultListCellRenderer{
	private static final long serialVersionUID = 3362213004588488760L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		Object toShow = value;
		if (value instanceof ReportType){
			toShow = Translation.get(((ReportType) value).getTextID());
		}
		return super.getListCellRendererComponent(list, toShow, index, isSelected, cellHasFocus);
	}
}