/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.SavePromptDialog;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsTableModel;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsTreeModel;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.client.view.appconf.TabSwitchAction;
import com.ni3.ag.adminconsole.client.view.appconf.UserSettingsTreeModel;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;

public class SettingsController extends AbstractController{
	private static final Logger log = Logger.getLogger(SettingsController.class);
	private SettingsModel model;
	private SettingsView view;

	private TabSwitchAction[] tabSwitchActions;
	private final static int ALWAYS_ASK_ACTION_INDEX = 0;
	private final static int SAVE_ACTION_INDEX = 1;
	private final static int DISCARD_ACTION_INDEX = 2;

	private UpdateButtonListener updateListener;
	private SchemaComboListener schemaComboListener;
	private LanguageComboListener languageComboListener;
	private TabSwitchActionComboListener tabSwitchComboListener;
	private InheritGroupSettingsCheckBoxListener inheritCheckBoxListener;
	private AccessComboListener accessComboListener;
	private HideGISPanelListener hideGISPanelListener;

	@Override
	public SettingsModel getModel(){
		return model;
	}

	@Override
	public SettingsView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (SettingsModel) m;
	}

	@Override
	public void setView(Component c){
		view = (SettingsView) c;
	}

	public void setModel(SettingsModel m){
		model = m;
	}

	public void setView(SettingsView v){
		view = v;
	}

	@Override
	public void initializeController(){
		tabSwitchActions = new TabSwitchAction[] {
		        new TabSwitchAction(Translation.get(TextID.AlwaysAsk), SavePromptDialog.ALWAYS_ASK),
		        new TabSwitchAction(Translation.get(TextID.Save), SavePromptDialog.SAVE_ACTION),
		        new TabSwitchAction(Translation.get(TextID.Discard), SavePromptDialog.DISCARD_ACTION) };
		loadDataToModel();
		super.initializeController();
		ACTree tree = (ACTree) view.getUserTree();
		tree.setCurrentController(this);
		view.setTabSwitchActions(tabSwitchActions);
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		this.view.getUserTree().addTreeSelectionListener(new UserTreeSelectionListener(this));
		this.view.getAddButton().addActionListener(new AddButtonActionListener(this));
		this.view.getDeleteButton().addActionListener(new DeleteButtonListener(this));
		updateListener = new UpdateButtonListener(this);
		this.view.getUpdateButton().addActionListener(updateListener);
		this.view.getRefreshButton().addActionListener(new RefreshButtonListener(this));
		schemaComboListener = new SchemaComboListener(this);
		this.view.addSchemaComboListener(schemaComboListener);
		languageComboListener = new LanguageComboListener(this);
		this.view.addLanguageComboListener(languageComboListener);
		inheritCheckBoxListener = new InheritGroupSettingsCheckBoxListener(this);
		this.view.setInheritPropertiesListener(inheritCheckBoxListener);
		tabSwitchComboListener = new TabSwitchActionComboListener(this);
		this.view.setTabSwitchActionComboListener(tabSwitchComboListener);
		accessComboListener = new AccessComboListener(this);
		this.view.addAccessComboListener(accessComboListener);
		hideGISPanelListener = new HideGISPanelListener(this);
		this.view.addHideGISPanelListener(hideGISPanelListener);
        this.view.addRefreshImageCacheButtonListener(new ImageCacheRefreshButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
		populateDocUrlPropertyToModel();
		populatePassFormatPropertyToModel();
	}

	private void populateDocUrlPropertyToModel(){
		String docUrl = view.getDocumentationUrlText();
		List<?> settings = model.getCurrentSettings();
		boolean containsInheritance = areInheritantSettings(settings);
		if ((model.isCurrentGroup() || model.isCurrentUser()) && containsInheritance){
			return;
		}

		Setting urlSetting = null;
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp().equals(Setting.HELP_DOCUMENT_URL_PROPERTY)){
				urlSetting = s;
				break;
			}
		}
		if (urlSetting == null && docUrl != null && docUrl.length() > 0){
			urlSetting = createSetting(Setting.APPLET_SECTION, Setting.HELP_DOCUMENT_URL_PROPERTY, null);
		}
		if (docUrl != null && urlSetting != null){
			urlSetting.setValue(docUrl);
		}
	}

	private void populatePassFormatPropertyToModel(){
		String passFormat = view.getPasswordFormatText();
		List<?> settings = model.getCurrentSettings();
		if (model.isCurrentGroup() || model.isCurrentUser()){
			return;
		}

		Setting urlSetting = null;
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp().equals(Setting.PASSWORD_COMPLEXITY_SETTING)){
				urlSetting = s;
				break;
			}
		}
		if (urlSetting == null && passFormat != null && passFormat.length() > 0){
			urlSetting = createSetting(Setting.APPLET_SECTION, Setting.PASSWORD_COMPLEXITY_SETTING, null);
		}
		if (passFormat != null && urlSetting != null){
			urlSetting.setValue(passFormat);
		}
	}

	private Setting createSetting(String section, String property, String value){
		Setting setting = null;
		if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			setting = new UserSetting(user, section, property, value);
			user.getSettings().add((UserSetting) setting);
		} else if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			setting = new GroupSetting(group, section, property, value);
			group.getGroupSettings().add((GroupSetting) setting);
		} else if (model.isCurrentApplication()){
			setting = new ApplicationSetting(section, property, value);
			model.getApplicationSettings().add((ApplicationSetting) setting);
		}
		return setting;
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		UserSettingsTreeModel ustrModel = new UserSettingsTreeModel(getModel().getGroupMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		getView().getUserTree().setModel(ustrModel);
	}

	public void updateUserSettingTree(){
		if (getModel().isCurrentUser()){
			User u = (User) getModel().getCurrentObject();
			List<UserSetting> userSettings = u.getSettings();
			boolean containsInheritance = areInheritantSettings(userSettings);
			getView().setInheritPropertiesSelected(containsInheritance);
			appendSettingsTree(userSettings, containsInheritance);
		}
	}

	@Override
	public void reloadData(){
		loadDataToModel();
		populateDataToView(model, view);
	}

	private void loadDataToModel(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			SettingsService service = ACSpringFactory.getInstance().getSettingsService();
			model.clearDeletableApplicationSettings();
			model.setGroups(service.getGroups());
			List<ApplicationSetting> appSettings = service.getApplicationSettings();
			model.setApplicationSettings(appSettings);
			model.setLanguages(service.getLanguages());
			model.setSchemas(service.getSchemas());
		}
	}

	private void reloadApplicationSettings(){
		SettingsService service = ACSpringFactory.getInstance().getSettingsService();
		model.setApplicationSettings(service.getApplicationSettings());
	}

	private void reloadGroupSettings(){
		Group currentGroup = (Group) model.getCurrentObject();
		if (currentGroup == null){
			return;
		}
		SettingsService service = ACSpringFactory.getInstance().getSettingsService();
		Group newGroup = service.reloadGroup(currentGroup.getId());
		currentGroup.setGroupSettings(newGroup.getGroupSettings());
		for (GroupSetting us : currentGroup.getGroupSettings()){
			us.setGroup(currentGroup);
		}
	}

	private void reloadUserSettings(){
		User currentUser = (User) model.getCurrentObject();
		if (currentUser == null){
			return;
		}
		SettingsService service = ACSpringFactory.getInstance().getSettingsService();
		User newUser = service.reloadUser(currentUser.getId());
		currentUser.setSettings(newUser.getSettings());
		for (UserSetting us : currentUser.getSettings()){
			us.setUser(currentUser);
		}
	}

	public void updateData(){
		setListenersEnabled(false);

		if (!model.isCurrent()){
			setEmptyData();
		} else if (model.isCurrentApplication()){
			DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
			if (!db.isConnected()){
				setEmptyData();
				return;
			}
		}

		List<?> settings = model.getCurrentSettings();
		SettingsTableModel tm = new SettingsTableModel(settings);
		view.setTableModel(tm);

		setComboReferenceData();
		updatePanel();

		setListenersEnabled(true);
	}

	private void updatePanel(){
		if (model.isCurrentUser())
			view.setInheritLabel(TextID.InheritGroupLevelProperties);
		else
			view.setInheritLabel(TextID.InheritApplicationLevelProperties);
		List<?> settings = model.getCurrentSettings();
		boolean containsInheritance = areInheritantSettings(settings);
		boolean inheritEnabled = (model.isCurrentGroup() || model.isCurrentUser());
		view.setInheritPropertiesEnabled(inheritEnabled);
		view.setInheritPropertiesSelected(containsInheritance);
		view.setTopPanelEnabled(model.isCurrent() && !containsInheritance);
		view.setPasswordFormatEnabled(model.isCurrentApplication());

		if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			group.getGroupSettings();
		}

		appendSettingsTree(settings, containsInheritance);
	}

	private void setComboReferenceData(){
		view.setLanguageReferenceData(model.getLanguages());
		view.setSchemaReferenceData(model.getSchemas());
		populateDataToCombo();
	}

	public void setListenersEnabled(boolean enabled){
		schemaComboListener.setEnabled(enabled);
		languageComboListener.setEnabled(enabled);
		tabSwitchComboListener.setEnabled(enabled);
		inheritCheckBoxListener.setEnabled(enabled);
		accessComboListener.setEnabled(enabled);
	}

	private void setEmptyData(){
		SettingsTableModel tm = new SettingsTableModel(new ArrayList<Setting>());
		view.getSettingsTable().setModel(tm);
		view.getSettingsTable().setRowSorter(new TableRowSorter<SettingsTableModel>(tm));
		view.setTopPanelEnabled(false);
		view.resetEditedFields();
	}

	/**
	 * Appends some of group(application) settings to user(group)
	 */
	private void appendSettingsTree(List<?> settings, boolean containsInheritance){
		List<Setting> categories = new ArrayList<Setting>();
		for (int i = 0; i < Setting.SETTINGS_MENU_TREE_NODES.length; i++){
			String cat = Setting.SETTINGS_MENU_TREE_NODES[i].replaceAll("_", "");
			categories.add(new UserSetting(null, null, cat, ""));
		}

		Map<Setting, List<Setting>> settingMap = new HashMap<Setting, List<Setting>>();
		for (Setting cat : categories)
			settingMap.put(cat, new ArrayList<Setting>());

		List<?> settingsForTree = null;
		if (containsInheritance){
			settingsForTree = getInheritantSettings();
		} else{
			settingsForTree = settings;
		}

		for (int i = 0; settingsForTree != null && i < settingsForTree.size(); i++){
			Setting setting = (Setting) settingsForTree.get(i);
			for (Iterator<Setting> it = settingMap.keySet().iterator(); it.hasNext();){
				Setting key = it.next();
				if (setting.getProp().startsWith(key.getProp() + "_")){
					settingMap.get(key).add(setting);
					break;
				}
			}
		}

		for (Iterator<Setting> it = settingMap.keySet().iterator(); it.hasNext();){
			Setting key = it.next();
			List<Setting> children = settingMap.get(key);
			boolean selected = false;
			for (Setting child : children){
				if ("1".equals(child.getValue()) || "true".equalsIgnoreCase(child.getValue())){
					selected = true;
					break;
				}
			}
			key.setValue(String.valueOf(selected).toUpperCase());
		}

		SettingsTreeModel settingsTreeModel = new SettingsTreeModel(settingMap, categories);
		getView().getSettingsTree().setModel(settingsTreeModel);

	}

	public void populateDataToCombo(){
		Language lang = null;
		Schema schema = null;
		TabSwitchAction tsa = null;
		UpdateAccessType updateAccess = null;
		DeleteAccessType deleteAccess = null;
		boolean hideGisPanel = false;
		String docUrl = null;

		List<?> settings = model.getCurrentSettings();

		boolean isInherit = areInheritantSettings(settings);

		if (isInherit){
			settings = getInheritantSettings();
		}

		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp() == null){
				continue;
			}
			if (s.getProp().equals(Setting.LANGUAGE_PROPERTY)){
				lang = getLanguage(s.getValue());
			} else if (s.getProp().equals(Setting.SCHEME_PROPERTY)){
				schema = getSchema(s.getValue());
			} else if (s.getProp().equals(Setting.HIDE_GIS_PANEL_PROPERTY)){
				hideGisPanel = s.getValue() != null && ("true".equalsIgnoreCase(s.getValue()) || "1".equals(s.getValue()));
			} else if (s.getProp().equals(Setting.TAB_SWITCH_ACTION_PROPERTY)){
				if (SavePromptDialog.SAVE_ACTION.equals(s.getValue())){
					tsa = tabSwitchActions[SAVE_ACTION_INDEX];
				} else if (SavePromptDialog.DISCARD_ACTION.equals(s.getValue())){
					tsa = tabSwitchActions[DISCARD_ACTION_INDEX];
				} else{
					tsa = tabSwitchActions[ALWAYS_ASK_ACTION_INDEX];
				}
			} else if (s.getProp().equals(Setting.OBJECT_UPDATE_RIGHTS_PROPERTY)){
				updateAccess = UpdateAccessType.getAccessType(s.getValue());
			} else if (s.getProp().equals(Setting.OBJECT_DELETE_RIGHTS_PROPERTY)){
				deleteAccess = DeleteAccessType.getAccessType(s.getValue());
			} else if (s.getProp().equals(Setting.HELP_DOCUMENT_URL_PROPERTY)){
				docUrl = s.getValue();
			}
		}

		view.setSelectedLanguage(lang);
		view.setSelectedSchema(schema);
		view.setCurrentAction(tsa);
		view.setUpdateAccess(updateAccess);
		view.setDeleteAccess(deleteAccess);
		view.setHideGisPanelSelected(hideGisPanel);
		view.setDocumenatationUrlText(docUrl);
		view.setPasswordFormatText(getPasswordFormatSettingValue());
	}

	private String getPasswordFormatSettingValue(){
		List<ApplicationSetting> appSettings = model.getApplicationSettings();
		if (appSettings == null)
			return null;
		for (Setting as : appSettings){
			if (as.getProp().equals(Setting.PASSWORD_COMPLEXITY_SETTING)){
				return as.getValue();
			}
		}
		return null;
	}

	private Schema getSchema(String value){
		if (model.getSchemas() == null || value == null){
			return null;
		}
		for (Schema schema : model.getSchemas()){
			if (schema.getId().toString().equals(value)){
				return schema;
			}
		}
		return null;
	}

	private Language getLanguage(String value){
		if (model.getLanguages() == null || value == null){
			return null;
		}
		for (Language language : model.getLanguages()){
			if (language.getId().toString().equals(value)){
				return language;
			}
		}
		return null;
	}

	public void addSetting(){
		if (!model.isCurrent())
			return;
		log.debug("Add new setting");
		Setting newSetting = null;
		if (model.isCurrentApplication()){
			ApplicationSetting as = new ApplicationSetting();
			as.setNew(true);
			model.getApplicationSettings().add(as);
			newSetting = as;
		} else if (model.isCurrentGroup()){
			Group g = (Group) model.getCurrentObject();
			GroupSetting gs = new GroupSetting();
			gs.setNew(true);
			gs.setGroup(g);
			g.getGroupSettings().add(gs);
			newSetting = gs;
		} else if (model.isCurrentUser()){
			User u = (User) model.getCurrentObject();
			UserSetting us = new UserSetting();
			us.setNew(true);
			us.setUser(u);
			u.getSettings().add(us);
			newSetting = us;
		}

		AbstractTableModel tableModel = (AbstractTableModel) view.getSettingsTable().getModel();
		int index = tableModel.getRowCount() - 1;
		tableModel.fireTableRowsInserted(index, index);
		view.setActiveTableRow(newSetting);
	}

	public void deleteSettings(){
		if (!model.isCurrent())
			return;
		log.debug("Deleting setting");
		int[] selected = view.getSettingsTable().getSelectedRows();
		if (selected.length == 0)
			return;
		for (int i = 0; i < selected.length; i++)
			selected[i] = view.getSettingsTable().convertRowIndexToModel(selected[i]);
		Arrays.sort(selected);

		List<?> settings = model.getCurrentSettings();
		if (settings == null)// oops
			return;
		int offset = 0;
		for (int i = 0; i < selected.length; i++){
			if (model.isCurrentApplication())
				model.addDeletableApplicationSetting((ApplicationSetting) settings.get(selected[i] - offset));
			settings.remove(selected[i] - offset);
			offset++;
		}
		AbstractTableModel model = (AbstractTableModel) view.getSettingsTable().getModel();
		model.fireTableRowsDeleted(selected[0], selected[0]);

		if (selected[0] >= 0 && settings.size() > 0){
			Setting next = (selected[0] < settings.size()) ? (Setting) settings.get(selected[0]) : (Setting) settings
			        .get(settings.size() - 1);
			view.setActiveTableRow(next);
		}
	}

	/**
	 * @return true if settings contain INHERITS_GROUP_SETTINGS_PROPERTY = true
	 */
	public boolean areInheritantSettings(List<?> settings){
		boolean inherits = false;
		if (!model.isCurrentGroup() && !model.isCurrentUser()){
			return inherits;
		}
		for (int i = 0; i < settings.size(); i++){
			Setting setting = (Setting) settings.get(i);
			if (setting.getProp().equals(UserSetting.INHERITS_GROUP_SETTINGS_PROPERTY)
			        && ("true".equalsIgnoreCase(setting.getValue()) || "1".equals(setting.getValue()))){
				inherits = true;
				break;
			}

		}
		return inherits;
	}

	/**
	 * @return true if settings contain HIDE_GIS_PANEL_PROPERTY = true
	 */
	public boolean isGISPanelHiddenInSettings(List<?> settings){
		boolean gisPanelHidden = false;
		if (settings == null)
			return gisPanelHidden;
		for (int i = 0; i < settings.size(); i++){
			Setting setting = (Setting) settings.get(i);
			if (setting.getProp().equals(UserSetting.HIDE_GIS_PANEL_PROPERTY)
			        && ("true".equalsIgnoreCase(setting.getValue()) || "1".equals(setting.getValue()))){
				gisPanelHidden = true;
				break;
			}

		}
		return gisPanelHidden;
	}

	public void submitSettings(){
		if (!model.isCurrent())
			return;
		log.debug("Submitting settings");

		SettingsService service = ACSpringFactory.getInstance().getSettingsService();

		List<?> settings = model.getCurrentSettings();
		boolean inherits = areInheritantSettings(settings);

		TreeModel treeModel = view.getSettingsTree().getModel();
		SettingsTreeModel stm = (SettingsTreeModel) treeModel;
		List<Setting> menuSettings = stm.getAllSettings();
		for (Setting us2 : menuSettings){
			for (int i = 0; i < settings.size(); i++){
				Setting us1 = (Setting) settings.get(i);
				if (us1.getProp().equals(us2.getProp())){
					if (!inherits)
						us1.setValue(us2.getValue());
					else{
						settings.remove(us1);
					}
					break;
				}
			}
		}

		if (model.isCurrentApplication())
			service.updateApplicationSettings(model.getApplicationSettings(), model.getDeletableApplicationSettings());
		else if (model.isCurrentGroup()){
			Group g = (Group) model.getCurrentObject();
			service.updateGroupSettings(g);
		} else if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			service.updateUserSettings(user);
		}
		SessionData.getInstance().resolveUserTabSwitchAction(SessionData.getInstance().getUser());
	}

	@Override
	public void clearData(){
		model.getGroupMap().clear();
		model.getAppSettingsMap().clear();
		model.getLanguageMap().clear();
		model.getSchemaMap().clear();
		model.setCurrentObject(null);
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getUserTree().getModel());
			view.getUserTree().setSelectionPath(found);
			return false;
		}
		return true;
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		if (model.isCurrentApplication()){
			reloadApplicationSettings();
		} else if (model.isCurrentGroup()){
			reloadGroupSettings();
		} else if (model.isCurrentUser()){
			reloadUserSettings();
		}
	}

	public boolean isFixed(Setting us){
		if (us instanceof GroupSetting)
			return isFixedForGroup(us);
		else if (us instanceof UserSetting)
			return isFixedForUser(us);
		return false;
	}

	private boolean isFixedForUser(Setting us){
		if (Setting.INHERITS_GROUP_SETTINGS_PROPERTY.equalsIgnoreCase(us.getProp())
		        || Setting.DEFAULT_FAVORITE_PROPERTY.equalsIgnoreCase(us.getProp()))
			return true;
		return false;
	}

	private boolean isFixedForGroup(Setting us){
		if (us.getProp().equals(Setting.INHERITS_GROUP_SETTINGS_PROPERTY))
			return true;
		return false;
	}

	public List<Setting> getInheritantSettings(){
		List<Setting> inheritantSettings = new ArrayList<Setting>();

		if (model.isCurrentUser()){
			User u = (User) model.getCurrentObject();
			List<Group> groups = u.getGroups();
			for (int i = 0; i < groups.size(); i++){
				List<GroupSetting> gsList = groups.get(i).getGroupSettings();
				boolean inherit = areInheritantSettings(gsList);
				if (!inherit)
					for (GroupSetting gs : gsList)
						inheritantSettings.add(new UserSetting(u, gs.getSection(), gs.getProp(), gs.getValue()));
				else{
					List<ApplicationSetting> appSettings = model.getApplicationSettings();
					for (Setting as : appSettings)
						inheritantSettings.add(new UserSetting(u, as.getSection(), as.getProp(), as.getValue()));
				}
			}
		} else if (model.isCurrentGroup()){
			Group g = (Group) model.getCurrentObject();
			List<ApplicationSetting> appSettings = model.getApplicationSettings();
			for (Setting as : appSettings)
				inheritantSettings.add(new GroupSetting(g, as.getSection(), as.getProp(), as.getValue()));
		}
		return inheritantSettings;
	}
}
