package com.ni3.ag.navigator.client.controller.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.navigator.client.model.Ni3Document;

public class UndoButtonListener implements ActionListener{
    private Ni3Document document;

    public UndoButtonListener(Ni3Document document) {
        this.document = document;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (document.undoredoManager.isLastUndo()) {
            document.setUndoRedoPoint(false);
        }
        document.undoredoManager.back();
    }
}
