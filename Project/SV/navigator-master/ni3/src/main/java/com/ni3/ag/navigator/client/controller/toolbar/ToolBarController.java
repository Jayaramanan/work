package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.ToolBarPanel;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class ToolBarController{
	private MainPanel mainPanel;
	private ToolBarPanel toolbarPanel;
	private Ni3Document document;

	public ToolBarController(MainPanel mainPanel, Ni3Document doc){
		this.mainPanel = mainPanel;
		this.document = doc;
	}

	public void init(boolean showToolbarPanel){
		if (showToolbarPanel)
			toolbarPanel = new ToolBarPanel();

		ExpressEditModeButtonListener expressEditModeButtonListener = new ExpressEditModeButtonListener(document,
		        toolbarPanel);
		KeyStroke altE = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK);
		mainPanel.registerKeyboardAction(expressEditModeButtonListener, altE, JComponent.WHEN_IN_FOCUSED_WINDOW);

		final ActivityStreamButtonListener activityStreamButtonListener = new ActivityStreamButtonListener(mainPanel);
		KeyStroke altA = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_MASK);
		mainPanel.registerKeyboardAction(activityStreamButtonListener, altA, JComponent.WHEN_IN_FOCUSED_WINDOW);

		if (showToolbarPanel){
			toolbarPanel.init();
			ToolBarNi3Listener toolBarNi3Listener = new ToolBarNi3Listener(this);
			document.registerListener(toolBarNi3Listener);

			toolbarPanel.setUndoButtonListener(new UndoButtonListener(document));
			toolbarPanel.setRedoButtonListener(new RedoButtonListener(document));
			toolbarPanel.setZoomInButtonListener(new ZoomInButtonListener(mainPanel));
			toolbarPanel.setZoomOutButtonListener(new ZoomOutButtonListener(mainPanel));
			toolbarPanel.setSearchButtonListener(new SearchButtonListener(mainPanel, toolbarPanel));
			toolbarPanel.setAdvancedSearchButtonListener(new AdvancedSearchButtonListener(mainPanel));
			toolbarPanel.setSaveButtonListener(new SaveButtonListener(mainPanel));
			toolbarPanel.setFindPathButtonListener(new FindPathButtonListener(mainPanel));
			toolbarPanel.setReleaseButtonListener(new ClearHighlightsButtonListener(document, mainPanel));
			toolbarPanel.setIsolateButtonListener(new IsolateButtonListener(mainPanel));
			toolbarPanel.setRemoveSelectedNodesButtonListener(new RemoveSelectedNodesButtonListener(mainPanel
			        .getGraphController()));
			toolbarPanel.setReloadButtonListener(new ReloadButtonListener(mainPanel));
			toolbarPanel.setClearButtonListener(new ClearButtonListener(document));
			toolbarPanel.setNewSearchRadioListener(new NewSearchRadioListener(document));
			toolbarPanel.setAddSearchRadioListener(new AddSearchRadioListener(document));
			toolbarPanel.setGeoAnalyticsButtonListener(new GeoAnalyticsButtonListener(document));
			toolbarPanel.setCreateDynamicAttributeButtonListener(new CreateDynamicAttributeButtonListener(mainPanel));
			toolbarPanel.setActivityStreamButtonListener(activityStreamButtonListener);
			toolbarPanel.setExpressEditModeButtonListener(expressEditModeButtonListener);
		}
	}

	public ToolBarPanel getToolbarPanel(){
		return toolbarPanel;
	}

	public void invalidate(){
		toolbarPanel.invalidate();
	}
}
