/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import com.ni3.ag.navigator.client.domain.ReportTemplate;
import com.ni3.ag.navigator.client.domain.UserSettings;

public class ReportListCellRenderer extends JLabel implements ListCellRenderer{
	private static final long serialVersionUID = 6175824776680398967L;
	private static final int TEXT_POSITION = 150;
	private Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE, 2);
	private Border notSelectedBorder = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY);
	private Icon emptyIcon = null;

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	        boolean cellHasFocus){
		ReportTemplate report = (ReportTemplate) value;
		Icon preview = null;
		if (report != null){
			preview = report.getPreviewIcon();
		}
		if (preview == null){
			preview = getEmptyIcon();
		}

		setIcon(preview);
		setText(report != null ? report.getName() : "");
		setIconTextGap(TEXT_POSITION - preview.getIconWidth());
		setBorder(getBorder(isSelected));
		setPreferredSize(new Dimension(getPreferredSize().width, preview.getIconHeight() + 10));

		return this;
	}

	private Border getBorder(boolean isSelected){
		return isSelected ? selectedBorder : notSelectedBorder;
	}

	private Icon getEmptyIcon(){
		if (emptyIcon == null){
			BufferedImage emptyImage = new BufferedImage(90, 64, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D graphics = (Graphics2D) emptyImage.getGraphics();
			graphics.setBackground(Color.WHITE);
			graphics.clearRect(0, 0, 90, 64);
			graphics.setColor(Color.BLACK);
			graphics.drawString(UserSettings.getWord("NoPreview"), 10, 35);
			emptyIcon = new ImageIcon(emptyImage);
		}
		return emptyIcon;
	}

}