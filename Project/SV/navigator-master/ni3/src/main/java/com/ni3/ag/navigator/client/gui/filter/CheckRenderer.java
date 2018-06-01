/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.filter;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

@SuppressWarnings("serial")
public class CheckRenderer extends JPanel implements TreeCellRenderer{
	private JCheckBox check;
	private TreeLabel label;

	private boolean imaCheck = true;
	Color BackgroundColor;

	public CheckRenderer(Color BackgroundColor){
		setLayout(null);
		add(label = new TreeLabel());
		add(check = new JCheckBox());

		this.BackgroundColor = BackgroundColor;
		//
		check.setBackground(BackgroundColor);

		label.setForeground(UIManager.getColor("Tree.textForeground"));
		label.setBackground(BackgroundColor);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
												  boolean leaf, int row, boolean hasFocus){
		if (!(value instanceof CheckNode)){
			return new JLabel((String) ((DefaultMutableTreeNode) value).getUserObject());
		}

		String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);

		check.setSelected(((CheckNode) value).isSelected());
		label.setFont(tree.getFont());
		label.setText(stringValue);
		label.setSelected(isSelected);
		label.setFocus(hasFocus);
		label.setIcon(((CheckNode) value).getIcon());

		imaCheck = ((CheckNode) value).hasCheckbox();
		check.setVisible(imaCheck);
		return this;
	}

	public Dimension getPreferredSize(){
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		return new Dimension(d_check.width + d_label.width, (d_check.height < d_label.height ? d_label.height
				: d_check.height));
	}

	public void doLayout(){
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();

		int y_check = 0;
		int y_label = 0;
		if (d_check.height < d_label.height){
			y_check = (d_label.height - d_check.height) / 2;
		} else{
			y_label = (d_check.height - d_label.height) / 2;
		}
		check.setLocation(0, y_check);
		check.setBounds(0, y_check, d_check.width, d_check.height - 2);
		label.setLocation(d_check.width, y_label);
		label.setBounds(d_check.width, y_label, d_label.width, d_label.height);

		if (!imaCheck){
			label.setLocation(check.getLocation());
		}
	}

	public void setBackground(Color color){
		if (color != null && check != null && label != null){
			check.setBackground(color);
			label.setBackground(color);
		}

		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}

	public class TreeLabel extends JLabel{
		boolean isSelected;
		boolean hasFocus;

		public TreeLabel(){
		}

		public void setBackground(Color color){
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

		public void paint(Graphics g){
			String str;
			if ((str = getText()) != null){
				if (str.length() > 0){

					if (isSelected){
						g.setColor(new Color(0, 255, 255));
					} else{
						g.setColor(BackgroundColor);
					}

					Dimension d = getPreferredSize();
					int imageOffset = 0;
					Icon currentI = getIcon();
					if (currentI != null){
						imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
					}
					g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 3);
				}
			}
			super.paint(g);
		}

		public Dimension getPreferredSize(){
			Dimension retDimension = super.getPreferredSize();
			if (retDimension != null){
				retDimension = new Dimension(retDimension.width + 3, retDimension.height + 5);
			}
			return retDimension;
		}

		public void setSelected(boolean isSelected){
			this.isSelected = isSelected;
		}

		public void setFocus(boolean hasFocus){
			this.hasFocus = hasFocus;
		}
	}
}
