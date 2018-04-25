/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.util.EventObject;

import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.domain.Language;

public class LanguageTreeCellEditor extends DefaultTreeCellEditor{

	public LanguageTreeCellEditor(ACTree tree, DefaultTreeCellRenderer renderer){
		super(tree, renderer);
	}

	@Override
	public boolean isCellEditable(EventObject event){
		boolean editable = super.isCellEditable(event);
		return editable && lastPath != null && lastPath.getLastPathComponent() instanceof Language;
	}
}
