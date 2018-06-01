/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gateway.IconGateway;
import com.ni3.ag.navigator.client.gateway.ObjectManagementGateway;
import com.ni3.ag.navigator.client.gateway.ServiceFactory;
import com.ni3.ag.navigator.client.gateway.impl.HttpObjectManagementGatewayImpl;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.gui.graph.Node;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class DlgImageSelector extends Ni3Dialog implements ActionListener{
	private static final Logger log = Logger.getLogger(DlgImageSelector.class);

	private MainPanel parentMP;

	private JPanel panel;
	private SpringLayout spring;
	private JLabel lblImageSelector;
	private JTable metaphorTable;
	private JScrollPane pnlImageSelector;
	private JButton btnCancel;
	private JButton btnOK;
	private JTextField tfFilter;
	private MetaphorTableModel metaphorTableModel;
	private JCheckBox chkRealSize;
	private Node node;

	private boolean nodeSelected;
	private List<Metaphor> metaphors = new ArrayList<Metaphor>();
	private final int minWidth = 50;
	private int currentWidth = 50;

	private IconGateway metaphorIconGateway;

	public DlgImageSelector(MainPanel xparentMP, Node node){
		super();
		metaphorIconGateway = ServiceFactory.getMetaphorProvider();
		parentMP = xparentMP;
		createControls();
		layoutControls();
		loadMetaphors();
		fillTableModel(metaphors);
		setCurrent(node);
		this.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent e){
				btnCancel.doClick();
			}

		});
	}

	private void createControls(){
		setTitle(UserSettings.getWord("Image Selector"));

		this.setMinimumSize(new Dimension(350, 450));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 210, 300);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);

		spring = new SpringLayout();
		panel.setLayout(spring);

		lblImageSelector = new JLabel(UserSettings.getWord("Images"));
		lblImageSelector.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblImageSelector);

		metaphorTable = new JTable();
		metaphorTableModel = new MetaphorTableModel(new ArrayList<Metaphor>());
		metaphorTable.setModel(metaphorTableModel);
		metaphorTable.setDefaultRenderer(Metaphor.class, new MetaphorTableCellRenderer());
		metaphorTable.setGridColor(metaphorTable.getBackground());
		metaphorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		metaphorTable.getActionMap().put("onEnterAction", new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e){
				onEnterAction();
			}
		});
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		metaphorTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enterStroke, "onEnterAction");

		ListSelectionModel selectionModel = metaphorTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e){
				Metaphor metaphor = getSelectedValue();
				if (metaphor != null){
					final ImageIcon icn = metaphor.getScaledRealSizeIcon(false);
					if (icn != null && icn.getIconWidth() > currentWidth){
						currentWidth = icn.getIconWidth();
						resizeColumns();
					}
				}
			}
		});

		pnlImageSelector = new JScrollPane(metaphorTable);
		panel.add(pnlImageSelector);

		btnCancel = new JButton(UserSettings.getWord("Cancel"));
		btnCancel.setActionCommand("btnCancel");
		btnCancel.addActionListener(this);
		panel.add(btnCancel);

		btnOK = new JButton(UserSettings.getWord("OK"));
		btnOK.setActionCommand("btnOK");
		btnOK.addActionListener(this);
		panel.add(btnOK);

		chkRealSize = new JCheckBox(UserSettings.getWord("Real Size"));
		chkRealSize.setActionCommand("chkRealSize");
		chkRealSize.addActionListener(this);
		panel.add(chkRealSize);

		tfFilter = new JTextField();
		tfFilter.getDocument().addDocumentListener(new FilterDocumentListener());
		panel.add(tfFilter);

	}

	private void layoutControls(){
		this.setMinimumSize(new Dimension(150, 300));
		spring.putConstraint(SpringLayout.NORTH, lblImageSelector, 10, SpringLayout.NORTH, panel);
		spring.putConstraint(SpringLayout.WEST, lblImageSelector, 10, SpringLayout.WEST, panel);

		spring.putConstraint(SpringLayout.SOUTH, btnCancel, -5, SpringLayout.SOUTH, panel);
		spring.putConstraint(SpringLayout.EAST, btnCancel, -10, SpringLayout.EAST, panel);

		spring.putConstraint(SpringLayout.EAST, btnOK, -10, SpringLayout.WEST, btnCancel);
		spring.putConstraint(SpringLayout.SOUTH, btnOK, 0, SpringLayout.SOUTH, btnCancel);

		spring.putConstraint(SpringLayout.NORTH, pnlImageSelector, 0, SpringLayout.SOUTH, lblImageSelector);
		spring.putConstraint(SpringLayout.WEST, pnlImageSelector, 10, SpringLayout.WEST, panel);
		spring.putConstraint(SpringLayout.EAST, pnlImageSelector, -10, SpringLayout.EAST, panel);
		spring.putConstraint(SpringLayout.SOUTH, pnlImageSelector, -10, SpringLayout.NORTH, btnOK);

		spring.putConstraint(SpringLayout.WEST, tfFilter, 0, SpringLayout.WEST, pnlImageSelector);
		spring.putConstraint(SpringLayout.SOUTH, tfFilter, -1, SpringLayout.SOUTH, btnOK);
		spring.putConstraint(SpringLayout.EAST, tfFilter, -10, SpringLayout.WEST, btnOK);

		spring.putConstraint(SpringLayout.SOUTH, chkRealSize, 0, SpringLayout.NORTH, pnlImageSelector);
		spring.putConstraint(SpringLayout.EAST, chkRealSize, 0, SpringLayout.EAST, pnlImageSelector);
		spring.putConstraint(SpringLayout.WEST, chkRealSize, -UserSettings.getWord("Real Size").length() * 12,
				SpringLayout.EAST, pnlImageSelector);
	}

	private void fillTableModel(List<Metaphor> metaphors){
		metaphorTableModel.setData(metaphors);
		calcuateCurrentWidth();
		metaphorTableModel.fireTableDataChanged();
		resizeColumns();
	}

	private void calcuateCurrentWidth(){
		currentWidth = minWidth;
		if (chkRealSize.isSelected()){
			for (int i = 1; i < metaphors.size(); i++){
				Metaphor m = metaphors.get(i);
				final ImageIcon icn = m.getScaledRealSizeIcon(false);
				if (icn == null || i == 20){
					break;
				}
				if (icn.getIconWidth() > currentWidth){
					currentWidth = icn.getIconWidth();
				}
			}
		}
	}

	private void resizeColumns(){
		final TableColumn column = metaphorTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(currentWidth + 10);
		final TableColumn column1 = metaphorTable.getColumnModel().getColumn(1);
		column1.setPreferredWidth(300);
		metaphorTable.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if ("btnCancel".equals(command)){
			this.setVisible(false);
			node.selected = nodeSelected;
		}
		if ("chkRealSize".equals(command)){
			calcuateCurrentWidth();
			resizeColumns();
		} else if ("btnOK".equals(command)){
			onEnterAction();
		}
	}

	protected void onEnterAction(){
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		String iconName = null;
		Metaphor metaphor = getSelectedValue();
		if (metaphor != null && metaphor.hasIcon()){
			iconName = metaphor.getDisplayName();
		}
		node.Obj.setAssignedIconName(iconName);

		ObjectManagementGateway objectManagementGateway = new HttpObjectManagementGatewayImpl();
		objectManagementGateway.updateNodeMetaphor(node.ID, iconName);

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.setVisible(false);

		node.selected = nodeSelected;

		parentMP.Doc.updateNodeIcon(node);
	}

	private Metaphor getSelectedValue(){
		Metaphor metaphor = null;
		final int selectedRow = metaphorTable.getSelectedRow();
		if (selectedRow >= 0){
			final int modelIndex = metaphorTable.convertRowIndexToModel(selectedRow);
			if (modelIndex >= 0){
				metaphor = metaphorTableModel.getMetaphor(modelIndex);
			}
		}
		return metaphor;
	}

	private void loadMetaphors(){
		List<String> iconNames = metaphorIconGateway.getIconNames();
		for (String iconName : iconNames){
			metaphors.add(new Metaphor(iconName));
		}
		Collections.sort(metaphors, new Comparator<Metaphor>(){
			@Override
			public int compare(Metaphor o1, Metaphor o2){
				return o1.getName().compareTo(o2.getName());
			}
		});
		metaphors.add(0, new Metaphor(UserSettings.getWord("no image"), false));
		loadIconsInBackground();
	}

	private void loadIconsInBackground(){
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception{
				for (int i = 1; i < metaphors.size(); i++){
					Metaphor metaphor = metaphors.get(i);
					if (metaphor.getIcon() != null){
						continue;
					}
					ImageIcon icon = downloadIcon(metaphor.getName());
					metaphor.setIcon(icon);
				}
				log.debug("Downloaded all icons");
				return null;
			}
		};
		worker.execute();
	}

	private ImageIcon downloadIcon(String name){
		ImageIcon icon = null;
		final Image image = metaphorIconGateway.loadImage(name);
		if (image != null){
			icon = new ImageIcon(image);
		}
		return icon;
	}

	public void setCurrent(Node node){
		this.node = node;
		nodeSelected = node.selected;
		node.selected = true;

		String nodeImage = node.Obj.getAssignedIconName();

		int modelIndex = -1;
		if (nodeImage != null){
			modelIndex = metaphorTableModel.getMetaphorIndex(nodeImage);
		}

		int index = 0;
		if (modelIndex >= 0){
			index = metaphorTable.convertRowIndexToView(modelIndex);
		}

		if (index >= 0 && metaphorTable.getRowCount() > index){
			metaphorTable.getSelectionModel().setSelectionInterval(index, index);
			Rectangle r = metaphorTable.getCellRect(index, 0, true);
			metaphorTable.scrollRectToVisible(r);
		}

		metaphorTable.requestFocusInWindow();
	}

	private void applyFilter(String text){
		if (text != null && text.length() > 0){
			text = text.toLowerCase();

			List<Metaphor> fMetaphors = new ArrayList<Metaphor>();
			for (Metaphor m : metaphors){
				if (m.getDisplayName().toLowerCase().contains(text)){
					fMetaphors.add(m);
				}
			}
			fillTableModel(fMetaphors);
		} else{
			fillTableModel(metaphors);
		}
	}

	public void clearFilter(){
		tfFilter.setText("");
	}

	private class FilterDocumentListener implements DocumentListener{

		@Override
		public void removeUpdate(DocumentEvent e){
			apply(e);
		}

		@Override
		public void insertUpdate(DocumentEvent e){
			apply(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e){
		}

		private void apply(DocumentEvent e){
			Document doc = e.getDocument();
			String text = null;
			try{
				text = doc.getText(0, doc.getLength());
			} catch (BadLocationException e1){
				log.error(e1);
			}
			applyFilter(text);
		}
	}

	private class Metaphor{
		private String name;
		private ImageIcon icon;
		private ImageIcon scaledIcon;
		private ImageIcon scaledRealSizeIcon;
		private boolean hasIcon = true;

		public Metaphor(String name, boolean hasIcon){
			this(name);
			this.hasIcon = hasIcon;
		}

		public Metaphor(String name){
			this.name = name;
		}

		public void setIcon(ImageIcon icon){
			this.icon = icon;
			scaledIcon = null;
			scaledRealSizeIcon = null;
		}

		public ImageIcon getIcon(){
			if (hasIcon){
				return icon;
			}
			return null;
		}

		public ImageIcon getScaledIcon(){
			if (scaledIcon == null && icon != null){
				final Image image = icon.getImage();
				Image newimg = image.getScaledInstance(24, 24, Image.SCALE_FAST);
				scaledIcon = new ImageIcon(newimg);
			}
			return scaledIcon;
		}

		public ImageIcon getScaledRealSizeIcon(boolean forceDownload){
			if (hasIcon && forceDownload && icon == null){
				icon = downloadIcon(name);
			}

			if (scaledRealSizeIcon == null && icon != null){
				int w = icon.getIconWidth();
				int h = icon.getIconHeight();
				if (w > 0 && h > 0){
					if ((w > 120 || h > 120) && w > 0 && h > 0)
						if (w > h){
							h = 120 * h / w;
							w = 120;
						} else{
							w = 120 * w / h;
							h = 120;
						}
					Image img = icon.getImage();
					Image newimg = img.getScaledInstance(w, h, Image.SCALE_FAST);
					scaledRealSizeIcon = new ImageIcon(newimg);
				}
			}
			return scaledRealSizeIcon;
		}

		public String getName(){
			return name;
		}

		public String getDisplayName(){
			return name.replace("%20", " ");
		}

		public boolean hasIcon(){
			return hasIcon;
		}

		@Override
		public String toString(){
			return getDisplayName();
		}
	}

	private class MetaphorTableCellRenderer extends JLabel implements TableCellRenderer{
		private final int minHeight = 20;

		public MetaphorTableCellRenderer(){
			setOpaque(true);
			setBackground(Color.white);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column){
			int height = minHeight;
			ImageIcon icn = null;
			if (value instanceof Metaphor){
				final Metaphor metaphor = (Metaphor) value;

				if (metaphor.hasIcon){
					if (isSelected || chkRealSize.isSelected()){
						icn = metaphor.getScaledRealSizeIcon(isSelected);
					} else{
						icn = metaphor.getScaledIcon();
					}
					setIcon(icn);
					if (icn != null && icn.getIconHeight() >= minHeight){
						height = icn.getIconHeight();
					}
				}
			}

			setIcon(icn);
			table.setRowHeight(row, height);

			return this;
		}
	}

	private class MetaphorTableModel extends AbstractTableModel{

		private static final long serialVersionUID = 1L;
		private List<Metaphor> metaphorList;
		private String[] columnsNames = new String[] { UserSettings.getWord("Icon"), UserSettings.getWord("Name") };
		private Class<?>[] columnClasses = new Class[] { Metaphor.class, String.class };

		@Override
		public Class<?> getColumnClass(int columnIndex){
			return columnClasses[columnIndex];
		}

		public MetaphorTableModel(List<Metaphor> icons){
			this.metaphorList = icons;
		}

		public void setData(List<Metaphor> icons){
			this.metaphorList = icons;
		}

		public int getColumnCount(){
			return columnsNames.length;
		}

		public int getRowCount(){
			if (metaphorList == null)
				return 0;
			return metaphorList.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex){
			Metaphor metaphor = metaphorList.get(rowIndex);
			switch (columnIndex){
				case 0:
					return metaphor;
				case 1:
					return metaphor.getDisplayName();
			}
			return null;
		}

		@Override
		public String getColumnName(int column){
			return columnsNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		}

		public int getMetaphorIndex(String name){
			int index = -1;
			for (int i = 0; i < metaphorList.size(); i++){
				Metaphor m = metaphorList.get(i);
				if (m.getDisplayName().equals(name)){
					index = i;
					break;
				}
			}
			return index;
		}

		public Metaphor getMetaphor(int index){
			return metaphorList.get(index);
		}
	}

}
