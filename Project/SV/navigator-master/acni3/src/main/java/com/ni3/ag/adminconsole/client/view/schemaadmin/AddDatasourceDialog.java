/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class AddDatasourceDialog extends JDialog implements ErrorRenderer{

	private static final long serialVersionUID = 1L;

	public static final int DBID_FIELD = 0;
	public static final int DSNAME_FIELD = 1;
	public static final int HOST_FIELD = 2;
	public static final int MAPPATH_FIELD = 3;
	public static final int DOCROOT_FIELD = 4;
	public static final int RASTER_SERVER_FIELD = 5;
	public static final int MODULE_PATH_FIELD = 6;
	public static final int DELTA_THRESHOLD_FIELD = 7;
	public static final int DELTA_OUT_THRESHOLD_FIELD = 8;

	private JTextField dbIDTField;
	private JTextField dataSourceNameTField;
	private JTextField navigatorHostTField;
	private JTextField mappathTField;
	private JTextField docrootTField;
	private JTextField rasterServerTField;
	private JTextField modulePathTField;
	private JTextField deltaWarnThresholdTField;
	private JTextField deltaErrThresholdTField;
	private JTextField deltaOutWarnThresholdTField;
	private JTextField deltaOutErrThresholdTField;
	private ACButton okButton;
	private ACButton cancelButton;
	private JLabel errorLabel;

	public AddDatasourceDialog(){
		super();
		setModal(true);
		setTitle("Add datasource");
		setResizable(false);
		initComponents();
		setSize(350, 330);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	private void initComponents(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		dbIDTField = new JTextField();
		dataSourceNameTField = new JTextField();
		navigatorHostTField = new JTextField();
		mappathTField = new JTextField();
		docrootTField = new JTextField();
		rasterServerTField = new JTextField();
		modulePathTField = new JTextField();
		deltaWarnThresholdTField = new JTextField("100");
		deltaErrThresholdTField = new JTextField("1000");
		deltaOutWarnThresholdTField = new JTextField("100");
		deltaOutErrThresholdTField = new JTextField("1000");
		okButton = new ACButton(Mnemonic.AltO, TextID.Ok);
		cancelButton = new ACButton(Mnemonic.AltC, TextID.Cancel);
		errorLabel = new JLabel();
		errorLabel.setForeground(Color.red);
		errorLabel.setAlignmentY(JLabel.TOP_ALIGNMENT);

		JLabel dbIdLabel = new JLabel(Translation.get(TextID.DatabaseId));
		dbIdLabel.setForeground(Color.red);
		layout.putConstraint(SpringLayout.NORTH, dbIdLabel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, dbIdLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(dbIdLabel);
		JLabel datasourceLabel = new JLabel(Translation.get(TextID.Datasource));
		datasourceLabel.setForeground(Color.red);
		layout.putConstraint(SpringLayout.NORTH, datasourceLabel, 10, SpringLayout.SOUTH, dbIdLabel);
		layout.putConstraint(SpringLayout.WEST, datasourceLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(datasourceLabel);
		JLabel mappathLabel = new JLabel(Translation.get(TextID.PathToMaps));
		layout.putConstraint(SpringLayout.NORTH, mappathLabel, 10, SpringLayout.SOUTH, datasourceLabel);
		layout.putConstraint(SpringLayout.WEST, mappathLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(mappathLabel);
		JLabel docrootLabel = new JLabel(Translation.get(TextID.Docroot));
		layout.putConstraint(SpringLayout.NORTH, docrootLabel, 10, SpringLayout.SOUTH, mappathLabel);
		layout.putConstraint(SpringLayout.WEST, docrootLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(docrootLabel);
		JLabel rasterServerLabel = new JLabel(Translation.get(TextID.RasterServerUrl));
		layout.putConstraint(SpringLayout.NORTH, rasterServerLabel, 10, SpringLayout.SOUTH, docrootLabel);
		layout.putConstraint(SpringLayout.WEST, rasterServerLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(rasterServerLabel);		
		JLabel modulePathLabel = new JLabel(Translation.get(TextID.ModulesPath));
		layout.putConstraint(SpringLayout.NORTH, modulePathLabel, 10, SpringLayout.SOUTH, rasterServerLabel);
		layout.putConstraint(SpringLayout.WEST, modulePathLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(modulePathLabel);
		JLabel deltaThresholdLabel = new JLabel(Translation.get(TextID.DeltaThreshold));
		layout.putConstraint(SpringLayout.NORTH, deltaThresholdLabel, 10, SpringLayout.SOUTH, modulePathLabel);
		layout.putConstraint(SpringLayout.WEST, deltaThresholdLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(deltaThresholdLabel);
		JLabel deltaOutThresholdLabel = new JLabel(Translation.get(TextID.DeltaOutThreshold));
		layout.putConstraint(SpringLayout.NORTH, deltaOutThresholdLabel, 10, SpringLayout.SOUTH, deltaThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, deltaOutThresholdLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(deltaOutThresholdLabel);
		JLabel navHostLabel = new JLabel(Translation.get(TextID.NavigatorHostPortContextRootHtml));
		layout.putConstraint(SpringLayout.NORTH, navHostLabel, 5, SpringLayout.SOUTH, deltaOutThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, navHostLabel, 10, SpringLayout.WEST, this);
		getContentPane().add(navHostLabel);

		layout.putConstraint(SpringLayout.NORTH, dbIDTField, 6, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, dbIDTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, dbIDTField, 195, SpringLayout.WEST, dbIDTField);
		getContentPane().add(dbIDTField);
		layout.putConstraint(SpringLayout.NORTH, dataSourceNameTField, 6, SpringLayout.SOUTH, dbIdLabel);
		layout.putConstraint(SpringLayout.WEST, dataSourceNameTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, dataSourceNameTField, 195, SpringLayout.WEST, dataSourceNameTField);
		getContentPane().add(dataSourceNameTField);
		layout.putConstraint(SpringLayout.NORTH, mappathTField, 6, SpringLayout.SOUTH, datasourceLabel);
		layout.putConstraint(SpringLayout.WEST, mappathTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, mappathTField, 195, SpringLayout.WEST, mappathTField);
		getContentPane().add(mappathTField);
		layout.putConstraint(SpringLayout.NORTH, docrootTField, 6, SpringLayout.SOUTH, mappathLabel);
		layout.putConstraint(SpringLayout.WEST, docrootTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, docrootTField, 195, SpringLayout.WEST, docrootTField);
		getContentPane().add(docrootTField);
		layout.putConstraint(SpringLayout.NORTH, rasterServerTField, 6, SpringLayout.SOUTH, docrootLabel);
		layout.putConstraint(SpringLayout.WEST, rasterServerTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, rasterServerTField, 195, SpringLayout.WEST, rasterServerTField);
		getContentPane().add(rasterServerTField);
		layout.putConstraint(SpringLayout.NORTH, modulePathTField, 6, SpringLayout.SOUTH, rasterServerLabel);
		layout.putConstraint(SpringLayout.WEST, modulePathTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, modulePathTField, 195, SpringLayout.WEST, modulePathTField);
		getContentPane().add(modulePathTField);
		layout.putConstraint(SpringLayout.NORTH, deltaWarnThresholdTField, 6, SpringLayout.SOUTH, modulePathTField);
		layout.putConstraint(SpringLayout.WEST, deltaWarnThresholdTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, deltaWarnThresholdTField, 90, SpringLayout.WEST, deltaWarnThresholdTField);
		getContentPane().add(deltaWarnThresholdTField);
		layout.putConstraint(SpringLayout.NORTH, deltaErrThresholdTField, 6, SpringLayout.SOUTH, modulePathTField);
		layout.putConstraint(SpringLayout.WEST, deltaErrThresholdTField, 15, SpringLayout.EAST, deltaWarnThresholdTField);
		layout.putConstraint(SpringLayout.EAST, deltaErrThresholdTField, 90, SpringLayout.WEST, deltaErrThresholdTField);
		getContentPane().add(deltaErrThresholdTField);
		layout.putConstraint(SpringLayout.NORTH, deltaOutWarnThresholdTField, 6, SpringLayout.SOUTH, deltaThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, deltaOutWarnThresholdTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, deltaOutWarnThresholdTField, 90, SpringLayout.WEST,
		        deltaOutWarnThresholdTField);
		getContentPane().add(deltaOutWarnThresholdTField);
		layout.putConstraint(SpringLayout.NORTH, deltaOutErrThresholdTField, 6, SpringLayout.SOUTH, deltaThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, deltaOutErrThresholdTField, 15, SpringLayout.EAST,
		        deltaOutWarnThresholdTField);
		layout.putConstraint(SpringLayout.EAST, deltaOutErrThresholdTField, 90, SpringLayout.WEST,
		        deltaOutErrThresholdTField);
		getContentPane().add(deltaOutErrThresholdTField);
		layout.putConstraint(SpringLayout.NORTH, navigatorHostTField, 6, SpringLayout.SOUTH, deltaOutThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, navigatorHostTField, 135, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, navigatorHostTField, 195, SpringLayout.WEST, navigatorHostTField);
		getContentPane().add(navigatorHostTField);

		JLabel thresholdDelim = new JLabel("/");
		layout.putConstraint(SpringLayout.NORTH, thresholdDelim, 0, SpringLayout.NORTH, deltaThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, thresholdDelim, 5, SpringLayout.EAST, deltaWarnThresholdTField);
		getContentPane().add(thresholdDelim);
		JLabel thresholdDelim2 = new JLabel("/");
		layout.putConstraint(SpringLayout.NORTH, thresholdDelim2, 0, SpringLayout.NORTH, deltaOutThresholdLabel);
		layout.putConstraint(SpringLayout.WEST, thresholdDelim2, 5, SpringLayout.EAST, deltaOutWarnThresholdTField);
		getContentPane().add(thresholdDelim2);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		okButton.setPreferredSize(new Dimension(80, 25));
		btnPanel.add(okButton);
		cancelButton.setPreferredSize(new Dimension(80, 25));
		btnPanel.add(cancelButton);
		layout.putConstraint(SpringLayout.NORTH, btnPanel, 10, SpringLayout.SOUTH, navigatorHostTField);
		layout.putConstraint(SpringLayout.WEST, btnPanel, 10, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.EAST, btnPanel, 10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnPanel);

		layout.putConstraint(SpringLayout.NORTH, errorLabel, 0, SpringLayout.SOUTH, btnPanel);
		layout.putConstraint(SpringLayout.SOUTH, errorLabel, 25, SpringLayout.NORTH, errorLabel);
		layout.putConstraint(SpringLayout.WEST, errorLabel, 10, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.EAST, errorLabel, 10, SpringLayout.EAST, getContentPane());
		getContentPane().add(errorLabel);

		addCancelButtonListener(new CancelButtonListener());
	}

	private void addCancelButtonListener(CancelButtonListener listener){
		cancelButton.addActionListener(listener);
		cancelButton.addKeyListener(listener);
		dbIDTField.addKeyListener(listener);
		dataSourceNameTField.addKeyListener(listener);
		navigatorHostTField.addKeyListener(listener);
		mappathTField.addKeyListener(listener);
		docrootTField.addKeyListener(listener);
		rasterServerTField.addKeyListener(listener);
		modulePathTField.addKeyListener(listener);
		deltaWarnThresholdTField.addKeyListener(listener);
		deltaErrThresholdTField.addKeyListener(listener);
		deltaOutWarnThresholdTField.addKeyListener(listener);
		deltaOutErrThresholdTField.addKeyListener(listener);
	}

	public void addOkButtonListener(ActionListener actionListener){
		okButton.addActionListener(actionListener);
		dbIDTField.addActionListener(actionListener);
		dataSourceNameTField.addActionListener(actionListener);
		navigatorHostTField.addActionListener(actionListener);
		mappathTField.addActionListener(actionListener);
		docrootTField.addActionListener(actionListener);
		rasterServerTField.addActionListener(actionListener);
		modulePathTField.addActionListener(actionListener);
		deltaWarnThresholdTField.addActionListener(actionListener);
		deltaErrThresholdTField.addActionListener(actionListener);
		deltaOutWarnThresholdTField.addActionListener(actionListener);
		deltaOutErrThresholdTField.addActionListener(actionListener);
	}

	public String getDeltaThreshold(){
		String warn = deltaWarnThresholdTField.getText();
		if ("".equals(warn.trim()))
			warn = "0";
		String err = deltaErrThresholdTField.getText();
		if ("".equals(err.trim()))
			err = "0";
		return warn + "/" + err;
	}

	public String getDeltaOutThreshold(){
		String warn = deltaOutWarnThresholdTField.getText();
		if ("".equals(warn.trim()))
			warn = "0";
		String err = deltaOutErrThresholdTField.getText();
		if ("".equals(err.trim()))
			err = "0";
		return warn + "/" + err;
	}

	public String[] getResults(){
		return new String[] { dbIDTField.getText(), dataSourceNameTField.getText(), navigatorHostTField.getText(),
		        mappathTField.getText(), docrootTField.getText(), rasterServerTField.getText(), modulePathTField.getText(), getDeltaThreshold(),
		        getDeltaOutThreshold() };
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		if (errors != null && !errors.isEmpty()){
			ErrorEntry err = errors.get(0);
			String msg = Translation.get(err.getId(), err.getErrors());
			errorLabel.setText("<html><b>" + msg + "</b></html>");
		}
	}

	public void clear(){
		dbIDTField.setText("");
		dataSourceNameTField.setText("");
		navigatorHostTField.setText("");
		mappathTField.setText("");
		docrootTField.setText("");
		rasterServerTField.setText("");
		modulePathTField.setText("");
		deltaWarnThresholdTField.setText("100");
		deltaErrThresholdTField.setText("1000");
		deltaOutWarnThresholdTField.setText("100");
		deltaOutErrThresholdTField.setText("1000");
		errorLabel.setText("");
	}

	private class CancelButtonListener implements ActionListener, KeyListener{
		@Override
		public void actionPerformed(ActionEvent e){
			close();
		}

		@Override
		public void keyPressed(KeyEvent e){
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				close();
		}

		private void close(){
			clear();
			setVisible(false);
		}

		@Override
		public void keyReleased(KeyEvent e){

		}

		@Override
		public void keyTyped(KeyEvent e){

		}

	}

}
