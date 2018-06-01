/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.polygon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.ColorTableCellEditor;
import com.ni3.ag.navigator.client.gui.ColorTableCellRenderer;
import com.ni3.ag.navigator.client.gui.common.Ni3Frame;
import com.ni3.ag.navigator.client.gui.graph.Node;

public class PolygonLegendFrame extends Ni3Frame{
	private static final long serialVersionUID = 5918934810532323941L;
	private JTable polyTable;
	private PolygonNodeTableModel model;

	public PolygonLegendFrame(){
		super(UserSettings.getWord("Polygon/Polyline legend"));
		initComponents();
	}

	protected void initComponents(){
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);
		polyTable = new JTable();
		scroll.setViewportView(polyTable);
		polyTable.setRowHeight(30);
		polyTable.setBackground(getBackground());
		polyTable.setGridColor(getBackground());
		polyTable.getTableHeader().setVisible(false);

		model = new PolygonNodeTableModel(new ArrayList<Node>(), new HashMap<Integer, Color>());
		polyTable.setModel(model);

		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(new JPanel(), BorderLayout.WEST);
		add(new JPanel(), BorderLayout.EAST);

		setAlwaysOnTop(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		TableColumn column = polyTable.getColumnModel().getColumn(PolygonNodeTableModel.COLOR_COLUMN_INDEX);
		column.setPreferredWidth(50);
		column.setMinWidth(50);
		column.setMaxWidth(50);

		polyTable.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
		polyTable.setDefaultEditor(Color.class, new ColorTableCellEditor(this));
	}

	public TableModel getTableModel(){
		return model;
	}

	public void setData(List<Node> nodes, Map<Integer, Color> polyColors){
		model.setData(nodes, polyColors);

		int height = 60 + nodes.size() * 30;
		Dimension size = new Dimension(280, height);

		setPreferredSize(size);
		setSize(size);
		invalidate();
		repaint();
	}

	@Override
	public void setVisible(boolean visible){
		if (visible){
			setLocation(new Point(200, 80));
		}
		super.setVisible(visible);
	}

}
