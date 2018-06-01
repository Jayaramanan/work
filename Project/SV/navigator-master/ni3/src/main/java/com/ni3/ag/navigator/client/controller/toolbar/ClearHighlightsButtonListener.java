package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class ClearHighlightsButtonListener implements ActionListener {
    private Ni3Document document;
    private MainPanel mainPanel;

    public ClearHighlightsButtonListener(Ni3Document document, MainPanel mainPanel) {
        this.document = document;
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        document.Subgraph.clearSelection();
        mainPanel.releasePath();
        mainPanel.resetHalos();
        document.updateNodeSelection();
    }
}
