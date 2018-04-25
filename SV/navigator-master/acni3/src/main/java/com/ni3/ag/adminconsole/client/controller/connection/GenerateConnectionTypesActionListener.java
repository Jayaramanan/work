package com.ni3.ag.adminconsole.client.controller.connection;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;

import java.awt.event.ActionEvent;
import java.util.List;

public class GenerateConnectionTypesActionListener extends ProgressActionListener {
    private ObjectConnectionController controller;

    public GenerateConnectionTypesActionListener(ObjectConnectionController objectConnectionController) {
        super(objectConnectionController);
        this.controller = objectConnectionController;
    }

    @Override
    public void performAction(ActionEvent e) {
        ObjectConnectionModel model = controller.getModel();
        ObjectDefinition selectedEdge = model.getCurrentObject();
        if(selectedEdge == null)
            return;
        List<ObjectDefinition> nodes = model.getNodeObjects();
        GenerateConnectionFrame dlg = new GenerateConnectionFrame(selectedEdge, nodes);
        dlg.setVisible(true);
        if(!dlg.isOk())
            return;
        List<ObjectDefinition> fromNodes = dlg.getSelectedFromNodes();
        List<ObjectDefinition> toNodes = dlg.getSelectedToNodes();
        List<PredefinedAttribute> connectionTypes = dlg.getSelectedConnectionTypes();
        controller.generateConnectionTypes(fromNodes, toNodes, connectionTypes);
    }
}
