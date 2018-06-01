/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.gui.common.Ni3Frame;
import com.ni3.ag.navigator.client.gui.customlayouts.GridLayout2;
import com.ni3.ag.navigator.shared.domain.Cluster;

public class GeoLegendFrame extends Ni3Frame{
	private static final long serialVersionUID = 5918934810532323941L;
	private JPanel panel;
	private DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");

	public GeoLegendFrame(){
		super();
		initComponents();
	}

	protected void initComponents(){
		getContentPane().setLayout(new BorderLayout());
		panel = new JPanel();
		JScrollPane scroll = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(new JPanel(), BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.SOUTH);

		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	public void setLegendData(List<Cluster> clusters, Attribute attribute){
		String title;
		if (attribute.ent == null)
			title = attribute.label;
		else
			title = attribute.ent.Name + "." + attribute.label;
		setLegendData(clusters, title);
	}

	public void setLegendData(List<Cluster> clusters, String title){
		setTitle(title);
		panel.removeAll();
		panel.setLayout(new GridLayout2(clusters.size(), 3, 10, 0));
		for (Cluster cluster : clusters){
			panel.add(createColorRectangle(cluster.getColor()));
			panel.add(createLabel(createRangeText(cluster)));
			panel.add(createLabel(cluster.getDescription()));
		}
		int height = 20 + clusters.size() * 30;
		Dimension size = new Dimension(260, height);

		panel.setPreferredSize(size);
		panel.setMinimumSize(size);
		panel.setSize(size);
		panel.invalidate();
		panel.repaint();
	}

	public void setVisible(boolean flag){
		pack();
		int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		setLocation(new Point(screenWidth - getWidth() - 10, 80));
		super.setVisible(flag);
	}

	private JPanel createLabel(String textToLabel){
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel();
		label.setText(textToLabel);
		label.setFont(new Font("Dialog", Font.BOLD, 12));
		label.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(label);

		return panel;
	}

	private String createRangeText(Cluster cluster){
		final String fromStr = decimalFormat.format(cluster.getFrom());
		final String toStr = decimalFormat.format(cluster.getTo());
		if (fromStr.equalsIgnoreCase(toStr)){
			return fromStr;
		} else{
			return fromStr + " - " + toStr;
		}
	}

	private JPanel createColorRectangle(Color c){
		JPanel panel = new JPanel();
		JLabel label = new JLabel();
		label.setMinimumSize(new Dimension(40, 20));
		label.setPreferredSize(new Dimension(40, 20));
		label.setMaximumSize(new Dimension(40, 20));
		label.setBackground(c);
		label.setOpaque(true);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.setMinimumSize(new Dimension(70, 20));
		panel.setPreferredSize(new Dimension(70, 20));
		panel.add(label);
		return panel;
	}

}
