/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.gui;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ControllerContainer;
import com.ni3.ag.adminconsole.client.controller.TabChangeListener;
import com.ni3.ag.adminconsole.client.controller.appconf.attributes.AttributeEditController;
import com.ni3.ag.adminconsole.client.controller.appconf.format.FormatAttributesController;
import com.ni3.ag.adminconsole.client.controller.appconf.predefattributes.PredefinedAttributeEditController;
import com.ni3.ag.adminconsole.client.controller.appconf.settings.SettingsController;
import com.ni3.ag.adminconsole.client.controller.charts.ChartController;
import com.ni3.ag.adminconsole.client.controller.connection.ObjectConnectionController;
import com.ni3.ag.adminconsole.client.controller.diag.DiagnosticsController;
import com.ni3.ag.adminconsole.client.controller.etl.ETLController;
import com.ni3.ag.adminconsole.client.controller.geoanalytics.GeoAnalyticsController;
import com.ni3.ag.adminconsole.client.controller.languageadmin.LanguageController;
import com.ni3.ag.adminconsole.client.controller.licenses.LicenseAdminController;
import com.ni3.ag.adminconsole.client.controller.licenses.ac.AdminConsoleLicenseController;
import com.ni3.ag.adminconsole.client.controller.licenses.navigator.NavigatorLicenseController;
import com.ni3.ag.adminconsole.client.controller.metaphoradmin.NodeMetaphorController;
import com.ni3.ag.adminconsole.client.controller.reports.ReportsController;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.controller.thickclient.ThickClientController;
import com.ni3.ag.adminconsole.client.controller.thickclient.vers.VersioningController;
import com.ni3.ag.adminconsole.client.controller.useractivity.UserActivityController;
import com.ni3.ag.adminconsole.client.controller.useradmin.UserAdminController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.ApplicationConfigurationTabbedPane;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.common.ACTabbedPane;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseSettingsService;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;

public class MainPanel2 extends JPanel{
	private static final long serialVersionUID = 1056223767330166027L;
	private static final Logger log = Logger.getLogger(MainPanel2.class);

	private static ACTabbedPane mainTabbedPane;
	private static URL WARNING_ICON_URL = MainPanel2.class.getResource("/images/Warning.png");
	private static boolean defaultUserLanguageInited = false;

	public MainPanel2(){
		super();
		setFocusCycleRoot(true);

		initDatabaseInstances();
		initDefaultLanguage();

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		mainTabbedPane = new ACTabbedPane();
		mainTabbedPane.setTabPlacement(SwingConstants.TOP);
		mainTabbedPane.setAutoscrolls(true);

		springLayout.putConstraint(SpringLayout.EAST, mainTabbedPane, 0, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.WEST, mainTabbedPane, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, mainTabbedPane, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, mainTabbedPane, 0, SpringLayout.NORTH, this);
		add(mainTabbedPane);

		ACSpringFactory instance = ACSpringFactory.getInstance();
		SchemaAdminController schemaAdminController = (SchemaAdminController) instance.getBean("schemaAdminController");
		NodeMetaphorController metaphorController = (NodeMetaphorController) instance.getBean("nodeMetaphorController");
		AttributeEditController attrEditController = (AttributeEditController) instance.getBean("attributeEditController");
		PredefinedAttributeEditController predefAttrController = (PredefinedAttributeEditController) instance
		        .getBean("predefinedAttributeEditController");
		FormatAttributesController formatAttrController = (FormatAttributesController) instance
		        .getBean("formatAttributesController");
		ObjectConnectionController objConnController = (ObjectConnectionController) instance
		        .getBean("objectConnectionController");
		UserAdminController userAdminController = (UserAdminController) instance.getBean("userAdminController");

		SettingsController settingsController = (SettingsController) instance.getBean("settingsController");
		LanguageController userLanguageController = (LanguageController) instance.getBean("languageController");
		ChartController chartController = (ChartController) instance.getBean("chartController");
		LicenseAdminController licenseController = (LicenseAdminController) instance.getBean("licenseAdminController");
		NavigatorLicenseController naviLicenseController = (NavigatorLicenseController) instance
		        .getBean("navigatorLicenseController");
		DiagnosticsController diagnosticsController = (DiagnosticsController) instance.getBean("diagnosticsController");
		ThickClientController thickClientController = (ThickClientController) instance.getBean("thickClientController");
		// MapJobController mapJobController = (MapJobController) instance.getBean("mapJobController");
		VersioningController versioningController = (VersioningController) instance.getBean("versioningController");
		GeoAnalyticsController geoAnalyticsController = (GeoAnalyticsController) instance.getBean("geoAnalyticsController");
		ReportsController reportsController = (ReportsController) instance.getBean("reportsController");
		ETLController etlController = (ETLController) instance.getBean("etlController");
		UserActivityController userActivityController = (UserActivityController) instance.getBean("userActivityController");
		AdminConsoleLicenseController acLicenseController = (AdminConsoleLicenseController) instance
		        .getBean("adminConsoleLicenseController");

		ApplicationConfigurationTabbedPane appConfigTabbedPane = new ApplicationConfigurationTabbedPane();
		appConfigTabbedPane.setAttributeEditView(attrEditController.getView());
		appConfigTabbedPane.setPredefinedAttributeEditView(predefAttrController.getView());
		appConfigTabbedPane.setFormatAttributesView(formatAttrController.getView());
		appConfigTabbedPane.setObjectConnectionView(objConnController.getView());

		ACTabbedPane thickClientTabbedPane = new ACTabbedPane();
		thickClientTabbedPane.addTab(get(TextID.Versions), null, versioningController.getView());
		thickClientTabbedPane.addTab(get(TextID.DataExtraction), null, thickClientController.getView());
		// thickClientTabbedPane.addTab(get(TextID.MapExtraction), null, mapJobController.getView());

		ACTabbedPane licenseTabbedPane = new ACTabbedPane();
		licenseTabbedPane.addTab(get(TextID.Licenses), null, licenseController.getView());
		licenseTabbedPane.addTab(get(TextID.AdminConsole), null, acLicenseController.getView());
		licenseTabbedPane.addTab(get(TextID.Navigator), null, naviLicenseController.getView());

		mainTabbedPane.addTab(get(TextID.Schemas), null, schemaAdminController.getView());
		mainTabbedPane.addTab(get(TextID.Attributes), null, appConfigTabbedPane);
		mainTabbedPane.addTab(get(TextID.Metaphors), null, metaphorController.getView());
		mainTabbedPane.addTab(get(TextID.Charts), null, chartController.getView());
		mainTabbedPane.addTab(get(TextID.Users), null, userAdminController.getView());
		mainTabbedPane.addTab(get(TextID.Settings), settingsController.getView());
		mainTabbedPane.addTab(get(TextID.Languages), null, userLanguageController.getView());

		mainTabbedPane.addTab(get(TextID.GeoAnalytics), geoAnalyticsController.getView());
		mainTabbedPane.addTab(get(TextID.ETL), etlController.getView());
		mainTabbedPane.addTab(get(TextID.Licenses), null, licenseTabbedPane);
		mainTabbedPane.addTab(get(TextID.Monitoring), null, userActivityController.getView());
		mainTabbedPane.addTab(get(TextID.Diagnostics), null, diagnosticsController.getView());
		mainTabbedPane.addTab(get(TextID.OfflineClients), thickClientTabbedPane);
		mainTabbedPane.addTab(get(TextID.Reports), reportsController.getView());

		ControllerContainer controllerContainer = ControllerContainer.getInstance();
		controllerContainer.addController(schemaAdminController);
		controllerContainer.addController(metaphorController);
		controllerContainer.addController(attrEditController);
		controllerContainer.addController(predefAttrController);
		controllerContainer.addController(formatAttrController);
		controllerContainer.addController(objConnController);
		controllerContainer.addController(userAdminController);
		controllerContainer.addController(userLanguageController);
		controllerContainer.addController(settingsController);
		controllerContainer.addController(chartController);
		controllerContainer.addController(licenseController);
		controllerContainer.addController(naviLicenseController);
		controllerContainer.addController(diagnosticsController);
		controllerContainer.addController(thickClientController);
		// controllerContainer.addController(mapJobController);
		controllerContainer.addController(versioningController);
		controllerContainer.addController(geoAnalyticsController);
		controllerContainer.addController(reportsController);
		controllerContainer.addController(etlController);
		controllerContainer.addController(userActivityController);
		controllerContainer.addController(acLicenseController);

		TabChangeListener tabListener = new TabChangeListener();
		mainTabbedPane.addChangeListener(tabListener);
		appConfigTabbedPane.addChangeListener(tabListener);
		thickClientTabbedPane.addChangeListener(tabListener);
		licenseTabbedPane.addChangeListener(tabListener);

		mainTabbedPane.setSelectedIndex(0);
		schemaAdminController.initializeController();
		isDisabledApp(null);

		this.setVisible(true);
	}

	private static void renameTabs(){
		ApplicationConfigurationTabbedPane appConfigTabbedPane = (ApplicationConfigurationTabbedPane) mainTabbedPane
		        .getComponentAt(1);
		appConfigTabbedPane.setTitleAt(0, get(TextID.Attributes));
		appConfigTabbedPane.setTitleAt(1, get(TextID.AttributeValues));
		appConfigTabbedPane.setTitleAt(2, get(TextID.Format));
		appConfigTabbedPane.setTitleAt(3, get(TextID.Connections));

		ACTabbedPane thickClientTabbedPane = (ACTabbedPane) mainTabbedPane.getComponentAt(12);
		thickClientTabbedPane.setTitleAt(0, get(TextID.Versions));
		thickClientTabbedPane.setTitleAt(1, get(TextID.DataExtraction));
		// thickClientTabbedPane.setTitleAt(2, get(TextID.MapExtraction));

		ACTabbedPane licenseTabbedPane = (ACTabbedPane) mainTabbedPane.getComponentAt(9);
		licenseTabbedPane.setTitleAt(0, get(TextID.Licenses));
		licenseTabbedPane.setTitleAt(1, get(TextID.AdminConsole));
		licenseTabbedPane.setTitleAt(2, get(TextID.Navigator));

		mainTabbedPane.setTitleAt(0, get(TextID.Schemas));
		mainTabbedPane.setTitleAt(1, get(TextID.Attributes));
		mainTabbedPane.setTitleAt(2, get(TextID.Metaphors));
		mainTabbedPane.setTitleAt(3, get(TextID.Charts));
		mainTabbedPane.setTitleAt(4, get(TextID.Users));
		mainTabbedPane.setTitleAt(5, get(TextID.Settings));
		mainTabbedPane.setTitleAt(6, get(TextID.Languages));

		mainTabbedPane.setTitleAt(7, get(TextID.GeoAnalytics));
		mainTabbedPane.setTitleAt(8, get(TextID.ETL));
		mainTabbedPane.setTitleAt(9, get(TextID.Licenses));
		mainTabbedPane.setTitleAt(10, get(TextID.Monitoring));
		mainTabbedPane.setTitleAt(11, get(TextID.Diagnostics));
		mainTabbedPane.setTitleAt(12, get(TextID.OfflineClients));
		mainTabbedPane.setTitleAt(13, get(TextID.Reports));
	}

	public static void setInvalidationNeeded(TextID id, boolean b){
		for (int i = 0; i < mainTabbedPane.getTabCount(); i++){
			String title = mainTabbedPane.getTitleAt(i);
			if (title.equals(get(id))){
				if (b)
					mainTabbedPane.setIconAt(i, new ImageIcon(WARNING_ICON_URL));
				else
					mainTabbedPane.setIconAt(i, null);
				break;
			}
		}
	}

	public static void setAllInvalidationNeeded(DataGroup... dataGroups){
		Set<DataGroup> toSet = new HashSet<DataGroup>();
		Collections.addAll(toSet, dataGroups);
		for (DataGroup dg : DataGroup.values()){
			setInvalidationNeeded(dg.getId(), toSet.contains(dg));
		}
	}

	public static void resetInvalidationNeeded(){
		for (int i = 0; i < mainTabbedPane.getTabCount(); i++){
			mainTabbedPane.setIconAt(i, null);
		}
	}

	public static boolean adjustTabVisibilities(){
		ObjectVisibilityStore visStore = ObjectVisibilityStore.getInstance();
		boolean isCurrentVisible = true;
		for (int i = 0; i < mainTabbedPane.getTabCount(); i++){
			String title = mainTabbedPane.getTitleAt(i);
			boolean isVisible = true;
			if (title.equals(get(TextID.Charts))){
				isVisible = visStore.isChartsScreenVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.OfflineClients))){
				isVisible = visStore.isThickClientScreenVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Attributes))){
				isVisible = visStore.isSchemaVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Metaphors))){
				isVisible = visStore.isMetaphorVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Users))){
				isVisible = visStore.isUserVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Languages))){
				isVisible = visStore.isLanguageVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.GeoAnalytics))){
				isVisible = visStore.isGeoAnalyticsVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Diagnostics))){
				isVisible = visStore.isDiagnosticsVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Reports))){
				isVisible = visStore.isReportsVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Settings))){
				isVisible = visStore.isSchemaVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.ETL))){
				User currUser = SessionData.getInstance().getUser();
				isVisible = visStore.isETLVisible() && currUser != null;
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else if (title.equals(get(TextID.Monitoring))){
				isVisible = visStore.isMonitoringScreenVisible();
				mainTabbedPane.setEnabledAt(i, isVisible);
			} else
				mainTabbedPane.setEnabledAt(i, true);

			if (title.equals(get(TextID.Licenses))){
				List<LicenseData> expiringLicenses = visStore.getExpiringLicenses();
				if (expiringLicenses != null && !expiringLicenses.isEmpty()){
					mainTabbedPane.setIconAt(i, new ImageIcon(WARNING_ICON_URL));
				} else{
					mainTabbedPane.setIconAt(i, null);
				}
			}

			if (!isVisible && mainTabbedPane.getSelectedIndex() == i)
				isCurrentVisible = false;
		}
		return isCurrentVisible;
	}

	private static boolean isSchemaAdminSelected(){
		int selectedIndex = mainTabbedPane.getSelectedIndex();
		return mainTabbedPane.getTitleAt(selectedIndex).equals(get(TextID.Schemas));
	}

	public static boolean isDisabledApp(DatabaseInstance currentDb){
		SchemaAdminService service = ACSpringFactory.getInstance().getSchemaAdminService();

		if (currentDb == null || !currentDb.isInited())
			setAllInvalidationNeeded(new DataGroup[] {});
		else{
			Set<DataGroup> dGroups = service.getInvalidationRequiredGroups();
			if (dGroups == null)
				dGroups = new HashSet<DataGroup>();
			setAllInvalidationNeeded(dGroups.toArray(new DataGroup[] {}));
		}

		if (!isSchemaAdminSelected() || (currentDb != null && currentDb.isConnected()))
			return false;

		ObjectVisibilityStore visStore = ObjectVisibilityStore.getInstance();

		for (int i = 0; i < mainTabbedPane.getTabCount(); i++){
			String title = mainTabbedPane.getTitleAt(i);
			if (!title.equals(get(TextID.Schemas)))
				mainTabbedPane.setEnabledAt(i, false);
			if (title.equals(get(TextID.Licenses))){
				List<LicenseData> expiringLicenses = visStore.getExpiringLicenses();
				boolean warn = expiringLicenses != null && !expiringLicenses.isEmpty();
				mainTabbedPane.setIconAt(i, warn ? new ImageIcon(WARNING_ICON_URL) : null);
			}
		}
		return true;
	}

	public static void forwardToSchemaAdmin(boolean disconnect){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		SchemaAdminView view = controller.getView();
		view.getRightPanel().setPanelToSchemaInfo(true);
		DatabaseInstance di = SessionData.getInstance().getCurrentDatabaseInstance();
		ObjectHolder.getInstance().setCurrentPath(new Object[] { new ACRootNode(), di });
		if (disconnect){
			controller.disconnect(di);
			controller.reloadData();
		}
		mainTabbedPane.setSelectedIndex(0);
	}

	public static boolean isDefaultUserLanguageInited(){
		return defaultUserLanguageInited;
	}

	private void initDefaultLanguage(){
		SessionData instance = SessionData.getInstance();
		LanguageAdminService langService = ACSpringFactory.getInstance().getLanguageAdminService();
		List<Language> langs = langService.getLanguages();
		for (Language lang : langs){
			if (lang.getId().equals(Language.DEFAULT_ID)){
				instance.setUserLanguage(lang);
				break;
			}
		}
		if (instance.getUserLanguage() == null){
			try{
				instance.setUserLanguage(langs.get(0));
			} catch (IndexOutOfBoundsException e){
				log.warn("Could not set application language based on user, setting default (1).");
				Language defaultLanguage = new Language();
				defaultLanguage.setId(Language.DEFAULT_ID);
				instance.setUserLanguage(defaultLanguage);
			}
		}
	}

	public static void initDefaultUserLanguage(){
		SessionData instance = SessionData.getInstance();
		LanguageAdminService langServ = ACSpringFactory.getInstance().getLanguageAdminService();
		User currentUser = instance.getUser();
		try{
			Language userLanguage = langServ.getLanguage(currentUser);
			instance.setUserLanguage(userLanguage);
			ACSpringFactory.getInstance().getUserLanguageService().refresh();
		} catch (ACException e){
			log.error("Could not set application language based on user, leaving default.", e);
		}
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		renameTabs();
		controller.getView().resetLabels();
		defaultUserLanguageInited = true;
	}

	private void initDatabaseInstances(){
		DatabaseSettingsService dbService = ACSpringFactory.getInstance().getDatabaseSettingsService();
		List<DatabaseInstance> dbInstances = dbService.getDatabaseInstanceNames();
		SessionData instance = SessionData.getInstance();
		instance.setDatabaseInstances(dbInstances);
		if (dbInstances != null && !dbInstances.isEmpty()){
			instance.setCurrentDatabaseInstance(dbInstances.get(0));
			instance.setDbName(dbInstances.get(0).getDatabaseInstanceId());
		}
	}

}
