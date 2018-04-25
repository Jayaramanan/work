/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public class ReportColumnSelectionTreeModel extends AbstractTreeModel{
	protected Vector<TreeModelListener> vector = new Vector<TreeModelListener>();
	private Map<TreeEntity, List<TreeAttribute>> attributeMap;
	private List<TreeEntity> entities;
	private final RootNode rootNode;

	public ReportColumnSelectionTreeModel(Map<TreeEntity, List<TreeAttribute>> attributeMap, List<TreeEntity> entities){
		super();
		this.attributeMap = attributeMap;
		this.entities = entities;
		rootNode = new RootNode("");
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return entities.get(index);
		} else if (parent instanceof TreeEntity){
			List<TreeAttribute> attributes = attributeMap.get((TreeEntity) parent);
			return attributes.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return entities.size();
		} else if (parent instanceof TreeEntity){
			List<TreeAttribute> attributes = attributeMap.get((TreeEntity) parent);
			return attributes != null ? attributes.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return entities.indexOf(child);
		} else if (parent instanceof TreeEntity){
			List<TreeAttribute> attributes = attributeMap.get((TreeEntity) parent);
			return attributes.indexOf(child);
		}
		return 0;
	}

	@Override
	public Object getRoot(){
		return rootNode;
	}

	@Override
	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return entities == null || entities.isEmpty();
		} else if (node instanceof TreeEntity){
			List<TreeAttribute> attributes = attributeMap.get((TreeEntity) node);
			return attributes == null || attributes.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
		Object pathComp = path.getLastPathComponent();
		if (pathComp instanceof TreeEntity){
			TreeEntity entity = (TreeEntity) path.getLastPathComponent();
			entity.setSelected((Boolean) newValue);
			List<TreeAttribute> children = attributeMap.get(entity);
			if (children != null && !children.isEmpty()){
				for (TreeAttribute attr : children)
					attr.setSelected((Boolean) newValue);
			}
		} else if (pathComp instanceof TreeAttribute){
			TreeAttribute attr = (TreeAttribute) path.getLastPathComponent();
			attr.setSelected((Boolean) newValue);
			TreePath parent = path.getParentPath();
			checkParentState(parent);
		}
		fireTreeNodesChanged(new TreeModelEvent(this, path));
	}

	private void checkParentState(TreePath parent){
		if (!(parent.getLastPathComponent() instanceof TreeEntity)){
			return;
		}
		TreeEntity category = (TreeEntity) parent.getLastPathComponent();
		boolean shouldSelect = !areNoneChildrenSelected(category);
		if (shouldSelect != category.isSelected()){
			category.setSelected(shouldSelect);
			fireTreeNodesChanged(new TreeModelEvent(this, parent));
		}
	}

	public boolean areAllChildrenSelected(Object parent){
		boolean selected = true;
		if (parent instanceof TreeEntity){
			List<TreeAttribute> attributes = attributeMap.get((TreeEntity) parent);
			if (attributes != null)
				for (TreeAttribute attr : attributes)
					if (!attr.isSelected()){
						selected = false;
						break;
					}
		}
		return selected;
	}

	public boolean areNoneChildrenSelected(Object parent){
		boolean noneSelected = true;
		if (parent instanceof TreeEntity){
			List<TreeAttribute> attributes = attributeMap.get((TreeEntity) parent);
			if (attributes != null)
				for (TreeAttribute attr : attributes)
					if (attr.isSelected()){
						noneSelected = false;
						break;
					}
		}
		return noneSelected;
	}
}
