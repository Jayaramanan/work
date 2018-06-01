package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.navigator.client.gui.MainPanel;

public class CreateDynamicAttributeButtonListener implements ActionListener{
    private MainPanel mainPanel;

    public CreateDynamicAttributeButtonListener(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        mainPanel.createDynamicAttribute();
    }
}
