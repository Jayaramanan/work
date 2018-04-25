/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.licenses.LicenseAdminController;
import com.ni3.ag.adminconsole.client.controller.licenses.LicenseTreeSelectionListener;
import com.ni3.ag.adminconsole.client.controller.licenses.UpdateLicenseListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTextArea;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;


public class LicenseAdminView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;

	private ACTree licenseTree;
	private ErrorPanel errorPanel;
	private ACTextArea licenseDetailsArea;
	private ACTextArea newLicenseArea;
	private ACButton addLicenseButton;
	private ACButton deleteLicenseButton;
	private ACButton saveLicenseButton;

	private JPanel createTopRightPanel(){
		JPanel topPanel = new JPanel();
		SpringLayout topLayout = new SpringLayout();
		topPanel.setLayout(topLayout);

		JLabel licenseDetailsLabel = new JLabel();
		topLayout.putConstraint(SpringLayout.NORTH, licenseDetailsLabel, 0, SpringLayout.NORTH, topPanel);
		topLayout.putConstraint(SpringLayout.WEST, licenseDetailsLabel, 0, SpringLayout.WEST, topPanel);
		licenseDetailsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		licenseDetailsLabel.setText(Translation.get(TextID.LabelLicenseDetails));
		topPanel.add(licenseDetailsLabel);

		licenseDetailsArea = new ACTextArea();
		JScrollPane licenseScrollPane = new JScrollPane(licenseDetailsArea);
		licenseDetailsArea.setEditable(false);

		topLayout.putConstraint(SpringLayout.WEST, licenseScrollPane, 0, SpringLayout.WEST, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, licenseScrollPane, 0, SpringLayout.EAST, topPanel);
		topLayout.putConstraint(SpringLayout.NORTH, licenseScrollPane, 13, SpringLayout.SOUTH, licenseDetailsLabel);
		topLayout.putConstraint(SpringLayout.SOUTH, licenseScrollPane, 0, SpringLayout.SOUTH, topPanel);
		topPanel.add(licenseScrollPane);

		return topPanel;
	}

	private JPanel createBottomRightPanel(){
		JPanel bottomPanel = new JPanel();
		SpringLayout bottomLayout = new SpringLayout();
		bottomPanel.setLayout(bottomLayout);

		ACToolBar btnBar = new ACToolBar();
		saveLicenseButton = btnBar.makeUpdateButton();
		bottomLayout.putConstraint(SpringLayout.NORTH, btnBar, 0, SpringLayout.NORTH, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.WEST, btnBar, 0, SpringLayout.WEST, bottomPanel);
		bottomPanel.add(btnBar);

		JLabel uploadLicenseLabel = new JLabel();
		bottomLayout.putConstraint(SpringLayout.NORTH, uploadLicenseLabel, 10, SpringLayout.SOUTH, btnBar);
		bottomLayout.putConstraint(SpringLayout.WEST, uploadLicenseLabel, 0, SpringLayout.WEST, bottomPanel);
		uploadLicenseLabel.setHorizontalAlignment(SwingConstants.LEFT);
		uploadLicenseLabel.setText(Translation.get(TextID.LabelUploadLicense));
		bottomPanel.add(uploadLicenseLabel);

		newLicenseArea = new ACTextArea();
		JScrollPane newLicenseScrollPane = new JScrollPane(newLicenseArea);
		bottomLayout.putConstraint(SpringLayout.WEST, newLicenseScrollPane, 0, SpringLayout.WEST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.EAST, newLicenseScrollPane, 0, SpringLayout.EAST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.NORTH, newLicenseScrollPane, 10, SpringLayout.SOUTH, uploadLicenseLabel);
		bottomLayout.putConstraint(SpringLayout.SOUTH, newLicenseScrollPane, 0, SpringLayout.SOUTH, bottomPanel);
		bottomPanel.add(newLicenseScrollPane);

		return bottomPanel;
	}

	private JPanel createRightPanel(){
		JPanel rightPanel = new JPanel();
		SpringLayout rightLayout = new SpringLayout();
		rightPanel.setLayout(rightLayout);

		JSplitPane licenseDetailsSplit = new JSplitPane();
		licenseDetailsSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		rightPanel.add(licenseDetailsSplit);
		licenseDetailsSplit.setDividerLocation((int) (ACMain.getScreenHeight() / 2));

		rightLayout.putConstraint(SpringLayout.WEST, licenseDetailsSplit, 10, SpringLayout.WEST, rightPanel);
		rightLayout.putConstraint(SpringLayout.NORTH, licenseDetailsSplit, 10, SpringLayout.NORTH, rightPanel);
		rightLayout.putConstraint(SpringLayout.SOUTH, licenseDetailsSplit, -10, SpringLayout.SOUTH, rightPanel);
		rightLayout.putConstraint(SpringLayout.EAST, licenseDetailsSplit, -10, SpringLayout.EAST, rightPanel);
		licenseDetailsSplit.setBorder(BorderFactory.createEmptyBorder());
		licenseDetailsSplit.setLeftComponent(createTopRightPanel());
		licenseDetailsSplit.setRightComponent(createBottomRightPanel());
		return rightPanel;
	}

	private JPanel createLeftPanel(){
		JPanel leftPanel = new JPanel();
		SpringLayout leftLayout = new SpringLayout();
		leftPanel.setLayout(leftLayout);
		ACToolBar leftBar = new ACToolBar();
		addLicenseButton = leftBar.makeAddButton();
		deleteLicenseButton = leftBar.makeDeleteButton();
		leftLayout.putConstraint(SpringLayout.WEST, leftBar, 10, SpringLayout.WEST, leftPanel);
		leftPanel.add(leftBar);
		JScrollPane treeScroll = new JScrollPane();
		licenseTree = new ACTree();
		licenseTree.setCellRenderer(new LicenseTreeCellRenderer());
		treeScroll.setViewportView(licenseTree);
		leftLayout.putConstraint(SpringLayout.NORTH, treeScroll, 0, SpringLayout.SOUTH, leftBar);
		leftLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);
		leftLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanel.add(treeScroll);
		return leftPanel;
	}

	@Override
	public void initializeComponents(){
		SpringLayout mainLayout = new SpringLayout();
		setLayout(mainLayout);
		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane mainSplit = new JSplitPane();
		mainLayout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, errorPanel);
		mainLayout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		mainLayout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		add(mainSplit);
		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));
		mainSplit.setRightComponent(createRightPanel());
		mainSplit.setLeftComponent(createLeftPanel());
	}

	@Override
	public boolean isChanged(){
		return newLicenseArea.isChanged();
	}

	@Override
	public void resetEditedFields(){
		newLicenseArea.setText("");
	}

	public void setNewLicenseAreaEditable(boolean editable){
		newLicenseArea.setEditable(editable);
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] {});
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			licenseTree.setSelectionPath(found);
		}
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		if (errors == null || errors.isEmpty())
			errorPanel.clearErrorMessage();
		else{
			List<String> msgs = new ArrayList<String>();
			for (int i = 0; i < errors.size(); i++){
				ErrorEntry err = errors.get(i);
				msgs.add(Translation.get(err.getId(), err.getErrors()));
			}
			errorPanel.setErrorMessages(msgs);
		}
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	public void setLicenseTreeModel(LicenseTreeModel treeModel){
		licenseTree.setModel(treeModel);
	}

	public void addTreeSelectionListener(LicenseTreeSelectionListener listener){
		licenseTree.addTreeSelectionListener(listener);
	}

	public void setSelectionTreePath(TreePath found){
		licenseTree.setSelectionPath(found);
	}

	public TreePath getSelectionTreePath(){
		return licenseTree.getSelectionPath();
	}

	public TreeModel getTreeModel(){
		return licenseTree.getModel();
	}

	public void showLicenseData(String license){
		licenseDetailsArea.setText(license);
	}

	public void addUpdateLicenseListener(UpdateLicenseListener listener){
		saveLicenseButton.addActionListener(listener);
	}

	public void addDeleteLicenseListener(ActionListener listener){
		deleteLicenseButton.addActionListener(listener);
	}

	public void addAddLicenseListener(ActionListener listener){
		addLicenseButton.addActionListener(listener);
	}

	public String getLicenseToUpdate(){
		return newLicenseArea.getText();
	}

	public void setTreeController(LicenseAdminController controller){
		licenseTree.setCurrentController(controller);
	}

	public void updateTreeUI(){
		licenseTree.updateUI();
	}

	public TreePath getTreeSelectionPath(){
		return licenseTree.getSelectionPath();
	}

	public void setTreeSelectionPath(TreePath found){
		licenseTree.setSelectionPath(found);
	}

}
