/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.shared.language.TextID.CanCreate;
import static com.ni3.ag.adminconsole.shared.language.TextID.CanDelete;
import static com.ni3.ag.adminconsole.shared.language.TextID.CanRead;
import static com.ni3.ag.adminconsole.shared.language.TextID.Object;

import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.useradmin.GroupPrivilegesUpdater;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class GroupPrivilegesTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;
	private static final int OBJECT_INDEX = 0;
	public static final int CAN_READ_INDEX = 1;
	public static final int CAN_CREATE_INDEX = 2;
	public static final int CAN_DELETE_INDEX = 3;
	public static final int CAN_UPDATE_INDEX = 4;
	public static final int CAN_UPDATE_LOCKED_INDEX = 5;

	private JTree tree;
	private Group group;
	private GroupPrivilegesUpdater privilegesUpdater;
	private TreeModelListener treeModelListener;

	public GroupPrivilegesTableModel(JTree tree, Group group, List<Schema> schemas, boolean showLockedColumns){
		this.tree = tree;
		this.group = group;
		this.privilegesUpdater = new GroupPrivilegesUpdater(schemas);
		this.treeModelListener = new PrivilegesTreeModelListener();
		this.tree.getModel().addTreeModelListener(treeModelListener);
		addColumn(get(Object), false, TreeModel.class, false);
		addColumn(get(CanRead), false, Boolean.class, false);
		addColumn(get(CanCreate), false, Boolean.class, false);
		addColumn(get(CanDelete), false, Boolean.class, false);
		addColumn(get(TextID.EditingOptions), false, EditingOption.class, false);
		if (showLockedColumns){
			addColumn(get(TextID.EditingOptionsLocked), false, EditingOption.class, false);
		}

		tree.addTreeExpansionListener(new TreeExpansionListener(){
			public void treeExpanded(TreeExpansionEvent event){
				fireTableDataChanged();
			}

			public void treeCollapsed(TreeExpansionEvent event){
				fireTableDataChanged();
			}
		});
	}

	@Override
	public int getRowCount(){
		return tree.getRowCount();
	}

	protected Object nodeForRow(int row){
		TreePath treePath = tree.getPathForRow(row);
		Object node = treePath.getLastPathComponent();
		Object obj = privilegesUpdater.convertSelectedObjectToModel(node);
		return obj;
	}

	@Override
	public Object getValueAt(int row, int column){
		Object node = nodeForRow(row);
		switch (column){
			case OBJECT_INDEX:
				return node;
			case CAN_READ_INDEX:
				return isCanRead(node);
			case CAN_CREATE_INDEX:
				return isCanCreate(node);
			case CAN_DELETE_INDEX:
				return isCanDelete(node);
			case CAN_UPDATE_INDEX:
				return getCanUpdate(node);
			case CAN_UPDATE_LOCKED_INDEX:
				return getCanUpdateLocked(node);
		}

		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column){
        super.setValueAt(aValue, row, column);
		Object node = nodeForRow(row);
		switch (column){
			case OBJECT_INDEX:
				break;
			case CAN_READ_INDEX:
				setCanRead(node, aValue);
				fireTableRowsUpdated(row, getRowCount());
				break;
			case CAN_CREATE_INDEX:
				setCanCreate(node, aValue);
				break;
			case CAN_DELETE_INDEX:
				setCanDelete(node, aValue);
				break;
			case CAN_UPDATE_INDEX:
				setCanUpdate(node, aValue);
				if (node instanceof ObjectDefinition){
					fireTableRowsUpdated(row, getRowCount());
				}
				break;
			case CAN_UPDATE_LOCKED_INDEX:
				setCanUpdateLocked(node, aValue);
				break;
			default:
				break;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column){
		return isCellEditable(nodeForRow(row), column);
	}

	boolean isCellEditable(Object node, int column){
		boolean editable = false;
		if (column == OBJECT_INDEX){
			editable = true;
		} else if (node instanceof Schema){
			editable = (column == CAN_READ_INDEX);
		} else if (node instanceof ObjectDefinition){
			ObjectDefinition object = (ObjectDefinition) node;
			switch (column){
				case CAN_READ_INDEX:
					editable = isCanReadSchema(object.getSchema());
					break;
				case CAN_UPDATE_INDEX:
				case CAN_CREATE_INDEX:
				case CAN_DELETE_INDEX:
					editable = isCanReadObject(object);
					break;
				default:
					break;
			}
		} else if (node instanceof ObjectAttribute){
			ObjectAttribute oa = (ObjectAttribute) node;
			ObjectDefinition object = oa.getObjectDefinition();
			switch (column){
				case CAN_READ_INDEX:
					editable = isCanReadObject(object);
					break;
				case CAN_UPDATE_INDEX:
					editable = (isCanReadObject(object) && isCanUpdateObject(object) && isCanReadAttribute(oa));
					break;
				case CAN_UPDATE_LOCKED_INDEX:
					editable = (isCanReadObject(object) && isCanUpdateObject(object) && isCanReadAttribute(oa));
					break;
				default:
					break;
			}
		} else if (node instanceof PredefinedAttribute){
			PredefinedAttribute pa = (PredefinedAttribute) node;
			editable = column == CAN_READ_INDEX && isCanReadAttribute(pa.getObjectAttribute());
		}
		return editable;
	}

	boolean isCanRead(Object node){
		if (node instanceof Schema){
			return isCanReadSchema((Schema) node);
		} else if (node instanceof ObjectDefinition){
			return isCanReadObject((ObjectDefinition) node);
		} else if (node instanceof ObjectAttribute){
			return isCanReadAttribute((ObjectAttribute) node);
		} else if (node instanceof PredefinedAttribute){
			return isCanReadPredefined((PredefinedAttribute) node);
		}
		return false;
	}

	boolean isCanReadObject(ObjectDefinition od){
		for (ObjectGroup oug : od.getObjectGroups()){
			if (oug.getGroup().equals(group)){
				return oug.isCanRead();
			}
		}
		return false;
	}

	boolean isCanReadSchema(Schema sch){
		for (SchemaGroup oug : sch.getSchemaGroups()){
			if (oug.getGroup().equals(group)){
				return oug.isCanRead();
			}
		}
		return false;
	}

	boolean isCanReadAttribute(ObjectAttribute oa){
		for (AttributeGroup ag : oa.getAttributeGroups()){
			if (ag.getGroup().equals(group)){
				return ag.isCanRead();
			}
		}
		return false;
	}

	boolean isCanReadPredefined(PredefinedAttribute node){
		for (GroupPrefilter pag : node.getPredefAttributeGroups()){
			if (pag.getGroup().equals(group)){
				return false;
			}
		}
		return true;
	}

	Object getCanUpdate(Object node){
		if (node instanceof ObjectDefinition){
			return isCanUpdateObject((ObjectDefinition) node);
		} else if (node instanceof ObjectAttribute){
			return getCanUpdateAttribute((ObjectAttribute) node);
		}
		return null;
	}

	boolean isCanUpdateObject(ObjectDefinition od){
		for (ObjectGroup oug : od.getObjectGroups()){
			if (oug.getGroup().equals(group)){
				return oug.isCanUpdate();
			}
		}
		return false;
	}

	EditingOption getCanUpdateAttribute(ObjectAttribute oa){
		EditingOption result = EditingOption.NotVisible;
		for (AttributeGroup ag : oa.getAttributeGroups()){
			if (ag.getGroup().equals(group)){
				if (ag.getEditingOption() != null){
					result = ag.getEditingOption();
				}
				break;
			}
		}
		return result;
	}

	boolean isCanCreate(Object node){
		if (node instanceof ObjectDefinition){
			ObjectDefinition od = (ObjectDefinition) node;
			for (ObjectGroup oug : od.getObjectGroups()){
				if (oug.getGroup().equals(group)){
					return oug.isCanCreate();
				}
			}
		}
		return false;
	}

	boolean isCanDelete(Object node){
		if (node instanceof ObjectDefinition){
			ObjectDefinition od = (ObjectDefinition) node;
			for (ObjectGroup oug : od.getObjectGroups()){
				if (oug.getGroup().equals(group)){
					return oug.isCanDelete();
				}
			}
		}
		return false;
	}

	void setCanRead(Object node, Object aValue){
		boolean value = (Boolean) aValue;
		if (node instanceof Schema){
			privilegesUpdater.setCanReadSchema((Schema) node, group, value, false);
		} else if (node instanceof ObjectDefinition){
			privilegesUpdater.setCanReadObject((ObjectDefinition) node, group, value, false);
		} else if (node instanceof ObjectAttribute){
			privilegesUpdater.setCanReadAttribute((ObjectAttribute) node, group, value, false, true);
		} else if (node instanceof PredefinedAttribute){
			privilegesUpdater.setCanReadPredefined((PredefinedAttribute) node, group, value);
		}
	}

	void setCanUpdate(Object node, Object aValue){
		if (node instanceof ObjectDefinition){
			boolean value = (Boolean) aValue;
			setCanUpdateObject((ObjectDefinition) node, value);
		} else if (node instanceof ObjectAttribute){
			EditingOption value = (EditingOption) aValue;
			setCanUpdateAttribute((ObjectAttribute) node, value);
		}
	}

	void setCanUpdateObject(ObjectDefinition od, boolean value){
		for (ObjectGroup oug : od.getObjectGroups()){
			if (oug.getGroup().equals(group)){
				oug.setCanUpdate(value);
				break;
			}
		}
		if (!value){
			for (ObjectAttribute oa : od.getObjectAttributes()){
				setCanUpdateAttribute(oa, EditingOption.NotVisible);
				setCanUpdateLocked(oa, EditingOption.NotVisible);
			}
		}
	}

	void setCanUpdateAttribute(ObjectAttribute oa, EditingOption value){
		for (AttributeGroup ag : oa.getAttributeGroups()){
			if (ag.getGroup().equals(group)){
				ag.setEditingOption(value);
				break;
			}
		}
	}

	void setCanCreate(Object node, Object aValue){
		boolean value = (Boolean) aValue;
		if (node instanceof ObjectDefinition){
			ObjectDefinition od = (ObjectDefinition) node;
			for (ObjectGroup oug : od.getObjectGroups()){
				if (oug.getGroup().equals(group)){
					oug.setCanCreate(value);
					break;
				}
			}
		}
	}

	void setCanDelete(Object node, Object aValue){
		boolean value = (Boolean) aValue;
		if (node instanceof ObjectDefinition){
			ObjectDefinition od = (ObjectDefinition) node;
			for (ObjectGroup oug : od.getObjectGroups()){
				if (oug.getGroup().equals(group)){
					oug.setCanDelete(value);
					break;
				}
			}
		}
	}

	EditingOption getCanUpdateLocked(Object node){
		EditingOption result = EditingOption.NotVisible;
		if (node instanceof ObjectAttribute){
			ObjectAttribute attr = (ObjectAttribute) node;
			for (AttributeGroup ag : attr.getAttributeGroups()){
				if (ag.getGroup().equals(group)){
					if (ag.getEditingOptionLocked() != null){
						result = ag.getEditingOptionLocked();
					}
					break;
				}
			}
		}
		return result;
	}

	void setCanUpdateLocked(Object node, Object aValue){
		EditingOption value = (EditingOption) aValue;
		if (node instanceof ObjectAttribute){
			ObjectAttribute attr = (ObjectAttribute) node;
			for (AttributeGroup ag : attr.getAttributeGroups()){
				if (ag.getGroup().equals(group)){
					ag.setEditingOptionLocked(value);
					break;
				}
			}
		}
	}

	public void setData(JTree tree, Group group, List<Schema> list){
		this.tree = tree;
		this.group = group;
		this.privilegesUpdater.setSchemas(list);
	}

	public void updateTreePath(TreePath path, Object obj){
		int row = tree.getRowForPath(path);

		if (obj instanceof Schema){
			List<SchemaGroup> schemaGroups = ((Schema) obj).getSchemaGroups();
			for (SchemaGroup sg : schemaGroups){
				if (group != null && group.equals(sg.getGroup())){
					setValueAt(sg.isCanRead(), row, 1);
					break;
				}
			}
		} else if (obj instanceof ObjectDefinition){
			List<ObjectGroup> objectGroups = ((ObjectDefinition) obj).getObjectGroups();
			for (ObjectGroup og : objectGroups){
				if (group != null && group.equals(og.getGroup())){
					setValueAt(og.isCanRead(), row, 1);
					setValueAt(og.isCanUpdate(), row, 2);
					setValueAt(og.isCanCreate(), row, 3);
					setValueAt(og.isCanDelete(), row, 4);
					break;
				}
			}
		} else if (obj instanceof ObjectAttribute){
			List<AttributeGroup> attrGroups = ((ObjectAttribute) obj).getAttributeGroups();
			for (AttributeGroup ag : attrGroups){
				if (group != null && group.equals(ag.getGroup())){
					setValueAt(ag.isCanRead(), row, 1);
					setValueAt(ag.getEditingOption(), row, 4);
					if (getColumnCount() > 5){
						setValueAt(ag.getEditingOptionLocked(), row, 5);
					}
					break;
				}
			}
		} else if (obj instanceof PredefinedAttribute){
			List<GroupPrefilter> predefGroups = ((PredefinedAttribute) obj).getPredefAttributeGroups();
			boolean groupPrefilterExists = false;
			for (GroupPrefilter gp : predefGroups){
				if (group != null && group.equals(gp.getGroup())){
					groupPrefilterExists = true;
					break;
				}
			}

			setValueAt(!groupPrefilterExists, row, 4);
		}
	}

	private class PrivilegesTreeModelListener implements TreeModelListener{
		@Override
		public void treeStructureChanged(TreeModelEvent e){
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e){
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e){
		}

		@Override
		public void treeNodesChanged(TreeModelEvent e){
			int rowCount = getRowCount();
			for (int i = 0; i < rowCount; i++){
				TreePath tp = GroupPrivilegesTableModel.this.tree.getPathForRow(i);
				Object node = tp.getLastPathComponent();
				Object obj = privilegesUpdater.convertSelectedObjectToModel(node);
				updateTreePath(tp, obj);
			}
		}
	}

	public void setTreeModelListener(){
		this.tree.getModel().removeTreeModelListener(treeModelListener);
		this.tree.getModel().addTreeModelListener(treeModelListener);
	}

	/**
	 * @param modelRow
	 */
	public void getSelected(int modelRow){
		// TODO Auto-generated method stub

	}
}
