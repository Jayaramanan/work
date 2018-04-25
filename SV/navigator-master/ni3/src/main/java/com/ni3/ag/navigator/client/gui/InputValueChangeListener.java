package com.ni3.ag.navigator.client.gui;

import javax.swing.event.*;

/**
 * Created by ilya on 8.5.16.
 */
public class InputValueChangeListener implements DocumentListener, ListDataListener, TreeModelListener {
    private boolean valuesHaveChanged = false;

    //text field related
    @Override
    public void insertUpdate(DocumentEvent e) {
        setValuesHaveChanged(true);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        setValuesHaveChanged(true);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        setValuesHaveChanged(true);
    }

    //Combobox related
    @Override
    public void intervalAdded(ListDataEvent e) {
        System.out.println("intervalAdded");
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        System.out.println("intervalRemoved");
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        setValuesHaveChanged(true);
    }

    //tree related
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        setValuesHaveChanged(true);
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        System.out.println("treeNodesInserted");
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        System.out.println("treeNodesRemoved");

    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        System.out.println("treeStructureChanged");
    }

    public boolean isValuesHaveChanged() {
        return valuesHaveChanged;
    }

    private void setValuesHaveChanged(boolean valuesHaveChanged) {
        this.valuesHaveChanged = valuesHaveChanged;
    }
}