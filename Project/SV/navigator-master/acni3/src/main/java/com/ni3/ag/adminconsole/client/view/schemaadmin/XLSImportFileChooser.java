/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class XLSImportFileChooser extends JFileChooser{
	private static final long serialVersionUID = -6869665779179989489L;
	private JCheckBox recalcFormulasCheckBox;

	public XLSImportFileChooser(){
		super();
		setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f){
				return f.isDirectory() || f.getName().endsWith(".xls");
			}

			@Override
			public String getDescription(){
				return "XLS files";
			}
		});
		initSeparatorPanel();
	}

	private void initSeparatorPanel(){
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(150, 150));
		panel.setLayout(new BorderLayout());
		recalcFormulasCheckBox = new JCheckBox(Translation.get(TextID.RecalculateFormulas));
		recalcFormulasCheckBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		panel.add(recalcFormulasCheckBox, BorderLayout.NORTH);

		setAccessory(panel);
	}

	public boolean isRecalculateFormulas(){
		return recalcFormulasCheckBox.isSelected();
	}
}
