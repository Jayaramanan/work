/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.ni3.ag.navigator.client.controller.LicenseValidator;
import com.ni3.ag.navigator.client.controller.toolbar.ExpressEditModeButtonListener;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.model.SystemGlobals;

@SuppressWarnings("serial")
public class ToolBarPanel extends JToolBar{

	// Components
	private JTextField searchField;
	private JLabel statusLabel;
	private JButton searchButton;
	private JButton combineSearchButton;
	private JButton geoAnalyticsButton;

	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JButton forwardButton;
	private JButton backButton;
	private JButton findPathButton;
	private JButton clearHighlightsButton;
	private JButton removeButton;
	private JButton isolateButton;
	private JButton reloadButton, clearButton;
	private JToggleButton expressEditModeButton;
	private JButton saveButton;
	private JButton createDynamicAttributeButton;
	private JButton activityStreamButton;

	private JRadioButton searchNewRadio;
	private JRadioButton searchAddRadio;

	private JPanel searchResultHeaderPanel;
	private JLabel graphControlLabel;

	IconCache images = new IconCache();

	public ToolBarPanel(){
		super();
	}

	public void init(){
		createComponents();
		layoutComponents();
	}

	private void createComponents(){
		// Zoom in - Zoom out

		statusLabel = new JLabel("");


		backButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_LEFT));
		backButton.setToolTipText(UserSettings.getWord("BACK_BUTTON_TOOLTIP_TEXT"));

		forwardButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_RIGHT));
		forwardButton.setToolTipText(UserSettings.getWord("FORWARD_BUTTON_TOOLTIP_TEXT"));

		zoomInButton = new JButton(images.getImageIcon(IconCache.ZOOM_IN));
		zoomInButton.setToolTipText(UserSettings.getWord("Zoom in"));

		zoomOutButton = new JButton(images.getImageIcon(IconCache.ZOOM_OUT));
		zoomOutButton.setToolTipText(UserSettings.getWord("Zoom out"));

		findPathButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_FINDPATH));
		findPathButton.setToolTipText(UserSettings.getWord("Find path"));

		clearHighlightsButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_CLEAR_HIGHLIGHTS));
		clearHighlightsButton.setToolTipText(UserSettings.getWord("Clear highlights"));

		removeButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_REMOVE));
		removeButton.setToolTipText(UserSettings.getWord("Remove selected"));

		isolateButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_ISOLATE));
		isolateButton.setToolTipText(UserSettings.getWord("Isolate"));

		searchButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_SIMPLE_SEARCH));
		searchButton.setToolTipText(UserSettings.getWord("Search"));

		combineSearchButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_ADVANCED_SEARCH));
		combineSearchButton.setToolTipText(UserSettings.getWord("Advanced search"));

		geoAnalyticsButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_GEO_ANALYTICS));
		geoAnalyticsButton.setToolTipText(UserSettings.getWord("Geo Analytics"));

		reloadButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_RELOAD));
		reloadButton.setToolTipText(UserSettings.getWord("Reload"));

		clearButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_CLEAR));
		clearButton.setToolTipText(UserSettings.getWord("Clear"));

		expressEditModeButton = new JToggleButton(images.getImageIcon(IconCache.TOOLBAR_GRAPH_EDIT_TOGGLE));
		expressEditModeButton.setToolTipText(UserSettings.getWord("Graph edit mode"));

		createDynamicAttributeButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_CREATE_DYNAMIC_ATTRIBUTE));
		createDynamicAttributeButton.setToolTipText(UserSettings.getWord("Create dynamic attribute"));

		searchField = new JTextField(20);
		searchField.setFont(UserSettings.getFont("SEARCH_FIELD_FONT"));
		searchField.setToolTipText(UserSettings.getWord("SEARCH_TOOLTIP_TEXT"));
		searchField.setActionCommand("Search");

		saveButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_SAVE));
		saveButton.setToolTipText(UserSettings.getWord("SaveAs"));

		searchNewRadio = new JRadioButton(UserSettings.getWord("Search new"));
		searchNewRadio.setSelected(true);
		searchAddRadio = new JRadioButton(UserSettings.getWord("Search add"));

		searchResultHeaderPanel = new JPanel(new GridLayout(2, 1));
		ButtonGroup group = new ButtonGroup();
		group.add(searchNewRadio);
		group.add(searchAddRadio);

		activityStreamButton = new JButton(images.getImageIcon(IconCache.TOOLBAR_ACTIVITY_STREAM));
		activityStreamButton.setToolTipText(UserSettings.getWord("ActivityStream"));

		searchResultHeaderPanel.add(searchNewRadio);
		searchResultHeaderPanel.add(searchAddRadio);

		searchResultHeaderPanel.setPreferredSize(new Dimension(20, 20));
		searchResultHeaderPanel.setMinimumSize(new Dimension(1, 1));

		final String userDescription = SystemGlobals.getUser().getFirstName() + " " + SystemGlobals.getUser().getLastName();
		graphControlLabel = new JLabel("  " + userDescription);
	}

	private void layoutComponents(){

		add(backButton);
		add(forwardButton);
		addSeparator();

		add(saveButton);
		addSeparator();

		add(isolateButton);
		add(removeButton);
		add(findPathButton);
		add(clearHighlightsButton);
		addSeparator();

		add(reloadButton);
		add(clearButton);
		addSeparator();

		if (UserSettings.getBooleanAppletProperty("Toolbar_DynamicAttribute_InUse", true))
			add(createDynamicAttributeButton);
		addSeparator();

		LicenseValidator validator = LicenseValidator.getInstance();
		if ((validator.isNodeDataChangeEnabled() && UserSettings.getBooleanAppletProperty("Toolbar_CreateNode_InUse", true))
				|| (validator.isEdgeDataChangeEnabled() && UserSettings.getBooleanAppletProperty("Toolbar_CreateEdge_InUse",
						true))){
			add(expressEditModeButton);
		}
		addSeparator();

		searchField.setMaximumSize(new Dimension(200, 20));
		add(searchField);

		add(searchButton);
		add(combineSearchButton);
		addSeparator();

		if (validator.isGeoAnalyticsEnabled() && UserSettings.getBooleanAppletProperty("GeoAnalytics_InUse", false)){
			add(geoAnalyticsButton);
			addSeparator();
		}

		if (UserSettings.getBooleanAppletProperty("ActivityStream_InUse", false)){
			addSeparator();
			add(activityStreamButton);
		}

		addSeparator();
		add(searchResultHeaderPanel);

		// TODO make client logo
		// String iconName = "ClientLogo.png";
		// if (iconName != null){
		// ClientLogo = IconCache.getInstance().getImageIcon(iconName);
		// add(new JLabel(ClientLogo));
		// add(new JLabel("       "));
		// }

		//String iconName = UserSettings.getStringAppletProperty("logo", "ni3_32.png");
//		if (!"<empty>".equals(iconName)) {
			Icon logo = images.getImageIcon(IconCache.TOOLBAR_NAVIGATOR_LOGO);
			add(new JLabel(logo));
//		}

		add(graphControlLabel);
	}

	public void setQueryNumber(int num){
		if (searchAddRadio != null){
			String txt = UserSettings.getWord("Search add");
			if (num > 0)
				txt += " (" + num + ")";

			searchAddRadio.setText(txt);
		}
	}

	public void showStatus(final String text){
		if (statusLabel != null){
			statusLabel.setText(text);
		}
	}

	public void updateSearchButtonState(Boolean newSearch){
		searchNewRadio.setSelected(newSearch);
		searchAddRadio.setSelected(!newSearch);
	}

	public void setExpressEditModeButtonListener(ExpressEditModeButtonListener expressEditModeButtonListener){
		expressEditModeButton.addActionListener(expressEditModeButtonListener);
	}

	public void setExpressEditMode(boolean newState){
		expressEditModeButton.setSelected(newState);
	}

	public void setUndoButtonListener(ActionListener undoButtonListener){
		backButton.addActionListener(undoButtonListener);
	}

	public void setRedoButtonListener(ActionListener redoButtonListener){
		forwardButton.addActionListener(redoButtonListener);
	}

	public void setZoomInButtonListener(ActionListener zoomInButtonListener){
		zoomInButton.addActionListener(zoomInButtonListener);
	}

	public void setZoomOutButtonListener(ActionListener zoomOutButtonListener){
		zoomOutButton.addActionListener(zoomOutButtonListener);
	}

	public String getSearchFieldText(){
		return searchField.getText().trim();
	}

	public void setSearchButtonListener(ActionListener searchButtonListener){
		searchField.addActionListener(searchButtonListener);
		searchButton.addActionListener(searchButtonListener);
	}

	public void setAdvancedSearchButtonListener(ActionListener advancedSearchButtonListener){
		combineSearchButton.addActionListener(advancedSearchButtonListener);
	}

	public void setSaveButtonListener(ActionListener saveButtonListener){
		saveButton.addActionListener(saveButtonListener);
	}

	public void setFindPathButtonListener(ActionListener findPathButtonListener){
		findPathButton.addActionListener(findPathButtonListener);
	}

	public void setReleaseButtonListener(ActionListener releaseButtonListener){
		clearHighlightsButton.addActionListener(releaseButtonListener);
	}

	public void setIsolateButtonListener(ActionListener isolateButtonListener){
		isolateButton.addActionListener(isolateButtonListener);
	}

	public void setRemoveSelectedNodesButtonListener(ActionListener removeSelectedNodesButtonListener){
		removeButton.addActionListener(removeSelectedNodesButtonListener);
	}

	public void setReloadButtonListener(ActionListener reloadButtonListener){
		reloadButton.addActionListener(reloadButtonListener);
	}

	public void setClearButtonListener(ActionListener clearButtonListener){
		clearButton.addActionListener(clearButtonListener);
	}

	public void setNewSearchRadioListener(ActionListener newSearchRadioListener){
		searchNewRadio.addActionListener(newSearchRadioListener);
	}

	public void setAddSearchRadioListener(ActionListener addSearchRadioListener){
		searchAddRadio.addActionListener(addSearchRadioListener);
	}

	public void setGeoAnalyticsButtonListener(ActionListener geoAnalyticsButtonListener){
		geoAnalyticsButton.addActionListener(geoAnalyticsButtonListener);
	}

	public void setCreateDynamicAttributeButtonListener(ActionListener createDynamicAttributeButtonListener){
		createDynamicAttributeButton.addActionListener(createDynamicAttributeButtonListener);
	}

	public void setActivityStreamButtonListener(ActionListener activityStreamButtonListener){
		activityStreamButton.addActionListener(activityStreamButtonListener);
	}
}
