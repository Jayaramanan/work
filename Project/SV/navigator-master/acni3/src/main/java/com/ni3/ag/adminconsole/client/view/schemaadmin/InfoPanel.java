/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.UpdateDatasourceButtonListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTextField;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class InfoPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private static final int HOST_FIELD_INDEX = 0;
	private static final int DATABASE_FIELD_INDEX = 1;
	private static final int DATASOURCE_NAMES_FIELD_INDEX = 2;
	private static final int EXPECTED_VERSION_FIELD_INDEX = 3;
	private static final int ACTUAL_VERSION_FIELD_INDEX = 4;
	private static final int MAPPATH_FIELD_INDEX = 5;
	private static final int DOCROOT_FIELD_INDEX = 6;
	private static final int RASTER_SERVER_FIELD_INDEX = 7;
	private static final int MODULE_PATH_FIELD_INDEX = 8;
	private static final int DELTA_THRESHOLD_FIELD_INDEX = 9;
	private static final int DELTA_OUT_THRESHOLD_FIELD_INDEX = 10;
	private static final int LICENCE_EXPIRING_SOON_FIELD_INDEX = 11;
	private static final int CACHE_REQUIRES_REFRESH_FIELD_INDEX = 12;

	private static final int LAST_FIELD_INDEX = LICENCE_EXPIRING_SOON_FIELD_INDEX;
	private static final int FIELD_COUNT = LAST_FIELD_INDEX + 1;
	private JLabel titleLabel;
	private JLabel[] labels = new JLabel[FIELD_COUNT + 1];

	private ACTextField dbIDTField;
	private ACTextField dataSourceNameTField;
	private ACTextField navigatorHostTField;
	private ACTextField mappathTField;
	private ACTextField docrootTField;
	private ACTextField rasterServerTField;
	private ACTextField modulePathTField;
	private ACTextField deltaWarnThresholdTField;
	private ACTextField deltaErrThresholdTField;
	private ACTextField deltaOutWarnThresholdTField;
	private ACTextField deltaOutErrThresholdTField;
	private JLabel dbExpectedVersionLabel, dbActualVersionLabel, licenseExpiringSoonLabel;

	private ACButton updateButton, refreshButton;

	public InfoPanel(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		ACToolBar toolBar = new ACToolBar();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();
		layout.putConstraint(SpringLayout.NORTH, toolBar, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);
		add(toolBar);

		titleLabel = new JLabel();
		titleLabel.setText(Translation.get(TextID.DatabaseInstance));
		layout.putConstraint(SpringLayout.NORTH, titleLabel, 10, SpringLayout.SOUTH, toolBar);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, titleLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);

		labels[HOST_FIELD_INDEX] = new JLabel(Translation.get(TextID.NavigatorHost) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[HOST_FIELD_INDEX], 10, SpringLayout.SOUTH, titleLabel);
		layout.putConstraint(SpringLayout.WEST, labels[HOST_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[DATABASE_FIELD_INDEX] = new JLabel(Translation.get(TextID.DatabaseId) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[DATABASE_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[HOST_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[DATABASE_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[DATASOURCE_NAMES_FIELD_INDEX] = new JLabel(Translation.get(TextID.Datasource) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[DATASOURCE_NAMES_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[DATABASE_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[DATASOURCE_NAMES_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[EXPECTED_VERSION_FIELD_INDEX] = new JLabel(Translation.get(TextID.ExpectedDatabaseVersion) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[EXPECTED_VERSION_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[DATASOURCE_NAMES_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[EXPECTED_VERSION_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[ACTUAL_VERSION_FIELD_INDEX] = new JLabel(Translation.get(TextID.ActualDatabaseVersion) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[ACTUAL_VERSION_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[EXPECTED_VERSION_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[ACTUAL_VERSION_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[MAPPATH_FIELD_INDEX] = new JLabel(Translation.get(TextID.PathToMaps) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[MAPPATH_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[ACTUAL_VERSION_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[MAPPATH_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[DOCROOT_FIELD_INDEX] = new JLabel(Translation.get(TextID.Docroot) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[DOCROOT_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[MAPPATH_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[DOCROOT_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[RASTER_SERVER_FIELD_INDEX] = new JLabel(Translation.get(TextID.RasterServerUrl) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[RASTER_SERVER_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[DOCROOT_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[RASTER_SERVER_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[MODULE_PATH_FIELD_INDEX] = new JLabel(Translation.get(TextID.ModulesPath) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[MODULE_PATH_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[RASTER_SERVER_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[MODULE_PATH_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[DELTA_THRESHOLD_FIELD_INDEX] = new JLabel(Translation.get(TextID.DeltaThreshold) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[DELTA_THRESHOLD_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[MODULE_PATH_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[DELTA_THRESHOLD_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[DELTA_OUT_THRESHOLD_FIELD_INDEX] = new JLabel(Translation.get(TextID.DeltaOutThreshold) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[DELTA_OUT_THRESHOLD_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[DELTA_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[DELTA_OUT_THRESHOLD_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[LICENCE_EXPIRING_SOON_FIELD_INDEX] = new JLabel(Translation.get(TextID.LicenseExpiringSoon) + ":");
		layout.putConstraint(SpringLayout.NORTH, labels[LICENCE_EXPIRING_SOON_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[DELTA_OUT_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[LICENCE_EXPIRING_SOON_FIELD_INDEX], 10, SpringLayout.WEST, this);

		labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX] = new JLabel(Translation.get(TextID.NavigatorCacheRequiresRefresh));
		layout.putConstraint(SpringLayout.NORTH, labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX], 10, SpringLayout.SOUTH,
		        labels[LICENCE_EXPIRING_SOON_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX], 10, SpringLayout.WEST, this);

		initTextFields();

		add(dbActualVersionLabel = new JLabel());
		add(dbExpectedVersionLabel = new JLabel());
		add(licenseExpiringSoonLabel = new JLabel());
		licenseExpiringSoonLabel.setBackground(Color.yellow);
		licenseExpiringSoonLabel.setOpaque(true);
		labels[LICENCE_EXPIRING_SOON_FIELD_INDEX].setBackground(Color.yellow);
		labels[LICENCE_EXPIRING_SOON_FIELD_INDEX].setOpaque(true);
		showExpiringLicenceLabel(null);
		labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX].setBackground(Color.yellow);
		labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX].setOpaque(true);

		add(titleLabel);
		add(labels[HOST_FIELD_INDEX]);
		add(labels[DATABASE_FIELD_INDEX]);
		add(labels[DATASOURCE_NAMES_FIELD_INDEX]);
		add(labels[EXPECTED_VERSION_FIELD_INDEX]);
		add(labels[ACTUAL_VERSION_FIELD_INDEX]);
		add(labels[RASTER_SERVER_FIELD_INDEX]);
		add(labels[MODULE_PATH_FIELD_INDEX]);		
		add(labels[MAPPATH_FIELD_INDEX]);
		add(labels[DOCROOT_FIELD_INDEX]);
		add(labels[DELTA_THRESHOLD_FIELD_INDEX]);
		add(labels[DELTA_OUT_THRESHOLD_FIELD_INDEX]);
		add(labels[LICENCE_EXPIRING_SOON_FIELD_INDEX]);
		add(labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX]);
		setDeltaThreshold("100/1000");
		setDeltaOutThreshold("100/1000");
		realignFields();
		setCacheRequiresRefresh(false);
	}

	private void initTextFields(){
		add(dbIDTField = new ACTextField());
		dbIDTField.setName("schemaAdmin_database");
		add(dataSourceNameTField = new ACTextField());
		dataSourceNameTField.setName("schemaAdmin_datasourceNames");
		add(navigatorHostTField = new ACTextField());
		navigatorHostTField.setName("schemaAdmin_host");
		add(mappathTField = new ACTextField());
		mappathTField.setName("schemaAdmin_mappath");
		add(docrootTField = new ACTextField());
		docrootTField.setName("schemaAdmin_docroot");
		add(rasterServerTField = new ACTextField());
		rasterServerTField.setName("schemaAdmin_rasterServer");
		add(deltaWarnThresholdTField = new ACTextField());
		deltaWarnThresholdTField.setName("schemaAdmin_deltaWarnThreshold");
		add(deltaErrThresholdTField = new ACTextField());
		deltaErrThresholdTField.setName("schemaAdmin_deltaErrThreshold");
		add(deltaOutWarnThresholdTField = new ACTextField());
		deltaOutWarnThresholdTField.setName("schemaAdmin_deltaOutWarnThreshold");
		add(deltaOutErrThresholdTField = new ACTextField());
		deltaOutErrThresholdTField.setName("schemaAdmin_deltaOutErrThreshold");		
		add(modulePathTField = new ACTextField());
	}

	public void setNavigatorHost(String host){
		navigatorHostTField.setText(host);
	}

	public void setDatabaseId(String databaseName){
		dbIDTField.setText(databaseName);
	}

	public void setExpiringLicence(String licence){
		licenseExpiringSoonLabel.setText(licence);
	}

	private void setExpectedVersion(String expectedVersion){
		dbExpectedVersionLabel.setText(expectedVersion);
	}

	private void setActualVersion(String actualVersion){
		dbActualVersionLabel.setText(actualVersion);
	}

	public void setCacheRequiresRefresh(boolean requires){
		labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX].setVisible(requires);
	}

	public void realignFields(){
		JLabel biggest = getBiggestWidthLabel();

		SpringLayout layout = (SpringLayout) getLayout();
		layout.putConstraint(SpringLayout.NORTH, navigatorHostTField, 0, SpringLayout.NORTH, labels[HOST_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, navigatorHostTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, navigatorHostTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, dbIDTField, 0, SpringLayout.NORTH, labels[DATABASE_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, dbIDTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, dbIDTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, dataSourceNameTField, 0, SpringLayout.NORTH,
		        labels[DATASOURCE_NAMES_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, dataSourceNameTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, dataSourceNameTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, dbExpectedVersionLabel, 0, SpringLayout.NORTH,
		        labels[EXPECTED_VERSION_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, dbExpectedVersionLabel, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, dbExpectedVersionLabel, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, dbActualVersionLabel, 0, SpringLayout.NORTH,
		        labels[ACTUAL_VERSION_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, dbActualVersionLabel, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, dbActualVersionLabel, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, mappathTField, 0, SpringLayout.NORTH, labels[MAPPATH_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, mappathTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, mappathTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, docrootTField, 0, SpringLayout.NORTH, labels[DOCROOT_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, docrootTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, docrootTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, rasterServerTField, 0, SpringLayout.NORTH, labels[RASTER_SERVER_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, rasterServerTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, rasterServerTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, modulePathTField, 0, SpringLayout.NORTH, labels[MODULE_PATH_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, modulePathTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, modulePathTField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, deltaWarnThresholdTField, 0, SpringLayout.NORTH,
		        labels[DELTA_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, deltaWarnThresholdTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, deltaWarnThresholdTField, 50, SpringLayout.WEST, deltaWarnThresholdTField);
		JLabel deltaDelimLabel = new JLabel("/");
		add(deltaDelimLabel);
		layout.putConstraint(SpringLayout.NORTH, deltaDelimLabel, 2, SpringLayout.NORTH, labels[DELTA_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, deltaDelimLabel, 2, SpringLayout.EAST, deltaWarnThresholdTField);
		layout.putConstraint(SpringLayout.EAST, deltaDelimLabel, 8, SpringLayout.WEST, deltaDelimLabel);
		layout.putConstraint(SpringLayout.NORTH, deltaErrThresholdTField, 0, SpringLayout.NORTH, labels[DELTA_THRESHOLD_FIELD_INDEX]);

		layout.putConstraint(SpringLayout.WEST, deltaErrThresholdTField, 10, SpringLayout.EAST, deltaWarnThresholdTField);
		layout.putConstraint(SpringLayout.EAST, deltaErrThresholdTField, 50, SpringLayout.WEST, deltaErrThresholdTField);

		layout.putConstraint(SpringLayout.NORTH, deltaOutWarnThresholdTField, 0, SpringLayout.NORTH,
		        labels[DELTA_OUT_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, deltaOutWarnThresholdTField, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, deltaOutWarnThresholdTField, 50, SpringLayout.WEST,
		        deltaOutWarnThresholdTField);
		JLabel deltaOutDelimLabel = new JLabel("/");
		add(deltaOutDelimLabel);
		layout.putConstraint(SpringLayout.NORTH, deltaOutDelimLabel, 2, SpringLayout.NORTH,
		        labels[DELTA_OUT_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, deltaOutDelimLabel, 2, SpringLayout.EAST, deltaOutWarnThresholdTField);
		layout.putConstraint(SpringLayout.EAST, deltaOutDelimLabel, 8, SpringLayout.WEST, deltaOutDelimLabel);
		layout.putConstraint(SpringLayout.NORTH, deltaOutErrThresholdTField, 0, SpringLayout.NORTH,
		        labels[DELTA_OUT_THRESHOLD_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, deltaOutErrThresholdTField, 10, SpringLayout.EAST,
		        deltaOutWarnThresholdTField);
		layout.putConstraint(SpringLayout.EAST, deltaOutErrThresholdTField, 50, SpringLayout.WEST,
		        deltaOutErrThresholdTField);

		layout.putConstraint(SpringLayout.NORTH, licenseExpiringSoonLabel, 0, SpringLayout.NORTH,
		        labels[LICENCE_EXPIRING_SOON_FIELD_INDEX]);
		layout.putConstraint(SpringLayout.WEST, licenseExpiringSoonLabel, 10, SpringLayout.EAST, biggest);
		layout.putConstraint(SpringLayout.EAST, licenseExpiringSoonLabel, -10, SpringLayout.EAST, this);

		doLayout();
	}

	private JLabel getBiggestWidthLabel(){
		int maxWidth = 0;
		int maxIndex = 0;
		for (int i = 0; i < FIELD_COUNT; i++)
			if (labels[i].getSize().width > maxWidth){
				maxWidth = labels[i].getSize().width;
				maxIndex = i;
			}
		return labels[maxIndex];
	}

	public void setVersions(String expectedVersion, String actualVersion){
		setExpectedVersion(expectedVersion);
		setActualVersion(actualVersion);

		int[] actual = parseVersion(actualVersion);
		int[] expected = parseVersion(expectedVersion);

		Font f = dbActualVersionLabel.getFont();
		if (actual[0] != expected[0] || actual[1] != expected[1]){
			dbActualVersionLabel.setOpaque(false);
			dbActualVersionLabel.setForeground(Color.red);
			dbActualVersionLabel.setBackground(this.getBackground());
			dbActualVersionLabel.setFont(f.deriveFont(Font.BOLD));
		} else if (actual[2] != expected[2]){
			dbActualVersionLabel.setOpaque(true);
			dbActualVersionLabel.setForeground(this.getForeground());
			dbActualVersionLabel.setBackground(Color.yellow);
			dbActualVersionLabel.setFont(f.deriveFont(Font.PLAIN));
		} else{
			dbActualVersionLabel.setOpaque(false);
			dbActualVersionLabel.setForeground(this.getForeground());
			dbActualVersionLabel.setBackground(this.getBackground());
			dbActualVersionLabel.setFont(f.deriveFont(Font.PLAIN));
		}
	}

	private int[] parseVersion(String version){
		StringTokenizer strt = new StringTokenizer(version, ".");
		String astr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		String bstr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		String cstr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		return new int[] { Integer.parseInt(astr), Integer.parseInt(bstr), Integer.parseInt(cstr) };
	}

	public void setDatasourceNames(List<String> datasourceNames){
		String names = "";
		if (datasourceNames != null && !datasourceNames.isEmpty()){
			boolean isCluster = false;
			for (String name : datasourceNames){
				if (isCluster)
					names += ", ";
				names += name;
				isCluster = true;
			}
		}
		dataSourceNameTField.setText(names);
	}

	public void setMappath(String mappath){
		mappathTField.setText(mappath);
	}

	public void setDocroot(String docroot){
		docrootTField.setText(docroot);
	}

	public void setRasterServerUrl(String rasterServerUrl){
		rasterServerTField.setText(rasterServerUrl);
	}

	public void setDeltaThreshold(String deltaThreshold){
		if (deltaThreshold == null)
			return;
		String[] deltas = deltaThreshold.split("/");
		deltaWarnThresholdTField.setText(deltas[0]);
		deltaErrThresholdTField.setText(deltas.length > 1 ? deltas[1] : "");
	}

	public void setDeltaOutThreshold(String deltaOutThreshold){
		if (deltaOutThreshold == null)
			return;
		String[] deltas = deltaOutThreshold.split("/");
		deltaOutWarnThresholdTField.setText(deltas[0]);
		deltaOutErrThresholdTField.setText(deltas.length > 1 ? deltas[1] : "");
	}

	public void showExpiringLicenceLabel(List<String> expiringLicenses){
		if (expiringLicenses == null || expiringLicenses.isEmpty()){
			labels[LICENCE_EXPIRING_SOON_FIELD_INDEX].setVisible(false);
			licenseExpiringSoonLabel.setVisible(false);
		} else{
			labels[LICENCE_EXPIRING_SOON_FIELD_INDEX].setVisible(true);
			licenseExpiringSoonLabel.setVisible(true);
			String text = "";
			for (String lic : expiringLicenses){
				if (!text.isEmpty())
					text += ", ";
				text += lic;
			}
			licenseExpiringSoonLabel.setText(text);
		}
	}

	public void resetLabels(){
		titleLabel.setText(Translation.get(TextID.DatabaseInstance));
		labels[HOST_FIELD_INDEX].setText(Translation.get(TextID.NavigatorHost) + ":");
		labels[DATABASE_FIELD_INDEX].setText(Translation.get(TextID.DatabaseId) + ":");
		labels[DATASOURCE_NAMES_FIELD_INDEX].setText(Translation.get(TextID.Datasource) + ":");
		labels[EXPECTED_VERSION_FIELD_INDEX].setText(Translation.get(TextID.ExpectedDatabaseVersion) + ":");
		labels[ACTUAL_VERSION_FIELD_INDEX].setText(Translation.get(TextID.ActualDatabaseVersion) + ":");
		labels[MAPPATH_FIELD_INDEX].setText(Translation.get(TextID.PathToMaps) + ":");
		labels[DOCROOT_FIELD_INDEX].setText(Translation.get(TextID.Docroot) + ":");
		labels[RASTER_SERVER_FIELD_INDEX].setText(Translation.get(TextID.RasterServerUrl) + ":");
		labels[MODULE_PATH_FIELD_INDEX].setText(Translation.get(TextID.ModulesPath) + ":");
		labels[DELTA_THRESHOLD_FIELD_INDEX].setText(Translation.get(TextID.DeltaThreshold) + ":");
		labels[DELTA_OUT_THRESHOLD_FIELD_INDEX].setText(Translation.get(TextID.DeltaOutThreshold) + ":");
		labels[LICENCE_EXPIRING_SOON_FIELD_INDEX].setText(Translation.get(TextID.LicenseExpiringSoon) + ":");
		labels[CACHE_REQUIRES_REFRESH_FIELD_INDEX].setText(Translation.get(TextID.NavigatorCacheRequiresRefresh));
	}

	public void addUpdateDatasourceButtonListener(UpdateDatasourceButtonListener listener){
		updateButton.addActionListener(listener);
	}

	public void addRefreshButtonListener(ActionListener listener){
		refreshButton.addActionListener(listener);
	}

	public String getDeltaThreshold(){
		String warn = deltaWarnThresholdTField.getText();
		if ("".equals(warn.trim()))
			warn = "0";
		String err = deltaErrThresholdTField.getText();
		if ("".equals(err.trim()))
			err = "0";
		return warn + "/" + err;
	}

	public String getDeltaOutThreshold(){
		String warn = deltaOutWarnThresholdTField.getText();
		if ("".equals(warn.trim()))
			warn = "0";
		String err = deltaOutErrThresholdTField.getText();
		if ("".equals(err.trim()))
			err = "0";
		return warn + "/" + err;
	}

	public String getDatabaseId(){
		return dbIDTField.getText();
	}

	public String getDatasourceNames(){
		return dataSourceNameTField.getText().trim();
	}

	public String getNavigatorHost(){
		return navigatorHostTField.getText();
	}

	public String getMappath(){
		return mappathTField.getText();
	}

	public String getDocroot(){
		return docrootTField.getText();
	}

	public String getRasterServer(){
		return rasterServerTField.getText();
	}

	public void setModulePath(String modulePath){
		modulePathTField.setText(modulePath);	    
    }

	public String getModulePath(){
		return modulePathTField.getText();
    }

}
