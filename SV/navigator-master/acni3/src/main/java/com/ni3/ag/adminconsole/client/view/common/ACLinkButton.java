/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;

import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACLinkButton extends ACButton{

	private static final long serialVersionUID = 372521670401449766L;

	private static Map<TextAttribute, Integer> fontAttributes;
	static{
		fontAttributes = new HashMap<TextAttribute, Integer>();
		fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
	}

	public ACLinkButton(TextID id){
		this(id, 14);
	}

	public ACLinkButton(TextID id, int fontSize){
		super(id);
		setUp(fontSize);
	}

	private void setUp(int fontSize){
		setMargin(new Insets(0, 0, 0, 0));
		setBorderPainted(false);
		setContentAreaFilled(false);
		setRolloverEnabled(true);
		Font font = new Font(getFont().getName(), Font.BOLD, fontSize).deriveFont(fontAttributes);
		setFont(font);
		setForeground(Color.BLUE);
		setHorizontalAlignment(SwingConstants.LEFT);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
