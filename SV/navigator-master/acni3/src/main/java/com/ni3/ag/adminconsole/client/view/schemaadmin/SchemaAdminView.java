/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class SchemaAdminView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private JSplitPane splitApplicationSetup;
	private ObjectDefinitionRightPanel rightPanel;
	private ObjectDefinitionLeftPanel leftPanel;
	private InfoPanel infoRightPanel;

	private ErrorPanel errorPanel;

	private SchemaAdminView(){
	}

	@Override
	public void initializeComponents(){
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		splitApplicationSetup = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, splitApplicationSetup, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.NORTH, splitApplicationSetup, 0, SpringLayout.SOUTH, errorPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, splitApplicationSetup, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, splitApplicationSetup, 0, SpringLayout.EAST, this);
		add(splitApplicationSetup);

		leftPanel = new ObjectDefinitionLeftPanel();
		splitApplicationSetup.setLeftComponent(leftPanel);

		infoRightPanel = new InfoPanel();

		rightPanel = new ObjectDefinitionRightPanel();
		splitApplicationSetup.setRightComponent(infoRightPanel);
		splitApplicationSetup.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		errorPanel.setErrorLabelName("schemaAdmin_errorPanel");
	}

	public ObjectDefinitionRightPanel getRightPanel(){
		return rightPanel;
	}

	public ObjectDefinitionLeftPanel getLeftPanel(){
		return leftPanel;
	}

	public SchemaAdminTreeModel getTreeModel(){
		return (SchemaAdminTreeModel) getLeftPanel().getTreeModel();
	}

	public void setTreeModel(SchemaAdminTreeModel model){
		getLeftPanel().setTreeModel(model);
	}

	public void setObjectName(String objectName){
		getRightPanel().getObjectName().setText(objectName);
	}

	public void setDescription(String description){
		getRightPanel().getDescription().setText(description);
	}

	public void setSort(String sort){
		getRightPanel().getSort().setText(sort);
	}

	public void setObjectType(ObjectType type){
		getRightPanel().getObjectTypeCombo().setSelectedItem(type);
	}

	public void setCreatedBy(User user){
		getRightPanel().getCreatedBy().setText(user.getUserName());
	}

	public void setCreationDate(Date date){
		getRightPanel().getCreationDate().setText(date != null ? date.toString() : "");
	}

	public void setObjectAttributeTableModel(ObjectAttributeTableModel model){
		getRightPanel().setTableModel(model);
	}

	public void setPredefinedReferenceData(List<Integer> predefinedTypes){
		getRightPanel().setPredefinedReferenceData(predefinedTypes);
	}

	public void renderErrors(ErrorContainer c){
		if (c == null)
			errorPanel.setErrorMessages(null);
		else
			renderErrors(c.getErrors());
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		List<String> msgs = new ArrayList<String>();
		for (int i = 0; i < errors.size(); i++){
			ErrorEntry err = errors.get(i);
			msgs.add(Translation.get(err.getId(), err.getErrors()));
		}
		errorPanel.setErrorMessages(msgs);
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	@Override
	public void resetEditedFields(){
		ChangeResetable[] changedComps = rightPanel.getChangeResetableComponents();
		for (ChangeResetable cr : changedComps)
			cr.resetChanges();
	}

	public JPanel getObjectPanel(){
		return rightPanel;
	}

	public InfoPanel getInfoPanel(){
		return infoRightPanel;
	}

	public void setActiveView(JPanel panel){
		int div = splitApplicationSetup.getDividerLocation();
		splitApplicationSetup.setRightComponent(panel);
		splitApplicationSetup.setDividerLocation(div);
	}

	public void setObjectTableModel(ObjectTableModel objectTableModel){
		getRightPanel().setObjectTableModel(objectTableModel);
	}

	public void setTableObjectTypeReferenceData(List<ObjectType> objectTypes){
		getRightPanel().setTableObjectTypeReferenceData(objectTypes);
	}

	@Override
	public boolean isChanged(){
		getRightPanel().stopEditing();
		for (ChangeResetable cr : getRightPanel().getChangeResetableComponents()){
			if (cr.isChanged()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class, ObjectDefinition.class });
		if (currentPath != null){
			AbstractTreeModel treeModel = getLeftPanel().getTreeModel();
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, treeModel);
			getLeftPanel().getSchemaTree().setSelectionPath(found);
		}
	}

	/**
	 * @return all paths for schema tree
	 */
	public TreePath[] getSchemaTreePaths(){
		JTree tree = getLeftPanel().getSchemaTree();
		return new TreeModelSupport().getPaths(tree);
	}

	public TreePath getSchemaTreeSelectedPath(){
		JTree tree = getLeftPanel().getSchemaTree();
		return tree.getSelectionPath();
	}

	public void setSchemaTreeSelectionPath(TreePath found){
		JTree tree = getLeftPanel().getSchemaTree();
		tree.setSelectionPath(found);
	}

	public void setEnabledSchemaButtons(boolean enabled, boolean schemaSelected, boolean objectSelected){
		getLeftPanel().setEnabledSchemaButtons(enabled, schemaSelected, objectSelected);
	}

	public void setConnected(boolean b){
		getLeftPanel().setConnected(b);
	}

	public void resetLabels(){
		getLeftPanel().resetLabels();
		getRightPanel().resetLabels();
		infoRightPanel.resetLabels();
	}

	public void updateTree(){
		leftPanel.updateTree();
	}

	public void setNotInitedInstance(boolean b){
		leftPanel.setNotInitedInstance(b);
	}

	public void setDataSourceCellEditor(){
		rightPanel.setDataSourceCellEditor();
	}
}