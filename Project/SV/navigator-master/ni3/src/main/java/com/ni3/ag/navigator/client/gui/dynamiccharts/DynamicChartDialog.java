package com.ni3.ag.navigator.client.gui.dynamiccharts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import com.ni3.ag.navigator.client.domain.DynamicChartAttribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.gui.ColorTableCellEditor;
import com.ni3.ag.navigator.client.gui.ColorTableCellRenderer;

public class DynamicChartDialog extends Ni3Dialog implements ActionListener{
	private static final long serialVersionUID = 6491514892587562437L;
	private JButton okButton;
	private JButton cancelButton;
	private JButton clearButton;
	private boolean okPressed = false;
	private DynamicChartTableModel model;
	private JTabbedPane tabbedPane;

	public DynamicChartDialog(){
		super();
		setTitle(UserSettings.getWord("DynamicChart"));
		initComponents();
	}

	protected void initComponents(){
		setModal(true);
		setSize(new Dimension(400, 400));
		setMinimumSize(new Dimension(280, 200));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(380, 35));
		tabbedPane.setSize(new Dimension(380, 35));
		tabbedPane.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				adjustTabs();
			};

			@Override
			public void componentShown(ComponentEvent e){
				adjustTabs();
			}
		});

		JTable table = new JTable();
		model = new DynamicChartTableModel(new ArrayList<DynamicChartAttribute>());
		table.setModel(model);

		// put custom processor for Enter key on table
		table.getActionMap().put("onEnterAction", new TableEnterAction());
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enterStroke, "onEnterAction");

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(table);

		mainPanel.add(tabbedPane);
		mainPanel.add(sp);
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.SOUTH, tabbedPane);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, sp, -50, SpringLayout.SOUTH, mainPanel);

		layout.putConstraint(SpringLayout.NORTH, tabbedPane, 0, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, mainPanel);

		okButton = new JButton(UserSettings.getWord("Ok"));
		cancelButton = new JButton(UserSettings.getWord("Cancel"));
		clearButton = new JButton(UserSettings.getWord("Clear"));

		layout.putConstraint(SpringLayout.NORTH, clearButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, clearButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(clearButton);
		clearButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.WEST, clearButton);
		mainPanel.add(cancelButton);
		cancelButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(okButton);
		okButton.addActionListener(this);

		TableColumn column = table.getColumnModel().getColumn(DynamicChartTableModel.SELECTION_COLUMN_INDEX);
		column.setPreferredWidth(30);
		column.setMinWidth(30);
		column.setMaxWidth(30);
		column = table.getColumnModel().getColumn(DynamicChartTableModel.COLOR_COLUMN_INDEX);
		column.setPreferredWidth(50);
		column.setMinWidth(50);
		column.setMaxWidth(50);

		table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
		table.setDefaultEditor(Color.class, new ColorTableCellEditor(this));
	}

	public void initTabs(Set<Entity> set){
		tabbedPane.removeAll();
		for (Entity entity : set){
			tabbedPane.addTab(entity.Name, null);
		}
		adjustTabs();
	}

	public void adjustTabs(){
		final int rowCount = tabbedPane.getTabRunCount();
		int size = 10 + rowCount * 16;
		Dimension d = new Dimension(tabbedPane.getPreferredSize().width, size);
		tabbedPane.setPreferredSize(d);
		validate();
		repaint();
	}

	public void attTabChangeListener(ChangeListener l){
		tabbedPane.addChangeListener(l);
	}

	public DynamicChartTableModel getTableModel(){
		return model;
	}

	public int getSelectedTabIndex(){
		return tabbedPane.getSelectedIndex();
	}

	public void actionPerformed(ActionEvent e){
		if (e.getSource().equals(okButton)){
			onEnterAction();
		} else if (e.getSource().equals(cancelButton)){
			okPressed = false;
			setVisible(false);
		} else if (e.getSource().equals(clearButton)){
			model.clearSelection();
		}
	}

	protected void onEnterAction(){
		okPressed = true;
		setVisible(false);
	}

	public boolean isOkPressed(){
		return okPressed;
	}

	public void showDialog(){
		okPressed = false;
		double screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		double screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		setLocation((int) (screenWidth / 2) - getWidth() / 2, (int) (screenHeight / 2) - getHeight() / 2);
		setVisible(true);
	}

	private class TableEnterAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			onEnterAction();
		}
	}
}