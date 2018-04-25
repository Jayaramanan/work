/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.treetable;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;

public class ACTreeTable extends ACTable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ACTreeTable.class);
	protected TreeTableCellRenderer tree;

	public ACTreeTable(){
		super();

		tree = new TreeTableCellRenderer();

		tree.setRowHeight(getRowHeight());

		setDefaultRenderer(TreeModel.class, tree);
		setDefaultEditor(TreeModel.class, new TreeTableCellEditor());
		tree.setOpaque(false);
	}

	public void setModel(TreeModel treeModel, ACTableModel tableModel){
		log.debug("Setting new tree table model");
		tree.setModel(treeModel);

		super.setModel(tableModel);

		// Force the JTable and JTree to share their row selection models.
		tree.setSelectionModel(new DefaultTreeSelectionModel(){
			{
				setSelectionModel(listSelectionModel);
			}
		});

		tree.setCellRenderer(new ACTreeCellRenderer());
	}

	public JTree getTree(){
		return tree;
	}

	public int getEditingRow(){
		if (getColumnClass(editingColumn) == TreeModel.class && getSelectedRow() != editingRow){
			setRowSelectionInterval(editingRow, editingRow);
		}
		return (getColumnClass(editingColumn) == TreeModel.class) ? -1 : editingRow;
	}

	/**
	 * The renderer used to display the tree nodes, a JTree.
	 */
	public class TreeTableCellRenderer extends JTree implements TableCellRenderer{

		private static final long serialVersionUID = 1L;
		protected int visibleRow;

		public TreeTableCellRenderer(){
			super();
		}

		public void setBounds(int x, int y, int w, int h){
			super.setBounds(x, 0, w, ACTreeTable.this.getHeight());
		}

		public void paint(Graphics g){
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
		        int row, int column){
			if (isSelected)
				setBackground(table.getSelectionBackground());
			else
				setBackground(table.getBackground());

			visibleRow = row;
			return this;
		}
	}

	/**
	 * The editor used to interact with tree nodes
	 */
	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor{
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c){
			return tree;
		}
	}

}
