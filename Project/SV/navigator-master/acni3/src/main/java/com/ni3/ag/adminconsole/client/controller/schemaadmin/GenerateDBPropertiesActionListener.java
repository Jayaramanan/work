/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseSettingsService;

public class GenerateDBPropertiesActionListener extends ProgressActionListener{

	private final static String DBID_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.dbid";
	private final static String DATASOURCE_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.datasource%2%";
	private final static String NAVIGATOR_HOST_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.navigator.host";
	private final static String RASTER_SERVER_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.rasterserver";
	private final static String MAPPATH_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.mappath";
	private final static String DOCROOT_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.docroot";
	private final static String DELTA_THRESHOLD_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.delta.threshold";
	private final static String DELTA_OUT_THRESHOLD_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.deltaOut.threshold";
	private final static String MODULE_PATH_PROPERTY = "com.ni3.ag.adminconsole.instance%1%.offline.modules.path";

	public GenerateDBPropertiesActionListener(AbstractController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminController controller = (SchemaAdminController) getController();
		List<DatabaseInstance> dbList = SessionData.getInstance().getDatabaseInstances();
		String text = "";
		for (int i = 0; i < dbList.size(); i++)
			text += getInstancePropertiesAsText(i + 1, dbList.get(i)) + "\n";

		DatabaseSettingsService service = ACSpringFactory.getInstance().getDatabaseSettingsService();
		Map<String, String> commonProps = service.getCommonProperties();
		for (String key : commonProps.keySet())
			text += key + "=" + commonProps.get(key) + "\n";

		controller.showDBPropertiesView(escapeBSlashes(text));
	}

	private String escapeBSlashes(String src){
		return src.replaceAll("\\\\", "\\\\\\\\");
	}

	private String getInstancePropertiesAsText(int index, DatabaseInstance di){
		String text = "";
		String prop = parseProperty(DBID_PROPERTY, new String[] { String.valueOf(index) });
		text = prop + "=" + di.getDatabaseInstanceId() + "\n";
		List<String> datasources = di.getDatasourceNames();
		for (int i = 0; i < datasources.size(); i++){
			prop = parseProperty(DATASOURCE_PROPERTY, new String[] { String.valueOf(index), String.valueOf(i + 1) });
			text += prop + "=" + datasources.get(i) + "\n";
		}
		if (di.getNavigatorHost() != null){
			prop = parseProperty(NAVIGATOR_HOST_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getNavigatorHost() + "\n";
		}
		if (di.getRasterServerUrl() != null){
			prop = parseProperty(RASTER_SERVER_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getRasterServerUrl() + "\n";
		}
		if (di.getMapPath() != null){
			prop = parseProperty(MAPPATH_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getMapPath() + "\n";
		}
		if (di.getDocrootPath() != null){
			prop = parseProperty(DOCROOT_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getDocrootPath() + "\n";
		}
		if (di.getDeltaThreshold() != null){
			prop = parseProperty(DELTA_THRESHOLD_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getDeltaThreshold() + "\n";
		}
		if (di.getDeltaOutThreshold() != null){
			prop = parseProperty(DELTA_OUT_THRESHOLD_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getDeltaOutThreshold() + "\n";
		}
		if(di.getModulePath() != null){
			prop = parseProperty(MODULE_PATH_PROPERTY, new String[] { String.valueOf(index) });
			text += prop + "=" + di.getModulePath() + "\n";			
		}

		return text;
	}

	private String parseProperty(String property, String[] values){
		for (int i = 0; i < values.length; i++){
			property = property.replaceAll("%" + (i + 1) + "%", values[i]);
		}
		return property;
	}

}
