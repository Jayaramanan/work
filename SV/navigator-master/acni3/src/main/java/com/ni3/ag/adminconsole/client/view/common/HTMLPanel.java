/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

public class HTMLPanel extends JPanel{
	private static final long serialVersionUID = 2470822259692885236L;
	JEditorPane editor = new JEditorPane();
	BorderLayout borderLayout1 = new BorderLayout();

	public HTMLPanel(){
		jbInit();
	}

	private void jbInit(){
		this.setLayout(borderLayout1);
		editor.setEditorKit(new HTMLEditorKit());
		editor.setEditable(false);
		this.add(editor, BorderLayout.CENTER);
	}

	protected JEditorPane getJEditorPane(){
		return editor;
	}

	public void showHTML(String html){
		if (html != null && html.length() != 0){
			editor.setText(html);
			editor.setCaretPosition(0);
		}
	}
}