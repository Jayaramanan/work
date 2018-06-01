/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.constants;

public enum ServletName{
	SearchServlet("/servlet/SearchServlet"),

	DynamicAttributesServlet("/servlet/DynamicAttributesServlet"),

	//TODO change to protobuffers
	ExportProvider("/servlet/ExportProvider"),

	FavoritesFolderManagementServlet("/servlet/FavoritesFolderManagementServlet"),

	FavoritesManagementServlet("/servlet/FavoritesManagementServlet"),

	//TODO ???
	GraphProvider("/servlet/GraphProvider"),

	IconProvider("/servlet/IconProvider"),

	//TODO change to protobuffers
	LicenseProvider("/servlet/LicenseProvider"),

	LoginServlet("/servlet/LoginServlet"),

	ReportProvider("/servlet/ReportProvider"),
	SchemaServlet("/servlet/SchemaServlet"),

	//TODO change to protobuffers, merge into SettingsServlet
	SettingsProvider("/servlet/SettingsProvider"),
	
	SrcIdToIdConvertionServlet("/servlet/srcidtoid"),
	IdToSrcIdConvertionServlet("/servlet/idtosrcid"),
	
	SynchronizationServlet("/servlet/SynchronizationServlet"), 
	ActivityStreamServlet("/servlet/ActivityStreamServlet"), 
	ObjectManagementServlet("/servlet/ObjectManagementServlet"), 
	PaletteServlet("/servlet/PaletteServlet"), 

	SettingsServlet("/servlet/SettingsServlet"), 
	LanguageServlet("/servlet/LanguageServlet"),
	ChartsServlet("/servlet/ChartsServlet"),
	GraphServlet("/servlet/GraphServlet"),
	GISServlet("/servlet/GISServlet"), 
	GeoAnalyticsServlet("/servlet/GeoAnalyticsServlet");

	private String url;
	
	private ServletName(String url){
		this.url = url;
	}
	
	public String getUrl(){
		return url;
	}
}
