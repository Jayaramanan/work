/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.datalist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.domain.metaphor.NumericMetaphor;
import com.ni3.ag.navigator.client.domain.query.Order;
import com.ni3.ag.navigator.client.domain.query.Section;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.util.JTableRowHeaderResizer;
import com.ni3.ag.navigator.client.gui.util.SortButtonRenderer;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings("serial")
public class DataSetTable extends JScrollPane implements MouseListener, ActionListener, Ni3ItemListener,
		ListSelectionListener{
	private Entity entity;
	private JViewport list;
	private JTable fixedTable;
	private JTable table;

	private DataSetTableModel fixedModel;
	private DataSetTableModel model;

	private SortButtonRenderer renderer;
	private SortButtonRenderer fixedRenderer;
	private SortButtonRenderer boldRenderer;
	private SortButtonRenderer boldFixedRenderer;
	private MatrixSortOrder order;

	private int headerTotalOperation[]; // 0-none, 1 - sum, 2 - avg

	private int total;
	private int selected;

	private List<Attribute> attributes;
	private final List<ItemsListListener> listeners;
	private final static Color backLightGray = new Color(240, 240, 240);
	private Ni3Document doc;

	public DataSetTable(Ni3Document doc, Entity ent, List<Attribute> attributes, boolean initialCheck, boolean initialStatus){
		this.doc = doc;
		doc.registerListener(this);

		this.attributes = new ArrayList<Attribute>(attributes); // copy
		this.entity = ent;

		fixedTable = new JTable(){
			public TableCellRenderer getCellRenderer(int row, int col){
				switch (col){
					case 0:
						return getDefaultRenderer(Boolean.class);

					case 1:
						return imgRender;

				}
				return dtcr;
			}

			public void valueChanged(ListSelectionEvent e){
				super.valueChanged(e);
				checkSelection(true);
			}

			public TableCellEditor getCellEditor(int row, int col){
				if (col == 0)
					return getDefaultEditor(Boolean.class);

				return null;
			}

			public boolean isCellEditable(int row, int column){
				return (column == 0);
			}
		};
		fixedTable.setName(ent.Name + "FixedDataSetTable");
		fixedTable.getTableHeader().setName(ent.Name + "FixedDataSetTableHeader");
		table = new JTable(){
			public TableCellRenderer getCellRenderer(int row, int col){
				return dtcr;
			}

			public void valueChanged(ListSelectionEvent e){
				super.valueChanged(e);
				checkSelection(false);
			}
		};
		table.setName(ent.Name + "DataSetTable");
		table.getTableHeader().setName(ent.Name + "DataSetTableHeader");

		fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		fixedTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		initRenderers();

		fixedTable.getSelectionModel().addListSelectionListener(this);

		changeColumnsHeaderRenderer(true);

		setItems(new ArrayList<DBObject>(), initialCheck, initialStatus, true);

		setViewportView(table);
		list = new JViewport();
		list.setView(fixedTable);
		list.setPreferredSize(fixedTable.getPreferredSize());
		setRowHeader(list);
		setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());

		addMouseListener(this);
		fixedTable.addMouseListener(this);
		table.addMouseListener(this);

		new JTableRowHeaderResizer(this).setEnabled(true);

		table.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
			}

			public void keyTyped(KeyEvent e){
			}

			public void keyReleased(KeyEvent e){
				if (e.getKeyCode() == 67){
					copyToClipboard(table);
				}
			}
		});

		fixedTable.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
			}

			public void keyTyped(KeyEvent e){
			}

			public void keyReleased(KeyEvent e){
				if (e.getKeyCode() == 67){
					copyToClipboard(fixedTable);
				}
			}
		});
		listeners = new ArrayList<ItemsListListener>();
	}

	public void initRenderers(){
		int fixedColumns = 0;
		for (Attribute a : attributes)
			if (a.getInMatrix() == 1)
				fixedColumns++;

		order = new MatrixSortOrder();
		renderer = new SortButtonRenderer(order, fixedTable, fixedColumns, false);
		fixedRenderer = new SortButtonRenderer(order, table, -2, false);
		boldRenderer = new SortButtonRenderer(order, fixedTable, fixedColumns, true);
		boldFixedRenderer = new SortButtonRenderer(order, table, -2, true);

		initHeaderListeners(fixedTable, fixedRenderer, renderer);
		initHeaderListeners(table, renderer, fixedRenderer);
	}

	private void initHeaderListeners(JTable table, SortButtonRenderer renderer, SortButtonRenderer syncRenderer){
		JTableHeader header = table.getTableHeader();
		final MouseListener[] listeners = header.getMouseListeners();
		for (MouseListener lsn : listeners){
			if (lsn instanceof HeaderListener){
				header.removeMouseListener(lsn);
			}
		}
		header.setReorderingAllowed(false);
		header.addMouseListener(new HeaderListener(header, false, renderer, syncRenderer));
	}

	public void clearOrder(){
		order.clear();
	}

	public void setOrder(Section querySection){
		for (Order o : querySection.getOrder()){
			int column = -3;
			if (o.attr != null){
				final int col = getColumnByAttributeId(o.attr.ID);
				if (col >= 0){
					column = col;
				}
			}
			order.addSort(column, o.attr, o.ent.ID, o.asc);
		}
		fixedModel.sortByColumn(order);
		doc.updateMatrixSort(order, entity.ID);
	}

	public void sort(){
		fixedModel.sortByColumn(order);
	}

	public void copyToClipboard(JTable table){
		int rows[] = table.getSelectedRows();

		StringBuilder bld = new StringBuilder();

		for (int i = 0; i < rows.length; i++){
			boolean first = true;

			for (int n = 2; n < fixedModel.getColumnCount(); n++){
				if (!first)
					bld.append("\t");
				bld.append(fixedModel.getValueAt(rows[i], n));

				first = false;
			}

			for (int n = 0; n < model.getColumnCount(); n++){
				if (!first)
					bld.append("\t");
				bld.append(model.getValueAt(rows[i], n));
				first = false;
			}

			bld.append("\n");
		}

		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection textSelection = new StringSelection(bld.toString());
		systemClipboard.setContents(textSelection, null);
	}

	public void addMouseMotionListener(MouseMotionListener lst){
		super.addMouseMotionListener(lst);
		fixedTable.addMouseMotionListener(lst);
		table.addMouseMotionListener(lst);
	}

	public void addMouseListener(MouseAdapter lst){
		super.addMouseListener(lst);
		fixedTable.addMouseListener(lst);
		table.addMouseListener(lst);
	}

	private void checkSelection(boolean isFixedTable){
		if (isFixedTable){
			int rows[] = fixedTable.getSelectedRows();
			if (rows.length > 0)
				table.setRowSelectionInterval(rows[0], rows[rows.length - 1]);
		} else{
			int rows[] = table.getSelectedRows();
			if (rows.length > 0)
				fixedTable.setRowSelectionInterval(rows[0], rows[rows.length - 1]);
		}
	}

	public int getSelectedCount(){
		return fixedModel.getSelectedCount();
	}

	public int getObjCount(){
		return fixedModel.getRowCount();
	}

	public DBObject objAtPoint(Point pt){
		int ret = rowAtPoint(pt);
		int ret2 = columnAtPoint(pt);
		if (ret != -1 && ret2 != -1)
			return fixedModel.getDBObjectAt(ret).obj;

		return null;
	}

	public int rowAtPoint(Point pt){
		int ret = fixedTable.rowAtPoint(pt);
		if (ret == -1)
			ret = table.rowAtPoint(pt);

		return ret;
	}

	public int columnAtPoint(Point pt){
		int ret = -1;
		if (fixedTable.getWidth() > pt.x)
			ret = fixedTable.columnAtPoint(pt);

		if (ret == -1 && fixedTable.getWidth() + table.getWidth() > pt.x)
			ret = table.columnAtPoint(pt) + fixedTable.getColumnCount();

		return ret;
	}

	public void setSelectionMode(int singleSelection){
		fixedTable.setSelectionMode(singleSelection);
		table.setSelectionMode(singleSelection);
	}

	public void setItems(List<DBObject> items, boolean initialCheck, boolean initialStatus, boolean clearList){
		if (headerTotalOperation == null){
			headerTotalOperation = new int[attributes.size()];

			for (int n = 0; n < headerTotalOperation.length; n++)
				headerTotalOperation[n] = 0;
		}

		if (clearList || fixedModel == null){
			int fixed = 0;
			for (Attribute a : attributes)
				if (a.getInMatrix() == 1)
					fixed++;

			if (fixedModel == null){
				fixedModel = new DataSetTableModel(this, entity, attributes, items, fixed, true, initialCheck, order);
				fixedTable.setModel(fixedModel);
			}
			if (model == null){
				model = new DataSetTableModel(this, entity, attributes, fixedModel, fixed, false);
				table.setModel(model);
			}
			fixedModel.setItems(new ArrayList<DataItem>());
			model.setItems(new ArrayList<DataItem>());
			changeColumnsHeaderRenderer(true);
		} else{
			boolean pack = false;

			if (fixedModel.getRowCount() == 0)
				pack = true;

			fixedModel.addItems(items, initialCheck, initialStatus, order);

			if (pack)
				changeColumnsHeaderRenderer(true);
		}

		table.setRowHeight(20);
		fixedTable.setRowHeight(20);

		fixedTable.doLayout();
		table.doLayout();
		if (list != null)
			list.setPreferredSize(fixedTable.getPreferredSize());
	}

	public void setSelectedValue(DBObject obj, boolean select){
		fixedModel.setSelected(obj, select, true);
	}

	public int rowCount(){
		return model.getRowCount();
	}

	public DataSetTableModel getModel(){
		return fixedModel;
	}

	public DataSetTableModel getScrollableModel(){
		return model;
	}

	public void clearCheck(){
		fixedModel.clearCheck();
	}

	public void addItemsListListener(ItemsListListener ill){
		listeners.add(ill);
	}

	public void notifyListeners(DBObject node, int index, int ClickCount, int Modifier){
		for (ItemsListListener ill : listeners){
			ill.itemSelected(node, index, ClickCount, Modifier);
		}
	}

	public void notifyListeners(DataItem node, int index, boolean Status){
		for (ItemsListListener ill : listeners){
			ill.itemChecked(node, index, Status);
		}
	}

	private DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(){
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int col){
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			int colReal;
			if (table == fixedTable){
				colReal = col - 2;
			} else{
				colReal = fixedModel.getFixedColumns() + col;
			}

			if (colReal >= 0 && colReal < attributes.size()){
				final Attribute attribute = attributes.get(colReal);
				if (attribute.predefined){
					((DefaultTableCellRenderer) comp).setHorizontalAlignment(LEFT);
				} else if (attribute.isNumericAttribute()){
					((DefaultTableCellRenderer) comp).setHorizontalAlignment(RIGHT);

				} else{
					((DefaultTableCellRenderer) comp).setHorizontalAlignment(LEFT);
				}
			}

			if (!isSelected && !hasFocus){
				if (row % 2 == 0){
					comp.setBackground(backLightGray);
				} else{
					comp.setBackground(Color.white);
				}
			}

			return (comp);
		}
	};

	public class SizableLabel extends JLabel{
		public SizableLabel(){
			setHorizontalAlignment(SwingConstants.RIGHT);
			setForeground(new Color(193, 0, 0));
			setFont(new Font(getFont().getName(), Font.BOLD, 10));
		}

		public void setIcon(ImageIcon img, int w, int h){
			super.setIcon(img);
			setPreferredSize(new Dimension(w, h));
			setSize(w, h);
		}

		public void paintComponent(Graphics g){
			if (getIcon() != null && ((ImageIcon) getIcon()).getImage() != null)
				g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getSize().width, getSize().height, this);
			else if (getText() != null && !getText().isEmpty())
				super.paintComponent(g);
		}
	}

	SizableLabel lbl = new SizableLabel();

	private DefaultTableCellRenderer imgRender = new DefaultTableCellRenderer(){
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column){
			lbl.setText("");
			if (value instanceof Image){
				ImageIcon img = new ImageIcon((Image) value);
				lbl.setIcon(img, 16, 16);
			} else if (value instanceof NumericMetaphor){
				lbl.setIcon(null, 16, 16);
				lbl.setText(String.valueOf(((NumericMetaphor) value).getIndex()));
			}

			return lbl;
		}

	};

	class HeaderListener extends MouseAdapter{
		JTableHeader header;
		SortButtonRenderer renderer, syncRenderer;
		int tmpcol;
		boolean fixed;

		HeaderListener(JTableHeader header, boolean fixed, SortButtonRenderer renderer, SortButtonRenderer syncRenderer){
			this.header = header;
			this.renderer = renderer;
			this.syncRenderer = syncRenderer;
			this.fixed = fixed;
		}

		private void sortByColumn(int col, boolean ctrlDown){
			int sortCol = header.getTable().convertColumnIndexToModel(col);
			renderer.setPressedColumn(col);
			header.repaint();

			if (header.getTable().isEditing()){
				header.getTable().getCellEditor().stopCellEditing();
			}

			if (header.getResizingColumn() == null){
				int column = renderer.shift + sortCol;
				final Attribute attribute = column >= 0 ? attributes.get(column) : null;
				if (column < 0 || !attribute.multivalue){
					order.setSort(column, attribute, entity.ID, !ctrlDown);
					((DataSetTableModel) header.getTable().getModel()).sortByColumn(order);
					doc.updateMatrixSort(order, entity.ID);
				}
			}

			fixedTable.repaint();
			table.repaint();
		}

		private void changeTotalOperation(int operation){
			DataSetTableModel m;
			if (fixed)
				m = fixedModel;
			else
				m = model;

			m.setHeaderTotalOperation(tmpcol, operation);
			header.getColumnModel().getColumn(tmpcol).setHeaderValue(m.getColumnName(tmpcol));
			header.resizeAndRepaint();
		}

		private void changeTotalOperationShowMenu(int col, MouseEvent e){
			// do not show dropdown menu for first two columns (checkbox and metaphor)
			if (fixed && col + fixedModel.getShift() < 0 || !isColumnAggregable(col))
				return;

			JMenuItem item;
			JPopupMenu popup = new JPopupMenu();

			tmpcol = col;

			item = new JMenuItem(UserSettings.getWord("None"));
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					changeTotalOperation(0);
				}
			});
			popup.add(item);

			item = new JMenuItem(UserSettings.getWord("Sum"));
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					changeTotalOperation(1);
				}
			});
			popup.add(item);

			item = new JMenuItem(UserSettings.getWord("Avg"));
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					changeTotalOperation(2);
				}
			});
			popup.add(item);

			popup.show(e.getComponent(), e.getX(), e.getY());
		}

		private boolean isColumnAggregable(int col){
			int shift = fixed ? fixedModel.getShift() : model.getShift();
			int index = col + shift;
			return index >= 0 && index < attributes.size() && attributes.get(col + shift).isAggregable();
		}

		public void mousePressed(MouseEvent e){
			int col = header.columnAtPoint(e.getPoint());
			if (col != -1){
				if (e.getButton() == 1)
					sortByColumn(col, e.isControlDown());
				else if (e.getButton() == 3){
					if (headerTotalOperation.length > col)
						changeTotalOperationShowMenu(col, e);
				}
			}
		}

		public void mouseReleased(MouseEvent e){
			renderer.setPressedColumn(-1); // clear
			header.repaint();
		}
	}

	// Sets the preferred width of the visible column specified by vColIndex.
	// The column
	// will be just wide enough to show the column head and the
	// widest cell in the column.
	// margin pixels are added to the left and
	// right
	// (resulting in an additional width of 2*margin pixels).
	public void packColumn(JTable table, int vColIndex, int margin){
		DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null){
			renderer = table.getTableHeader().getDefaultRenderer();
		}

		Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// Get maximum width of column data
		for (int r = 0; r < table.getRowCount(); r++){
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2 * margin;
		// Set the width
		col.setPreferredWidth(width);
	}

	static DBObject clicked = null;

	public void mouseClicked(MouseEvent e){
		if (e.getButton() == MouseEvent.BUTTON1){
			if (e.getClickCount() == 2){
				clicked = objAtPoint(e.getPoint());
				if (clicked == null)
					return;
				int col = columnAtPoint(e.getPoint());
				if (col > 1){
					Attribute attr = attributes.get(col - 2);
					if (attr.isURLAttribute() && clicked.getValue(attr.ID) != null){
						if (attr.multivalue){
							for (Object url : (Object[]) (clicked.getValue(attr.ID))){
								if (url instanceof Value){
									showInBrowser(((Value) url).getValue());
								} else{
									showInBrowser((String) url);
								}
							}
						} else{
							final Object url = clicked.getValue(attr.ID);
							if (url instanceof Value){
								showInBrowser(((Value) url).getValue());
							} else{
								showInBrowser((String) url);
							}
						}
					}
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3){
			clicked = objAtPoint(e.getPoint());
			if (clicked == null)
				return;

			JMenuItem item;
			JPopupMenu popup = new JPopupMenu();

			item = new JMenuItem(UserSettings.getWord("Add selected objects"));
			item.setActionCommand("AddSelection");
			item.addActionListener(this);
			popup.add(item);

			item = new JMenuItem(UserSettings.getWord("Remove selected objects"));
			item.setActionCommand("RemoveSelection");
			item.addActionListener(this);
			popup.add(item);

			DBObject obj = null;
			int row = fixedTable.getSelectedRow();
			if (row != -1)
				obj = fixedModel.getDBObjectAt(row).obj;

			if (obj != null && obj != clicked){
				if (UserSettings.getBooleanAppletProperty("Matrix_Merge_InUse", true)){
					item = new JMenuItem(UserSettings.getWord("Merge with selected"));
					item.setActionCommand("Merge");
					item.addActionListener(this);
					popup.add(item);
				}
			}

			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private boolean showInBrowser(String s){
		boolean result = true;
		if (s != null && !s.isEmpty()){
			String[] urls = s.split(";");
			for (String url : urls){
				result &= MainPanel.showInBrowser(url.trim());
			}
		}
		return result;
	}

	public void mouseEntered(MouseEvent e){
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e){
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e){
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e){
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e){
		if ("AddSelection".equals(e.getActionCommand())){
			int rows[] = fixedTable.getSelectedRows();

			fixedModel.setValuesAt(true, rows);
			fixedTable.clearSelection();

			repaint();
		}
		if ("RemoveSelection".equals(e.getActionCommand())){
			int rows[] = fixedTable.getSelectedRows();

			fixedModel.setValuesAt(false, rows);
			fixedTable.clearSelection();

			repaint();
		} else if ("Merge".equals(e.getActionCommand())){
			int row = fixedTable.getSelectedRow();
			DBObject toObj = fixedModel.getDBObjectAt(row).obj;

			doc.merge(clicked, toObj);

			repaint();
		} else if ("Test".equals(e.getActionCommand())){
			Dimension dim = fixedTable.getPreferredSize();
			dim.width += 50;
			fixedTable.setPreferredSize(dim);
			list.setPreferredSize(dim);

			repaint();
		}

	}

	public List<Integer> getNodeIds(){
		return model.getNodeIds();
	}

	private void changeColumnsHeaderRenderer(boolean toPack){
		TableColumnModel model = fixedTable.getColumnModel();
		int len2 = model.getColumnCount();
		for (int i = 0; i < len2; i++){
			if (toPack)
				packColumn(fixedTable, i, 2);

			if (i > 2 && attributes.get(i - 2).inMetaphor)
				model.getColumn(i).setHeaderRenderer(boldFixedRenderer);
			else
				model.getColumn(i).setHeaderRenderer(fixedRenderer);
		}

		model = table.getColumnModel();
		int len = model.getColumnCount();

		for (int i = 0; i < len; i++){
			if (toPack)
				packColumn(table, i, 2);

			model.getColumn(i).setIdentifier(table.getColumnName(i));
			model.getColumn(i).setHeaderValue(table.getColumnName(i));

			if (attributes.get(i + len2 - 2).inMetaphor)
				model.getColumn(i).setHeaderRenderer(boldRenderer);
			else
				model.getColumn(i).setHeaderRenderer(renderer);
		}
	}

	@Override
	public void event(int EventCode, int SourceID, Object source, Object Param){
		switch (EventCode){
			case MSG_MetaphorSetChanged:
				changeColumnsHeaderRenderer(false);
				table.doLayout();
				fixedTable.doLayout();
				break;

			case MSG_SubgraphObjectsRemoved:
				if (SourceID != SRC_ItemsPanel && Param != null){
					if (Param instanceof DBObject){
						DBObject obj = (DBObject) Param;
						fixedModel.setSelected(obj, false, false);
					} else if (Param instanceof List){
						List<DBObject> ds = (List<DBObject>) Param;
						for (DBObject obj : ds)
							fixedModel.setSelected(obj, false, false);
					}
				}
				break;

			case MSG_NodeIconChanged:
				getModel().fireTableDataChanged();
				break;
		}
	}

	@Override
	public int getListenerType(){
		return SRC_OtherGUIComponents;
	}

	public String updateHeader(){
		int l = model.getColumnCount();
		for (int n = 0; n < l; n++)
			table.getColumnModel().getColumn(n).setHeaderValue(model.getColumnName(n));

		l = fixedModel.getColumnCount();
		for (int n = 0; n < l; n++)
			fixedTable.getColumnModel().getColumn(n).setHeaderValue(fixedModel.getColumnName(n));

		table.getTableHeader().resizeAndRepaint();
		fixedTable.getTableHeader().resizeAndRepaint();

		total = getObjCount();
		selected = getSelectedCount();

		if (total > 0)
			return " (" + selected + "/" + total + ")";

		return "";
	}

	public void setHeaderTotalScope(int newScope){
		model.setHeaderTotalScope(newScope);
		updateHeader();
	}

	@Override
	public void valueChanged(ListSelectionEvent e){
	}

	void adjustTotalOperation(){
		if (headerTotalOperation != null){
			int newHeaderTotalOperation[] = new int[attributes.size()];

			for (int n = 0; n < Math.min(headerTotalOperation.length, newHeaderTotalOperation.length); n++)
				newHeaderTotalOperation[n] = headerTotalOperation[n];

			headerTotalOperation = newHeaderTotalOperation;
		}
	}

	public List<DataItem> getItems(){
		return model.getItems();
	}

	public MatrixSortOrder getOrder(){
		return order;
	}

	public void refreshFixed(){
		fixedModel.fireTableDataChanged();
	}

	public void setItems(List<DataItem> items){
		fixedModel.setItems(items);
	}

	public void changeTableStructure(List<Attribute> attributes){
		this.attributes.clear();
		this.attributes.addAll(attributes); // copy

		final List<DataItem> items = new ArrayList<DataItem>(fixedModel.getItems()); // backup items
		initRenderers();
		adjustTotalOperation();

		fixedModel.fireTableStructureChanged();
		model.fireTableStructureChanged();
		setItems(new ArrayList<DBObject>(), true, true, true);
		setItems(items); // restore items

		changeColumnsHeaderRenderer(true);
	}

	public int getColumnByAttributeId(int attributeId){
		int column = -1;
		for (int i = 0; i < attributes.size(); i++){
			if (attributes.get(i).ID == attributeId){
				column = i;
			}
		}
		return column;
	}

	public Entity getEntity(){
		return entity;
	}

	public JTable getTable(){
		return table;
	}

	public int[] getHeaderTotalOperation(){
		return headerTotalOperation;
	}

	public int getTotal(){
		return total;
	}

	public int getSelected(){
		return selected;
	}

	public int getAttributeCount(){
		return attributes.size();
	}
}
