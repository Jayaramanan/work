/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.extend;

import java.util.*;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;

public class TreeModelSupport{
	protected Vector<TreeModelListener> vector = new Vector<TreeModelListener>();

	public void addTreeModelListener(TreeModelListener listener){
		if (listener != null && !vector.contains(listener)){
			vector.addElement(listener);
		}
	}

	public void removeTreeModelListener(TreeModelListener listener){
		if (listener != null){
			vector.removeElement(listener);
		}
	}

	public void fireTreeNodesChanged(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeNodesChanged(e);
		}
	}

	public void fireTreeNodesInserted(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeNodesInserted(e);
		}
	}

	public void fireTreeNodesRemoved(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeNodesRemoved(e);
		}
	}

	public void fireTreeStructureChanged(TreeModelEvent e){
		Enumeration<TreeModelListener> listeners = vector.elements();
		while (listeners.hasMoreElements()){
			TreeModelListener listener = (TreeModelListener) listeners.nextElement();
			listener.treeStructureChanged(e);
		}
	}

	public TreePath[] getPaths(JTree tree){
		ACRootNode root = (ACRootNode) tree.getModel().getRoot();
		// Create array to hold the treepaths
		List<TreePath> list = new ArrayList<TreePath>();
		// Traverse tree from root adding treepaths for all nodes to list
		getPaths(tree, root, new TreePath(root), list);
		// Convert list to array
		return (TreePath[]) list.toArray(new TreePath[list.size()]);
	}

	public void getPaths(JTree tree, Object parent, TreePath parentPath, List<TreePath> list){
		AbstractTreeModel mdl = (AbstractTreeModel) tree.getModel();
		// Add node to list
		list.add(parentPath);
		// Create paths for all children
		int childCount = mdl.getChildCount(parent);
		for (int i = 0; i < childCount; i++){
			Object n = mdl.getChild(parent, i);
			TreePath path = parentPath.pathByAddingChild(n);
			getPaths(tree, n, path, list);
		}
	}

	/**
	 * Searches for an equal Object within the given tree
	 * 
	 * @param search
	 *            the Object to find
	 * @param model
	 *            the model to search for the Object
	 * @return a TreePath to the equal Object or TreePath to the root if not found
	 */
	public TreePath findPathForEqualObject(Object search, TreeModel model){
		Object root = model.getRoot();
		if (root.equals(search)){
			return new SearchContainer(root);
		}
		SearchContainer searchResult = findEqualPathAndObject(root, search, new SearchContainer(root), model);
		return searchResult;
	}

	/**
	 * Searches for an equal Object within the given (sub)tree
	 * 
	 * @param search
	 *            the Object to find
	 * @param model
	 *            the model to search for the Object
	 * @param root
	 *            the root of the subtree
	 * @return a TreePath to the equal Object or TreePath to the root if not found
	 */
	public TreePath findPathForEqualObject(Object root, Object search, TreeModel model){
		SearchContainer searchResult = findEqualPathAndObject(root, search, new SearchContainer(root), model);
		return searchResult;
	}

	/**
	 * Recursively searches for an equal Object within given subtree
	 * 
	 * @param root
	 *            the root of the search subtree
	 * @param search
	 *            the Object to find
	 * @param path
	 *            the previously generated TreePath (to this subtree)
	 * @return a TreePath to the equal Object or TreePath to the subtree if not found
	 */
	private SearchContainer findEqualPathAndObject(Object root, Object search, SearchContainer path, TreeModel model){
		int size = model.getChildCount(root);
		SearchContainer construct = path;

		for (int i = 0; construct.getObject() == null && i < size; i++){
			construct = new SearchContainer(path.getPath());
			Object od = model.getChild(root, i);
			construct = construct.pathByAddingChild(od);

			if (od.equals(search))
				construct.setObject(od);
			else
				construct = findEqualPathAndObject(od, search, construct, model);
		}
		return construct;
	}

	public TreePath findPathByNodes(Object[] nodes, TreeModel model){
		Object root = model.getRoot();
		if (nodes.length == 1 && root.equals(nodes[0])){
			return new SearchContainer(root);
		}
		TreePath searchResult = findPathByNodes(root, nodes, new SearchContainer(root), model, 1);

		if (!searchResult.getLastPathComponent().equals(nodes[nodes.length - 1])){
			Object[] cutNodes = new Object[nodes.length - 1];
			for (int i = 0; i < cutNodes.length; i++)
				cutNodes[i] = nodes[i];
			searchResult = findPathByNodes(cutNodes, model);
		}

		return searchResult;
	}

	private SearchContainer findPathByNodes(Object root, Object[] nodes, SearchContainer path, TreeModel model, int depth){
		int size = model.getChildCount(root);
		SearchContainer construct = path;

		for (int i = 0; construct.getObject() == null && i < size; i++){
			construct = new SearchContainer(path.getPath());
			Object od = model.getChild(root, i);
			construct = construct.pathByAddingChild(od);

			if (depth < nodes.length && (od.equals(nodes[depth]) || od.toString().equals(nodes[depth].toString()))){
				if (depth == nodes.length - 1){
					construct.setObject(od);
				} else{
					construct = findPathByNodes(od, nodes, construct, model, depth + 1);
				}
			}
		}
		return construct;
	}

	/**
	 * A helper class to store search results
	 */
	private class SearchContainer extends TreePath{
		private static final long serialVersionUID = 1L;
		private Object object = null;

		public SearchContainer(Object[] path){
			super(path);
		}

		public SearchContainer(Object root){
			super(root);
		}

		public SearchContainer(TreePath path, Object o){
			super(path, o);
		}

		public SearchContainer pathByAddingChild(Object od){
			return new SearchContainer(this, od);
		}

		public void setObject(Object obj){
			this.object = obj;
		}

		public Object getObject(){
			return object;
		}
	}
}
