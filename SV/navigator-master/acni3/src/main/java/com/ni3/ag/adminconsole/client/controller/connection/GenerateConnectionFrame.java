package com.ni3.ag.adminconsole.client.controller.connection;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateConnectionFrame extends JDialog{
    private JList fromNodeList;
    private JList toNodeList;
    private JList connectionTypeList;
    private ACButton generateButton;
    private boolean resultOk;

    private boolean isAnyFromSelected;
    private boolean isAnyToSelected;
    private boolean isAnyConnectionTypeSelected;

    public GenerateConnectionFrame(ObjectDefinition selectedEdge, List<ObjectDefinition> nodes) {
        setTitle(Translation.get(TextID.GenerateConnectionTypes));
        setModal(true);

        initComponents();
        fillComponentData(selectedEdge, nodes);
        setSize(new Dimension(500, 300));
        setLocationRelativeTo(null);
    }

    private void fillComponentData(ObjectDefinition selectedEdge, List<ObjectDefinition> nodes) {
        DefaultListModel fromNodesModel = new DefaultListModel();
        DefaultListModel toNodesModel = new DefaultListModel();
        DefaultListModel connectionTypesModel = new DefaultListModel();

        List<PredefinedAttribute> connectionTypes = getConnectionTypes(selectedEdge);

        for(ObjectDefinition od : nodes){
            if (selectedEdge.getSchema().equals(od.getSchema())) {
                fromNodesModel.addElement(new CheckListItem(od));
                toNodesModel.addElement(new CheckListItem(od));
            }
        }

        for(PredefinedAttribute pa : connectionTypes)
            connectionTypesModel.addElement(new CheckListItem(pa));

        fromNodeList.setModel(fromNodesModel);
        toNodeList.setModel(toNodesModel);
        connectionTypeList.setModel(connectionTypesModel);
    }

    private List<PredefinedAttribute> getConnectionTypes(ObjectDefinition selectedEdge) {
        for(ObjectAttribute oa : selectedEdge.getObjectAttributes()){
            if(!oa.getName().equals(ObjectAttribute.CONNECTION_TYPE_ATTRIBUTE_NAME))
                continue;
            return oa.getPredefinedAttributes();
        }
        return null;
    }

    private void initComponents() {
        Container pane = getContentPane();
        SpringLayout layout = new SpringLayout();
        pane.setLayout(layout);

        fromNodeList = new JList();
        JScrollPane fromListScroll = new JScrollPane(fromNodeList);
        layout.putConstraint(SpringLayout.NORTH, fromListScroll, 10, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.SOUTH, fromListScroll, -40, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.WEST, fromListScroll, 10, SpringLayout.WEST, pane);
        layout.putConstraint(SpringLayout.EAST, fromListScroll, 150, SpringLayout.WEST, pane);
        pane.add(fromListScroll);

        toNodeList = new JList();
        JScrollPane toListScroll = new JScrollPane(toNodeList);
        layout.putConstraint(SpringLayout.NORTH, toListScroll, 10, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.SOUTH, toListScroll, -40, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.WEST, toListScroll, -150, SpringLayout.EAST, pane);
        layout.putConstraint(SpringLayout.EAST, toListScroll, -10, SpringLayout.EAST, pane);
        pane.add(toListScroll);

        connectionTypeList = new JList();
        JScrollPane connectionTypeScroll = new JScrollPane(connectionTypeList);
        layout.putConstraint(SpringLayout.NORTH, connectionTypeScroll, 10, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.SOUTH, connectionTypeScroll, -40, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.WEST, connectionTypeScroll, 10, SpringLayout.EAST, fromListScroll);
        layout.putConstraint(SpringLayout.EAST, connectionTypeScroll, -10, SpringLayout.WEST, toListScroll);
        pane.add(connectionTypeScroll);

        generateButton = new ACButton(Mnemonic.AltG, TextID.Generate);
        layout.putConstraint(SpringLayout.NORTH, generateButton, -30, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.SOUTH, generateButton, -10, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.EAST, generateButton, -10, SpringLayout.HORIZONTAL_CENTER, pane);
        pane.add(generateButton);

        ACButton closeButton = new ACButton(Mnemonic.AltG, TextID.Close);
        layout.putConstraint(SpringLayout.NORTH, closeButton, -30, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.SOUTH, closeButton, -10, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.WEST, closeButton, 10, SpringLayout.HORIZONTAL_CENTER, pane);
        pane.add(closeButton);

        fromNodeList.setCellRenderer(new NodeCheckCellRenderer());
        toNodeList.setCellRenderer(new NodeCheckCellRenderer());
        connectionTypeList.setCellRenderer(new NodeCheckCellRenderer());

        fromNodeList.addMouseListener(new ListMouseAdapter());
        toNodeList.addMouseListener(new ListMouseAdapter());
        connectionTypeList.addMouseListener(new ListMouseAdapter());

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                resultOk = true;
                setVisible(false);
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        });

        generateButton.setEnabled(false);
    }

    public boolean isOk(){
        return resultOk;
    }

    public List<ObjectDefinition> getSelectedFromNodes() {
        return getObjectArray(fromNodeList);
    }

    public List<ObjectDefinition> getSelectedToNodes() {
        return getObjectArray(toNodeList);
    }

    private List<ObjectDefinition> getObjectArray(JList jlist) {
        ListModel lm = jlist.getModel();
        List<ObjectDefinition> objects = new ArrayList<ObjectDefinition>();
        for(int i = 0; i < lm.getSize(); i++){
            CheckListItem cli = (CheckListItem) lm.getElementAt(i);
            if(!cli.isSelected())
                continue;
            objects.add((ObjectDefinition) cli.getObject());
        }
        return objects;
    }

    private void updateControlState(JList list) {
        if(list == fromNodeList)
            isAnyFromSelected = !getSelectedFromNodes().isEmpty();
        else if(list == toNodeList)
            isAnyToSelected = !getSelectedToNodes().isEmpty();
        else if(list == connectionTypeList)
            isAnyConnectionTypeSelected = !getSelectedConnectionTypes().isEmpty();
        generateButton.setEnabled(isAnyFromSelected && isAnyToSelected && isAnyConnectionTypeSelected);
    }

    public List<PredefinedAttribute> getSelectedConnectionTypes() {
        ListModel lm = connectionTypeList.getModel();
        List<PredefinedAttribute> objects = new ArrayList<PredefinedAttribute>();
        for (int i = 0; i < lm.getSize(); i++) {
            CheckListItem cli = (CheckListItem) lm.getElementAt(i);
            if(!cli.isSelected())
                continue;
            objects.add((PredefinedAttribute) cli.getObject());
        }
        return objects;
    }

    class CheckListItem {
        private Object object;
        private boolean isSelected = false;

        public CheckListItem(Object o) {
            this.object = o;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public Object getObject(){
            return object;
        }

        public String toString() {
            if(object instanceof ObjectDefinition)
                return ((ObjectDefinition) object).getName();
            if(object instanceof PredefinedAttribute)
                return ((PredefinedAttribute) object).getLabel();
            return null;
        }
    }

    class NodeCheckCellRenderer extends JCheckBox implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean hasFocus) {
            setEnabled(list.isEnabled());
            setSelected(((CheckListItem) value).isSelected());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setText(value.toString());
            return this;
        }
    }

    private class ListMouseAdapter extends MouseAdapter {
        public ListMouseAdapter() {
        }

        public void mouseClicked(MouseEvent event) {
            JList list = (JList) event.getSource();
            int index = list.locationToIndex(event.getPoint());
            CheckListItem item = (CheckListItem)list.getModel().getElementAt(index);
            item.setSelected(!item.isSelected());
            list.repaint(list.getCellBounds(index, index));

            updateControlState(list);
        }
    }
}
