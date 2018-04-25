/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.ConnectButtonListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.DisconnectButtonListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.ExportButtonListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.TreeMouseListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.UpdateCacheButtonListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACSplitButton;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ObjectDefinitionLeftPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final String SCHEMA_ADMIN = "schemaAdmin";
	private SchemaAdminTreeModel treeModel;
	private ACTree schemaTree;
	private JScrollPane treeScroll;
	private ACButton btnDelete;
	private ACButton btnCopy;
	private ACButton btnAddObjectDefinition;
	private ACButton btnAddSchema;
	private ACSplitButton btnImport;
	private ACButton btnConnect;
	private ACButton btnDisconnect;
	private ACButton btnUpdateCache;
	private ACButton btnAddDSource;
	private ACButton btnDeleteDSource;
	private ACSplitButton btnExport;
	private ACButton btnGenerateDBProperties;
	private JFileChooser xmlChooser;
	private DBPropertiesDialog dbPropertiesDialog;
	private ACToolBar upperToolbar;
	private ACToolBar bottomTBar;

	private int xmlChooserResult = JFileChooser.CANCEL_OPTION;

	public ObjectDefinitionLeftPanel(){
		initComponents();
	}

	private void initComponents(){
		xmlChooser = new JFileChooser();
		xmlChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f){
				return f.isDirectory() || f.getName().endsWith(".xml");
			}

			@Override
			public String getDescription(){
				return "XML files";
			}

		});
		dbPropertiesDialog = new DBPropertiesDialog();

		schemaTree = new ACTree();
		schemaTree.setCellRenderer(new ACTreeCellRenderer());
		schemaTree.setExpandsSelectedPaths(true);

		SpringLayout treeSpringLayout = new SpringLayout();
		setLayout(treeSpringLayout);

		upperToolbar = new ACToolBar();
		btnConnect = upperToolbar.makeConnectButton();
		btnDisconnect = upperToolbar.makeDisconnectButton();
		upperToolbar.remove(btnDisconnect);
		btnAddDSource = upperToolbar.makeAddDatasourceButton();
		btnDeleteDSource = upperToolbar.makeDeleteDatasourceButton();
		btnGenerateDBProperties = upperToolbar.makeGenerateDBPropertiesButton();
		upperToolbar.addSeparator();
		btnUpdateCache = upperToolbar.makeUpdateCacheButton();
		bottomTBar = new ACToolBar();
		btnAddSchema = bottomTBar.makeAddSchemaButton();
		btnAddObjectDefinition = bottomTBar.makeAddObjectButton();
		btnDelete = bottomTBar.makeDeleteSchemaObjectButton();
		btnCopy = bottomTBar.makeCopySchemaButton();
		bottomTBar.addSeparator();
		btnImport = bottomTBar.makeImportSplitButton();
		btnExport = bottomTBar.makeExportSplitButton();
		add(upperToolbar);
		treeSpringLayout.putConstraint(SpringLayout.WEST, upperToolbar, 10, SpringLayout.WEST, this);
		add(bottomTBar);
		treeSpringLayout.putConstraint(SpringLayout.NORTH, bottomTBar, 0, SpringLayout.SOUTH, upperToolbar);
		treeSpringLayout.putConstraint(SpringLayout.WEST, bottomTBar, 10, SpringLayout.WEST, this);

		treeScroll = new JScrollPane(schemaTree);
		treeSpringLayout.putConstraint(SpringLayout.NORTH, treeScroll, 0, SpringLayout.SOUTH, bottomTBar);
		treeSpringLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, this);
		treeSpringLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, this);
		treeSpringLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, this);
		add(treeScroll);

		schemaTree.setName(SCHEMA_ADMIN + "_tree");
		btnConnect.setName(SCHEMA_ADMIN + "_connect");
		btnDisconnect.setName(SCHEMA_ADMIN + "_disconnect");
		btnAddSchema.setName(SCHEMA_ADMIN + "_addSchema");
		btnDelete.setName(SCHEMA_ADMIN + "_delete");
		btnAddObjectDefinition.setName(SCHEMA_ADMIN + "_addObject");
		btnCopy.setName(SCHEMA_ADMIN + "_copySchema");
		btnImport.setName(SCHEMA_ADMIN + "_xmlImport");
	}

	public void addGenerateDBPropertiesButtonListener(ActionListener l){
		btnGenerateDBProperties.addActionListener(l);
	}

	public JTree getSchemaTree(){
		return schemaTree;
	}

	public SchemaAdminTreeModel getTreeModel(){
		return treeModel;
	}

	public void setTreeModel(SchemaAdminTreeModel model){
		this.treeModel = model;
		getSchemaTree().setModel(treeModel);
	}

	public void addDeleteDatasourceButtonListener(ActionListener listener){
		btnDeleteDSource.addActionListener(listener);
	}

	public void addAddDatasourceDialogOkButtonListener(ActionListener listener){
		btnAddDSource.addActionListener(listener);
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		schemaTree.addTreeSelectionListener(tsl);
	}

	public void addCopyButtonListener(ActionListener listener){
		btnCopy.addActionListener(listener);
	}

	public void addDeleteButtonListener(ActionListener listener){
		btnDelete.addActionListener(listener);
	}

	public void addAddSchemaButtonListener(ActionListener listener){
		btnAddSchema.addActionListener(listener);
	}

	public void addAddObjectButtonListener(ActionListener listener){
		btnAddObjectDefinition.addActionListener(listener);
	}

	public void addImportButtonListener(ActionListener listener){
		btnImport.addActionListener(listener);
	}

	public void addConnectButtonListener(ConnectButtonListener listener){
		btnConnect.addActionListener(listener);
	}

	public void addDisconnectButtonListener(DisconnectButtonListener listener){
		btnDisconnect.addActionListener(listener);
	}

	public void addTreeMouseListener(TreeMouseListener listener){
		schemaTree.addMouseListener(listener);
	}

	public void showXMLChooser(){
		xmlChooserResult = xmlChooser.showDialog(this, Translation.get(TextID.Open));
	}

	public File getSelectedXML(){
		if (xmlChooserResult == JFileChooser.APPROVE_OPTION)
			return xmlChooser.getSelectedFile();
		return null;
	}

	public void addExportButtonListener(ExportButtonListener exportSchemaButtonListener){
		btnExport.addActionListener(exportSchemaButtonListener);
	}

	public void addUpdateCacheButtonListener(UpdateCacheButtonListener updateCacheButtonListener){
		btnUpdateCache.addActionListener(updateCacheButtonListener);
	}

	public void showDBPropertiesDialog(String text){
		dbPropertiesDialog.setText(text);
		dbPropertiesDialog.setVisible(true);
	}

	public void setConnected(boolean b){
		if (b)
			upperToolbar.switchButtons(btnConnect, btnDisconnect);
		else
			upperToolbar.switchButtons(btnDisconnect, btnConnect);
	}

	public void setEnabledSchemaButtons(boolean enabled, boolean schemaSelected, boolean objectSelected){
		btnCopy.setEnabled(enabled && schemaSelected);
		btnAddObjectDefinition.setEnabled(enabled && (schemaSelected || objectSelected));
		btnAddSchema.setEnabled(enabled);
		btnImport.setEnabled(enabled);
		btnUpdateCache.setEnabled(enabled);
		btnExport.setEnabled(enabled && (schemaSelected || objectSelected));
		btnDelete.setEnabled(enabled && (schemaSelected || objectSelected));
	}

	public void setNotInitedInstance(boolean b){
		btnConnect.setEnabled(!b);
		btnDisconnect.setEnabled(!b);
	}

	public void resetLabels(){
		btnDelete.setToolTipText(Translation.get(TextID.Delete));
		btnCopy.setToolTipText(Translation.get(TextID.Copy));
		btnAddObjectDefinition.setToolTipText(Translation.get(TextID.AddObject));
		btnAddSchema.setToolTipText(Translation.get(TextID.AddSchema));
		btnImport.resetLabels(new String[] { Translation.get(TextID.Import), Translation.get(TextID.XMLImport),
				Translation.get(TextID.XLSDataImport), Translation.get(TextID.CSVDataImport),
				Translation.get(TextID.XLSSchemaImport), Translation.get(TextID.SalesforceSchemaImport) });
		btnConnect.setToolTipText(Translation.get(TextID.Connect));
		btnDisconnect.setToolTipText(Translation.get(TextID.Disconnect));
		btnUpdateCache.setToolTipText(Translation.get(TextID.InvalidateCache));
		btnAddDSource.setToolTipText(Translation.get(TextID.AddDatasourceToTree));
		btnDeleteDSource.setToolTipText(Translation.get(TextID.RemoveDatasourceFromTree));
		btnExport.resetLabels(new String[] { Translation.get(TextID.Export), Translation.get(TextID.XMLSchemaExport),
				Translation.get(TextID.XLSDataExport), Translation.get(TextID.CSVDataExport),
				Translation.get(TextID.XLSSchemaExport) });
		btnGenerateDBProperties.setToolTipText(Translation.get(TextID.GenerateDatabaseProperties));
	}

	public void updateTree(){
		SwingUtilities.updateComponentTreeUI(schemaTree);

	}

}