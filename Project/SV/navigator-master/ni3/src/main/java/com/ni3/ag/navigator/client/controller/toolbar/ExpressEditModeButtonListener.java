package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.ni3.ag.navigator.client.gui.ToolBarPanel;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class ExpressEditModeButtonListener extends AbstractAction{
    private Ni3Document document;
    private ToolBarPanel toolbarPanel;

    public ExpressEditModeButtonListener(Ni3Document document, ToolBarPanel toolbarPanel) {
        this.document = document;
        this.toolbarPanel = toolbarPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        boolean newState = !document.isExpressEditMode();
        document.setExpressEditMode(newState);
        if(toolbarPanel != null)
            toolbarPanel.setExpressEditMode(newState);
    }
}
