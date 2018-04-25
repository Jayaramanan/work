/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SettingsUtil{
	private SettingsUtil(){
	}

	private static final Map<String, TextID> settingsNameLabelMap = new HashMap<String, TextID>();

	static{
		settingsNameLabelMap.put("File", TextID.File);
		settingsNameLabelMap.put("Node", TextID.Node);
		settingsNameLabelMap.put("Connection", TextID.Connection);
		settingsNameLabelMap.put("Maps", TextID.Maps);
		settingsNameLabelMap.put("Charts", TextID.Charts);
		settingsNameLabelMap.put("Help", TextID.Help);
		settingsNameLabelMap.put("File_ChangePassword_InUse", TextID.FileChangePassword);
		settingsNameLabelMap.put("File_CopyData_InUse", TextID.FileCopyData);
		settingsNameLabelMap.put("File_CopyGraph_InUse", TextID.FileCopyGraph);
		settingsNameLabelMap.put("File_CopyMap_InUse", TextID.FileCopyMap);
		settingsNameLabelMap.put("File_Exit_InUse", TextID.FileExit);
		settingsNameLabelMap.put("File_ExportData_InUse", TextID.FileExportData);
		settingsNameLabelMap.put("File_ExportAsCSV_InUse", TextID.FileExportAsCSV);
		settingsNameLabelMap.put("File_InUse", TextID.File);
		settingsNameLabelMap.put("Node_InUse", TextID.Node);
		settingsNameLabelMap.put("Node_NodeCreate_InUse", TextID.NodeCreate);
		settingsNameLabelMap.put("Node_NodeDelete_InUse", TextID.NodeDelete);
		settingsNameLabelMap.put("Node_NodeEdit_InUse", TextID.NodeEdit);
		settingsNameLabelMap.put("Node_NodeSecurity_InUse", TextID.NodeSecurity);
		settingsNameLabelMap.put("Connection_ConnectionCreate_InUse", TextID.ConnectionCreate);
		settingsNameLabelMap.put("Connection_ConnectionEdit_InUse", TextID.ConnectionEdit);
		settingsNameLabelMap.put("Connection_InUse", TextID.Connection);
		settingsNameLabelMap.put("Maps_InUse", TextID.Maps);
		settingsNameLabelMap.put("Charts_InUse", TextID.Charts);
		settingsNameLabelMap.put("Help_About_InUse", TextID.HelpAbout);
		settingsNameLabelMap.put("Help_Documentation_InUse", TextID.Documentation);
		settingsNameLabelMap.put("Help_InUse", TextID.Help);

	}

	public static TextID getLabelIdByName(String name){
		return settingsNameLabelMap.containsKey(name) ? settingsNameLabelMap.get(name) : null;
	}

	public static boolean isTrueValue(String value){
		return (("1".equals(value)) || ("yes".equalsIgnoreCase(value)) || ("y".equalsIgnoreCase(value))
		        || ("true".equalsIgnoreCase(value)) || ("t".equalsIgnoreCase(value)));
	}

	public static boolean isBooleanSetting(Setting setting){
		String prop = setting.getProp();
		return (prop != null
		        && (prop.startsWith(Setting.SHOW_PREFIX) || prop.endsWith(Setting.IN_USE_SUFFIX) || prop
		                .endsWith(Setting.VISIBLE_SUFFIX)) || Setting.BOOLEAN_SETTINGS.contains(prop));
	}

	public static boolean isColorSetting(Setting setting){
		String prop = setting.getProp();
		return prop != null
		        && (prop.equals(Setting.NODE_SELECTED_COLOR_PROPERTY) || prop.equals(Setting.PREFILTER_BACKGROUND_PROPERTY)
		                || prop.equals(Setting.GRADIENT_START_COLOR_PROPERTY) || prop
		                .equals(Setting.GRADIENT_END_COLOR_PROPERTY));
	}
}
