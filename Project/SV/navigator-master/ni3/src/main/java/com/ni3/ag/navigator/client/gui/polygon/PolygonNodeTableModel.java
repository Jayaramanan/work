package com.ni3.ag.navigator.client.gui.polygon;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.graph.Node;

public class PolygonNodeTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	private static final int NODE_COLUMN_INDEX = 0;
	public static final int COLOR_COLUMN_INDEX = 1;

	private static final String[] columnsNames = new String[] { UserSettings.getWord("Node"), UserSettings.getWord("Color") };
	private static final Class<?>[] columnClasses = new Class[] { String.class, Color.class };

	private List<Node> nodes;
	private Map<Integer, Color> colorMap;

	@Override
	public Class<?> getColumnClass(int columnIndex){
		return columnClasses[columnIndex];
	}

	public PolygonNodeTableModel(List<Node> nodes, Map<Integer, Color> colorMap){
		this.nodes = nodes;
		this.colorMap = colorMap;
	}

	public void setData(List<Node> nodes, Map<Integer, Color> colorMap){
		this.nodes = nodes;
		this.colorMap = colorMap;
		fireTableDataChanged();
	}

	public int getColumnCount(){
		return columnsNames.length;
	}

	public int getRowCount(){
		if (nodes == null)
			return 0;
		return nodes.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		Object value = null;
		final Node node = nodes.get(rowIndex);
		switch (columnIndex){
			case NODE_COLUMN_INDEX:
				value = node.Obj.getLabel();
				break;
			case COLOR_COLUMN_INDEX:
				value = colorMap.get(node.ID);
				break;
			default:
				break;
		}
		return value;
	}

	@Override
	public String getColumnName(int column){
		return columnsNames[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		return columnIndex == COLOR_COLUMN_INDEX;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		final Node node = nodes.get(rowIndex);
		switch (columnIndex){
			case COLOR_COLUMN_INDEX:
				colorMap.put(node.ID, (Color) aValue);
				fireTableCellUpdated(rowIndex, columnIndex);
				break;
			default:
				break;
		}
	}

}
