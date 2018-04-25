/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.tree.TreePath;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;


public class NodeMetaphorView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private JSplitPane splitApplicationSetup;
	private NodeMetaphorRightPanel rightPanel;
	private NodeMetaphorLeftPanel leftPanel;

	private boolean metaphorSetCopied;
	private boolean metaphorSetDeleted;

	private ErrorPanel errorPanel;

	private NodeMetaphorView(){
	}

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

		splitApplicationSetup.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		leftPanel = new NodeMetaphorLeftPanel();
		splitApplicationSetup.setLeftComponent(leftPanel);

		rightPanel = new NodeMetaphorRightPanel();
		splitApplicationSetup.setRightComponent(rightPanel);
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

	public NodeMetaphorLeftPanel getLeftPanel(){
		return leftPanel;
	}

	public NodeMetaphorRightPanel getRightPanel(){
		return rightPanel;
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

	@Override
	public boolean isChanged(){
		rightPanel.stopCellEditing();
		ChangeResetable[] changedComps = rightPanel.getChangeResetableComponents();
		for (ChangeResetable cr : changedComps){
			if (cr.isChanged()){
				return true;
			}
		}
		return metaphorSetCopied || metaphorSetDeleted;
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		ObjectDefinition currentObject = holder.getCurrentObject();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class, ObjectDefinition.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getLeftPanel().getTreeModel());
			if (currentObject != null && !currentObject.equals(found.getLastPathComponent()) && currentPath.length > 3){
				found = new TreeModelSupport().findPathByNodes(Arrays.copyOf(currentPath, 3), getLeftPanel().getTreeModel());
			}
			getLeftPanel().setSelectionTreePath(found);
		}
	}

	public void resizeNodeMetaphorTableColumns(){
		rightPanel.resizeTableColumns();
	}

	public void setMetaphorSetCopied(boolean b){
		metaphorSetCopied = b;
	}

	public void setMetaphorSetDeleted(boolean b){
		metaphorSetDeleted = b;
	}

	public void updateTree(){
		leftPanel.updateTree();
	}
}
