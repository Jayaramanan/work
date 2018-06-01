/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.gui.search.CheckNode;
import com.ni3.ag.navigator.client.model.SystemGlobals;

public class Models{

	public static DefaultTreeModel getPredefinedTree(Attribute attr, CheckValueIntegrity check, DataFilter SYSGroupPrefilter){
		DefaultMutableTreeNode root;
		if (SystemGlobals.isMarathonTesting()){
			root = new DefaultMutableTreeNode(Attribute.nullValue);
		} else{
			root = new DefaultMutableTreeNode();
		}
		DefaultTreeModel model = new DefaultTreeModel(root);
		Map<Integer, DefaultMutableTreeNode> nodes = new HashMap<Integer, DefaultMutableTreeNode>();

		root.add(new DefaultMutableTreeNode(Attribute.nullValue));

		for (Value v : attr.getValuesToUse()){
			if ((check != null && !check.checkValue(attr.ent, attr, v)) || SYSGroupPrefilter.checkExclusion(v.getId()))
				continue;

			createValueItem(attr, root, nodes, v);
		}

		return model;
	}

	static void createParent(Attribute attr, DefaultMutableTreeNode rootNode, int ParentID,
	        Map<Integer, DefaultMutableTreeNode> nodes){
		for (Entity def : attr.ent.getSchema().definitions){
			for (Attribute a : def.getReadableAttributes()){
				if (a.getValuesToUse() != null){
					for (Value v : a.getValuesToUse()){
						if (v.getId() == ParentID){
							createValueItem(attr, rootNode, nodes, v);
						}
					}
				}
			}
		}
	}

	private static void createValueItem(Attribute attr, DefaultMutableTreeNode root,
	        Map<Integer, DefaultMutableTreeNode> nodes, Value v){
		DefaultMutableTreeNode parent;
		if (v.getParentId() == 0)
			parent = root;
		else{
			if (!nodes.containsKey(v.getParentId()))
				createParent(attr, root, v.getParentId(), nodes);
			parent = nodes.get(v.getParentId());
		}

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(v);
		nodes.put(v.getId(), node);
		parent.add(node);
	}

	public static DefaultTreeModel getPredefinedCheckTree(Attribute attr, CheckValueIntegrity check,
	        DataFilter SYSGroupPrefilter, boolean intitalSelection){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(root);
		DefaultMutableTreeNode node;

		Map<Integer, DefaultMutableTreeNode> nodes = new HashMap<Integer, DefaultMutableTreeNode>();

		if (attr.getValuesToUse() != null)
			for (Value v : attr.getValuesToUse()){
				if ((check != null && !check.checkValue(attr.ent, attr, v)) || SYSGroupPrefilter.checkExclusion(v.getId()))
					continue;

				if (v.getParentId() == 0){
					node = new CheckNode(v.getLabel(), v, attr.ent.isEdge(), intitalSelection);
					nodes.put(v.getId(), node);
					root.add(node);
				} else{
					if (nodes.get(v.getParentId()) == null)
						createCheckParent(attr, root, v.getParentId(), nodes, intitalSelection);
				}
			}

		return model;
	}

	static void createCheckChildren(Attribute attr, DefaultMutableTreeNode parentNode, int ParentID,
	        Map<Integer, DefaultMutableTreeNode> nodes, boolean intitalSelection){
		DefaultMutableTreeNode node;

		for (Entity def : attr.ent.getSchema().definitions){
			for (Attribute a : def.getReadableAttributes()){
				if (a.getValuesToUse() != null){
					for (Value v : a.getValuesToUse()){
						if (v.getParentId() == ParentID){
							node = new CheckNode(v.getLabel(), v, def.isEdge(), intitalSelection);

							nodes.put(v.getId(), node);
							parentNode.add(node);
							createCheckChildren(attr, node, v.getId(), nodes, intitalSelection);
						}
					}
				}
			}
		}
	}

	static void createCheckParent(Attribute attr, DefaultMutableTreeNode rootNode, int ParentID,
	        Map<Integer, DefaultMutableTreeNode> nodes, boolean intitalSelection){
		DefaultMutableTreeNode node;

		for (Entity def : attr.ent.getSchema().definitions){
			for (Attribute a : def.getReadableAttributes()){
				if (a.getValuesToUse() != null){
					for (Value v : a.getValuesToUse()){
						if (v.getId() == ParentID){
							if (v.getParentId() == 0){
								node = new CheckNode(v.getLabel(), v, def.isEdge(), intitalSelection);
								nodes.put(v.getId(), node);

								rootNode.add(node);
								createCheckChildren(attr, node, v.getId(), nodes, intitalSelection);
							} else
								createCheckParent(attr, rootNode, v.getParentId(), nodes, intitalSelection);
						}
					}
				}
			}
		}
	}

}
