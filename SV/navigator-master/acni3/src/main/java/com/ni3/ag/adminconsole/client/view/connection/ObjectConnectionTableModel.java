/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ObjectConnectionTableModel extends ACTableModel{
	private static final long serialVersionUID = -5001273716317207718L;

	private List<ObjectConnection> objectConnections = new ArrayList<ObjectConnection>();

	public ObjectConnectionTableModel(){
		addColumn(Translation.get(TextID.ConnectionType), true, PredefinedAttribute.class, true);
		addColumn(Translation.get(TextID.FromObject), true, ObjectDefinition.class, true);
		addColumn(Translation.get(TextID.ToObject), true, ObjectDefinition.class, true);
		addColumn(Translation.get(TextID.LineStyle), true, LineStyle.class, true);
		addColumn(Translation.get(TextID.LineColor), true, String.class, true);
		addColumn(Translation.get(TextID.LineWeight), true, LineWeight.class, false);
		addColumn(Translation.get(TextID.HierarchicalEdge), true, Boolean.class, false);
	}

	public ObjectConnectionTableModel(List<ObjectConnection> objectConnections){
		this();
		setData(objectConnections);
	}

	public int getRowCount(){
		return objectConnections.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		ObjectConnection objectConnection = objectConnections.get(rowIndex);
		switch (columnIndex){
			case 0:
				return objectConnection.getConnectionType();
			case 1:
				return objectConnection.getFromObject();
			case 2:
				return objectConnection.getToObject();
			case 3:
				return objectConnection.getLineStyle();
			case 4:
				return objectConnection.getRgb();
			case 5:
				return objectConnection.getLineWeight();
			case 6:
				return objectConnection.isHierarchical();

			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		super.setValueAt(value, rowIndex, columnIndex);
		ObjectConnection objectConnection = objectConnections.get(rowIndex);
		switch (columnIndex){
			case 0:
				objectConnection.setConnectionType((PredefinedAttribute) value);
				break;
			case 1:
				objectConnection.setFromObject((ObjectDefinition) value);
				break;
			case 2:
				objectConnection.setToObject((ObjectDefinition) value);
				break;
			case 3:
				objectConnection.setLineStyle((LineStyle) value);
				break;
			case 4:
				objectConnection.setRgb((String) value);
				break;
			case 5:
				objectConnection.setLineWeight((LineWeight) value);
				break;
			case 6:
				objectConnection.setHierarchical((Boolean) value);
				break;

			default:
				break;
		}
	}

	public ObjectConnection getSelectedConnection(int rowIndex){
		if (rowIndex >= 0 && rowIndex < objectConnections.size()){
			return objectConnections.get(rowIndex);
		}
		return null;
	}

	public int indexOf(ObjectConnection newConnection){
		return objectConnections.indexOf(newConnection);
	}

	public void setData(List<ObjectConnection> connections){
		this.objectConnections = connections;
	}

}
