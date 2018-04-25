package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.navigator.client.controller.graph.GraphController;

public class RemoveSelectedNodesButtonListener implements ActionListener{
    private GraphController graphController;

    public RemoveSelectedNodesButtonListener(GraphController graphController) {
        this.graphController = graphController;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        graphController.removeSelectedNodesFromGraph();
    }
}
