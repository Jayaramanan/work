package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.gui.common.Ni3Frame;
import com.ni3.ag.navigator.shared.domain.ThematicMap;

public class DeleteThematicMapDialog extends Ni3Dialog{

	private static final long serialVersionUID = 3864844263486827445L;
	private JButton okButton;
	private JButton cancelButton;
	private JTable table;

	public DeleteThematicMapDialog(Ni3Frame parent){
		super(parent);
		setTitle(UserSettings.getWord("DeleteThematicMap"));
		initComponents();
	}

	private void initComponents(){
		setModal(true);
		setSize(new Dimension(300, 400));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		table = new JTable();
		table.setModel(new ThematicMapTableModel(new ArrayList<ThematicMap>()));

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(table);

		mainPanel.add(sp);
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, sp, -40, SpringLayout.SOUTH, mainPanel);

		okButton = new JButton(UserSettings.getWord("Ok"));
		cancelButton = new JButton(UserSettings.getWord("Cancel"));

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(cancelButton);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(okButton);
	}

	public void addOkButtonListener(ActionListener l){
		okButton.addActionListener(l);
	}

	public void addCancelButtonListener(ActionListener l){
		cancelButton.addActionListener(l);
	}

	public void setTableModel(ThematicMapTableModel model){
		table.setModel(model);
		TableColumn column = table.getColumnModel().getColumn(ThematicMapTableModel.SELECTION_COLUMN_INDEX);
		column.setPreferredWidth(30);
		column.setMinWidth(30);
		column.setMaxWidth(30);
	}

	public ThematicMapTableModel getModel(){
		return (ThematicMapTableModel) table.getModel();
	}

	public Set<ThematicMap> getSelectedThematicMaps(){
		return getModel().getSelectedThematicMaps();
	}
}