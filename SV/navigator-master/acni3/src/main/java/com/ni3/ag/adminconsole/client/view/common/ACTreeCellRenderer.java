/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ACTreeCellRenderer extends DefaultTreeCellRenderer{

	private static final long serialVersionUID = 1L;

	private ImageIcon rootIcon;
	private ImageIcon openedIcon;
	private ImageIcon disabledIcon;
	private ImageIcon inactiveIcon;
	private ImageIcon nodeODIcon;
	private ImageIcon edgeODIcon;
	private ImageIcon contextEdgeODIcon;
	private ImageIcon schemaIcon;
	private ImageIcon chartIcon;
	private ImageIcon attributeIcon;
	private ImageIcon groupIcon;
	private ImageIcon languageIcon;
	private ImageIcon userIcon;
	private ImageIcon reportIcon;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
	        int row, boolean hasFocus){
		ImageIcon currentIcon = null;

		if (value instanceof ACRootNode){
			currentIcon = getRootIcon();
		} else if (value instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) value;
			if (!db.isConnected()){
				currentIcon = getNotConnectedIcon();
			} else{
				currentIcon = (leaf ? getInactiveIcon() : getOpenedIcon());
			}
		} else if (value instanceof ObjectDefinition){
			ObjectDefinition od = (ObjectDefinition) value;
			if (od != null && od.getObjectType() != null){
				switch (od.getObjectType()){
					case CONTEXT_EDGE:
						currentIcon = getContextEdgeObjectDefinitionIcon();
						break;
					case EDGE:
						currentIcon = getEdgeObjectDefinitionIcon();
						break;
					case NODE:
						currentIcon = getNodeObjectDefinitionIcon();
						break;
				}
			}
			value = ((ObjectDefinition) value).getName();
		} else if (value instanceof Schema){
			currentIcon = getSchemaIcon();
			value = ((Schema) value).getName();
		} else if (value instanceof ObjectAttribute){
			currentIcon = getAttributeIcon();
			value = ((ObjectAttribute) value).getLabel();
		} else if (value instanceof PredefinedAttribute){
			value = ((PredefinedAttribute) value).getLabel();
		} else if (value instanceof Chart){
			currentIcon = getChartIcon();
			value = ((Chart) value).getName();
		} else if (value instanceof Group){
			currentIcon = getGroupIcon();
			value = ((Group) value).getName();
		} else if (value instanceof User){
			currentIcon = getUserIcon();
			value = ((User) value).getUserName();
		} else if (value instanceof Language){
			currentIcon = getLanguageIcon();
			value = ((Language) value).getLanguage();
		} else if (value instanceof ReportTemplate){
			currentIcon = getReportIcon();
			value = ((ReportTemplate) value).getName();
		}

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (currentIcon != null){
			setIcon(currentIcon);
		}
		return this;
	}

	private ImageIcon getRootIcon(){
		if (rootIcon == null){
			rootIcon = new ImageIcon(ACMain.class.getResource("/images/Ni3_20x16.png"));
		}
		return rootIcon;
	}

	private ImageIcon getOpenedIcon(){
		if (openedIcon == null){
			openedIcon = new ImageIcon(ACMain.class.getResource("/images/Database_opened_12.png"));
		}
		return openedIcon;
	}

	private ImageIcon getInactiveIcon(){
		if (inactiveIcon == null){
			inactiveIcon = new ImageIcon(ACMain.class.getResource("/images/Database_inactive_12.png"));
		}
		return inactiveIcon;
	}

	private ImageIcon getNotConnectedIcon(){
		if (disabledIcon == null){
			disabledIcon = new ImageIcon(ACMain.class.getResource("/images/Database_disabled_12.png"));
		}
		return disabledIcon;
	}

	private ImageIcon getNodeObjectDefinitionIcon(){
		if (nodeODIcon == null){
			nodeODIcon = new ImageIcon(ACMain.class.getResource("/images/Node16.png"));
		}
		return nodeODIcon;
	}

	private ImageIcon getEdgeObjectDefinitionIcon(){
		if (edgeODIcon == null){
			edgeODIcon = new ImageIcon(ACMain.class.getResource("/images/Edge16.png"));
		}
		return edgeODIcon;
	}

	private ImageIcon getContextEdgeObjectDefinitionIcon(){
		if (contextEdgeODIcon == null){
			contextEdgeODIcon = new ImageIcon(ACMain.class.getResource("/images/Context_edge16.png"));
		}
		return contextEdgeODIcon;
	}

	private ImageIcon getSchemaIcon(){
		if (schemaIcon == null){
			schemaIcon = new ImageIcon(ACMain.class.getResource("/images/Schema16.png"));
		}
		return schemaIcon;
	}

	private ImageIcon getChartIcon(){
		if (chartIcon == null){
			chartIcon = new ImageIcon(ACMain.class.getResource("/images/Chart16.png"));
		}
		return chartIcon;
	}

	private ImageIcon getAttributeIcon(){
		if (attributeIcon == null){
			attributeIcon = new ImageIcon(ACMain.class.getResource("/images/Attribute16.png"));
		}
		return attributeIcon;
	}

	private ImageIcon getGroupIcon(){
		if (groupIcon == null)
			groupIcon = new ImageIcon(ACMain.class.getResource("/images/Group16.png"));
		return groupIcon;
	}

	protected ImageIcon getUserIcon(){
		if (userIcon == null)
			userIcon = new ImageIcon(ACMain.class.getResource("/images/User16.png"));
		return userIcon;
	}

	private ImageIcon getLanguageIcon(){
		if (languageIcon == null)
			languageIcon = new ImageIcon(ACMain.class.getResource("/images/Language16.png"));
		return languageIcon;
	}

	private ImageIcon getReportIcon(){
		if (reportIcon == null)
			reportIcon = new ImageIcon(ACMain.class.getResource("/images/Report16.png"));
		return reportIcon;
	}
}
