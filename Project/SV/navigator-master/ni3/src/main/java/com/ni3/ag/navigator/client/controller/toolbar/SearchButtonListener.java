package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.ToolBarPanel;

public class SearchButtonListener implements ActionListener{
    private MainPanel mainPanel;
    private ToolBarPanel toolbarPanel;

    public SearchButtonListener(MainPanel mainPanel, ToolBarPanel toolbarPanel) {
        this.mainPanel = mainPanel;
        this.toolbarPanel = toolbarPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        mainPanel.performSearch(toolbarPanel.getSearchFieldText());
    }
}
