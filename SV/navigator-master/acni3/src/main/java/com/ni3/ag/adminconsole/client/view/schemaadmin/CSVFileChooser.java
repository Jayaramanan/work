/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class CSVFileChooser extends JFileChooser{
	private static final long serialVersionUID = -6869665779179989489L;
	private JComboBox csCombo;
	private JComboBox lsCombo;
	private JCheckBox recalcFormulasCheckBox;

	public CSVFileChooser(boolean withRecalc){
		super();
		setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f){
				return f.isDirectory() || f.getName().endsWith(".csv") || f.getName().endsWith(".CSV");
			}

			@Override
			public String getDescription(){
				return "CSV files";
			}
		});
		initSeparatorPanel(withRecalc);
		fillCombo();
	}

	private void initSeparatorPanel(boolean withRecalc){
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(180, 150));
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		JLabel csLabel = new JLabel(Translation.get(TextID.ColumnSeparator));
		panel.add(csLabel);
		JLabel lsLabel = new JLabel(Translation.get(TextID.LineSeparator));
		panel.add(lsLabel);

		csCombo = new JComboBox();
		panel.add(csCombo);
		lsCombo = new JComboBox();
		panel.add(lsCombo);
		recalcFormulasCheckBox = new JCheckBox(Translation.get(TextID.RecalculateFormulas));
		panel.add(recalcFormulasCheckBox);

		layout.putConstraint(SpringLayout.WEST, csLabel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, csLabel, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, csLabel, 150, SpringLayout.WEST, csLabel);

		layout.putConstraint(SpringLayout.WEST, csCombo, 0, SpringLayout.WEST, csLabel);
		layout.putConstraint(SpringLayout.NORTH, csCombo, 10, SpringLayout.SOUTH, csLabel);
		layout.putConstraint(SpringLayout.EAST, csCombo, 0, SpringLayout.EAST, csLabel);

		layout.putConstraint(SpringLayout.WEST, lsLabel, 0, SpringLayout.WEST, csLabel);
		layout.putConstraint(SpringLayout.NORTH, lsLabel, 20, SpringLayout.SOUTH, csCombo);
		layout.putConstraint(SpringLayout.EAST, lsLabel, 0, SpringLayout.EAST, csLabel);

		layout.putConstraint(SpringLayout.WEST, lsCombo, 0, SpringLayout.WEST, csLabel);
		layout.putConstraint(SpringLayout.NORTH, lsCombo, 10, SpringLayout.SOUTH, lsLabel);
		layout.putConstraint(SpringLayout.EAST, lsCombo, 0, SpringLayout.EAST, csLabel);

		layout.putConstraint(SpringLayout.WEST, recalcFormulasCheckBox, 0, SpringLayout.WEST, lsCombo);
		layout.putConstraint(SpringLayout.NORTH, recalcFormulasCheckBox, 10, SpringLayout.SOUTH, lsCombo);

		recalcFormulasCheckBox.setVisible(withRecalc);

		setAccessory(panel);
	}

	private void fillCombo(){
		fillLineSeparators();
		fillColumnSeparators();
		lsCombo.setSelectedIndex(0);
		csCombo.setSelectedIndex(0);
	}

	private void fillLineSeparators(){
		lsCombo.addItem(new Separator("\r\n", "Windows (\\r\\n)"));
		lsCombo.addItem(new Separator("\n", "Web, Unix (\\n)"));
		lsCombo.addItem(new Separator("\r", "Mac (\\r)"));
	}

	private void fillColumnSeparators(){
		csCombo.addItem(new Separator("\t", "Tab (\\t)"));
		csCombo.addItem(new Separator(",", "Comma (,)"));
		csCombo.addItem(new Separator(";", "Semicolon (;)"));
	}

	public String getLineSeparator(){
		if (lsCombo.getSelectedIndex() >= 0){
			return ((Separator) lsCombo.getSelectedItem()).getSeparator();
		}
		return "\r\n";
	}

	public String getColumnSeparator(){
		if (csCombo.getSelectedIndex() >= 0){
			return ((Separator) csCombo.getSelectedItem()).getSeparator();
		}
		return "\t";
	}

	public boolean isRecalculateFormulas(){
		return recalcFormulasCheckBox.isSelected();
	}

	private class Separator{
		private String separator;
		private String name;

		public Separator(String separator, String name){
			this.separator = separator;
			this.name = name;
		}

		@Override
		public String toString(){
			return name;
		}

		public String getSeparator(){
			return separator;
		}
	}

}
