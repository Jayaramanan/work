package com.ni3.ag.licensecreator.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.ni3.ag.adminconsole.license.LicenseData;

public class PropertyTableModel implements TableModel{

	private List<Object[]> data = new ArrayList<Object[]>();
	private List<Object[]> meta = new ArrayList<Object[]>();

	public PropertyTableModel(String product){
		generateMeta();
		generateData(product);
	}

	private void generateMeta(){
		meta.add(new Object[] { "Property", String.class, false });
		meta.add(new Object[] { "User count", Integer.class, true });
	}

	private void generateData(String product){
		if (product == LicenseData.NAVIGATOR_PRODUCT){
			data.add(new Object[] { LicenseData.BASE_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.DATA_CAPTURE_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.CHARTS_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.MAPS_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.GEO_ANALYTICS_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.REMOTE_CLIENT_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.REPORTS_MODULE, new Integer(1) });
		} else if (product == LicenseData.ACNi3WEB_PRODUCT){
			data.add(new Object[] { LicenseData.ACUSERS_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACSCHEMA_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACMETAPHOR_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACLANGUAGE_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACCHART_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACGEO_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACDIAGNOSTICS_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACOFFLINE_MODULE, new Integer(1) });
			data.add(new Object[] { LicenseData.ACREPORTS_MODULE, new Integer(1) });
			//data.add(new Object[] { LicenseData.ACETL_USER_COUNT_PROPERTY, new Integer(1) });
		}

	}

	public int getRowCount(){
		return data.size();
	}

	public int getColumnCount(){
		return meta.size();
	}

	public String getColumnName(int columnIndex){
		return (String) meta.get(columnIndex)[0];
	}

	public Class<?> getColumnClass(int columnIndex){
		return (Class<?>) meta.get(columnIndex)[1];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex){
		return (Boolean) meta.get(columnIndex)[2];
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		return data.get(rowIndex)[columnIndex];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		data.get(rowIndex)[columnIndex] = aValue;
	}

	public void addTableModelListener(TableModelListener l){
	}

	public void removeTableModelListener(TableModelListener l){
	}

	public List<Object[]> getData(){
		return data;
	}

}
