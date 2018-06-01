package com.ni3.ag.navigator.client.gui.search;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class DescriptionTableCellEditor extends DefaultCellEditor {
    public DescriptionTableCellEditor() {
        super(new JTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        Component c = super.getTableCellEditorComponent(jTable, o, b, i, i1);
        ((JTextField)c).selectAll();
        return c;
    }
}
