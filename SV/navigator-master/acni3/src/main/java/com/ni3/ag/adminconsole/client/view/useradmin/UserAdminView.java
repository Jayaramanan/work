/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.CardLayout;
import java.util.ArrayList;
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
import com.ni3.ag.adminconsole.client.view.useradmin.charts.ChartPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.privileges.PrivilegesPanel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserAdminView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	public static final String EMPTY = "Empty";
	public static final String ALL_USERS = "All users";

	private JSplitPane splitApplicationSetup;
	private JPanel rightPanel;
	private UserAdminLeftPanel leftPanel;

	private CardLayout cardLayout;
	private UserPanel userPanel;
	private PrivilegesPanel privilegesPanel;
	private ScopePanel scopePanel;
	private ThickClientPanel thickClientPanel;
	private ChartPanel chartPanel;

	private ErrorPanel errorPanel;

	private UserAdminView(){
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

		leftPanel = new UserAdminLeftPanel();
		splitApplicationSetup.setLeftComponent(leftPanel);

		rightPanel = new JPanel();
		splitApplicationSetup.setRightComponent(rightPanel);

		splitApplicationSetup.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		cardLayout = new CardLayout();
		rightPanel.setLayout(cardLayout);

		userPanel = new UserPanel();
		privilegesPanel = new PrivilegesPanel();
		scopePanel = new ScopePanel();
		thickClientPanel = new ThickClientPanel();
		chartPanel = new ChartPanel();

		rightPanel.add(new JPanel(), EMPTY);
		rightPanel.add(userPanel, Translation.get(TextID.GroupMembers));
		rightPanel.add(privilegesPanel, Translation.get(TextID.GroupPrivileges));
		rightPanel.add(scopePanel, Translation.get(TextID.GroupScope));
		rightPanel.add(chartPanel, Translation.get(TextID.Charts));
		rightPanel.add(thickClientPanel, Translation.get(TextID.OfflineClient));

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

	public UserAdminLeftPanel getLeftPanel(){
		return leftPanel;
	}

	public UserPanel getUserPanel(){
		return userPanel;
	}

	public PrivilegesPanel getPrivilegesPanel(){
		return privilegesPanel;
	}

	public ThickClientPanel getThickClientPanel(){
		return thickClientPanel;
	}

	public ChartPanel getChartPanel(){
		return chartPanel;
	}

	public void showCurrentPanel(String name){
		cardLayout.show(rightPanel, name);
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	@Override
	public void resetEditedFields(){
		resetChanges(userPanel.getChangeResetableComponents());
		resetChanges(scopePanel.getChangeResetableComponents());
		resetChanges(thickClientPanel.getChangeResetableComponents());
		resetChanges(chartPanel.getChangeResetableComponents());
		resetChanges(privilegesPanel.getChangeResettableComponents());
	}

	private void resetChanges(ChangeResetable[] changedComps){
		for (ChangeResetable cr : changedComps)
			cr.resetChanges();
	}

	public ScopePanel getScopePanel(){
		return scopePanel;
	}

	@Override
	public boolean isChanged(){
		ChangeResetable[] resetableComponents = null;
		if (userPanel.isVisible()){
			userPanel.stopCellEditing();
			resetableComponents = userPanel.getChangeResetableComponents();
		} else if (scopePanel.isVisible()){
			resetableComponents = scopePanel.getChangeResetableComponents();
		} else if (thickClientPanel.isVisible()){
			thickClientPanel.stopCellEditing();
			resetableComponents = thickClientPanel.getChangeResetableComponents();
		} else if (chartPanel.isVisible()){
			chartPanel.stopCellEditing();
			resetableComponents = chartPanel.getChangeResetableComponents();
		} else if (privilegesPanel.isVisible()){
			resetableComponents = privilegesPanel.getChangeResettableComponents();
		}

		if (resetableComponents != null){
			for (ChangeResetable cr : resetableComponents){
				if (cr.isChanged()){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Group.class, String.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getLeftPanel().getTreeModel());
			getLeftPanel().setSelectionTreePath(found);
		}
	}

}
