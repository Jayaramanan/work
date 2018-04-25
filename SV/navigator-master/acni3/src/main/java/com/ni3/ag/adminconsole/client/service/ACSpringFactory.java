/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.service;

import com.ni3.ag.adminconsole.shared.service.def.*;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

public class ACSpringFactory{
	private static ACSpringFactory instance = null;
	private ClassPathXmlApplicationContext context = null;
	private static final String[] CONFIG_FILE_NAME = {"services-context.xml", "ui-context.xml"};
	private static final Logger log = Logger.getLogger(ACSpringFactory.class);

	public static void init(Properties externalProps){
		try{
			instance = new ACSpringFactory();

			//injecting external properties from the jnlp file
			PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
			propertyPlaceholderConfigurer.setProperties(externalProps);


			ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
			classPathXmlApplicationContext.addBeanFactoryPostProcessor(propertyPlaceholderConfigurer);
			classPathXmlApplicationContext.setConfigLocations(CONFIG_FILE_NAME);
			classPathXmlApplicationContext.refresh();

			instance.context = classPathXmlApplicationContext;
		} catch (Exception e){
			log.error("Error while initializing ACSpringFactory", e);
		}
	}

	private ACSpringFactory(){
	}

	public static ACSpringFactory getInstance(){
		return instance;
	}

	public Object getBean(String beanName){
		Object bean = context.getBean(beanName);
		return bean;
	}

	public DeploymentVersionService getDeploymentVersionService(){
		return (DeploymentVersionService) context.getBean("deploymentVersionService");
	}

	public LoginService getLoginService(){
		return (LoginService) context.getBean("loginService");
	}

	public SchemaAdminService getSchemaAdminService(){
		return (SchemaAdminService) context.getBean("schemaAdminService");
	}

	public NodeMetaphorService getNodeMetaphorService(){
		return (NodeMetaphorService) context.getBean("nodeMetaphorService");
	}

	public DatabaseSettingsService getDatabaseSettingsService(){
		return (DatabaseSettingsService) context.getBean("databaseSettingsService");
	}

	public ObjectsConnectionsService getObjectsConnectionService(){
		return (ObjectsConnectionsService) context.getBean("objectsConnectionsService");
	}

	public UserAdminService getUserAdminService(){
		return (UserAdminService) context.getBean("userAdminService");
	}

	public PredefinedAttributeService getPredefinedAttributeService(){
		return (PredefinedAttributeService) context.getBean("predefinedAttributeService");
	}

	public SettingsService getSettingsService(){
		return (SettingsService) context.getBean("settingsService");
	}

	public LanguageAdminService getLanguageAdminService(){
		return (LanguageAdminService) context.getBean("languageAdminService");
	}

	public UserLanguageService getUserLanguageService(){
		return (UserLanguageService) context.getBean("userLanguageService");
	}

	public DatabaseVersionService getDatabaseVersionService(){
		return (DatabaseVersionService) context.getBean("databaseVersionService");
	}

	public AttributeEditService getAttributeEditService(){
		return (AttributeEditService) context.getBean("attributeEditService");
	}

	public LicenseService getLicenseService(){
		return (LicenseService) context.getBean("licenseService");
	}

	public DiagnosticsService getDiagnosticsService(){
		return (DiagnosticsService) context.getBean("diagnosticsService");
	}

	public ThickClientJobService getThickClientService(){
		return (ThickClientJobService) context.getBean("thickClientJobService");
	}

	public MapJobService getMapJobService(){
		return (MapJobService) context.getBean("mapJobService");
	}

	public UserDataExportService getUserDataExportService(){
		return (UserDataExportService) context.getBean("userDataExportService");
	}

	public AddDatasourceService getAddDatasourceService(){
		return (AddDatasourceService) context.getBean("addDatasourceService");
	}

	public NavigatorLicenseService getNavigatorLicenseService(){
		return (NavigatorLicenseService) context.getBean("navigatorLicenseService");
	}

	public VersioningService getVersioningService(){
		return (VersioningService) context.getBean("versioningService");
	}

	public GeoAnalyticsService getGeoAnalyticsService(){
		return (GeoAnalyticsService) context.getBean("geoAnalyticsService");
	}

	public ACVisibilityService getACVisibilityService(){
		return (ACVisibilityService) context.getBean("acVisibilityService");
	}

	public ReportsService getReportsService(){
		return (ReportsService) context.getBean("reportsService");
	}

	public UserActivityService getUserActivityService(){
		return (UserActivityService) context.getBean("userActivityService");
	}

	public AdminConsoleLicenseService getACLicenseService(){
		return (AdminConsoleLicenseService) context.getBean("adminConsoleLicenseService");
	}

	public ACValidationRule getAttributeUsedInMetaphorRule(){
		return (ACValidationRule) getBean("attributeInMetaphorUserRule");
	}
}
