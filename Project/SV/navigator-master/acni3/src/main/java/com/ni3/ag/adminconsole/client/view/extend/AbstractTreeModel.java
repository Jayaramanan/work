/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.extend;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;


public abstract class AbstractTreeModel extends TreeModelSupport implements TreeModel{

	/**
	 * Searches for an equal Object within this tree
	 * 
	 * @param search
	 *            the Object to find
	 * @return a TreePath to the equal Object or TreePath to the root if not found
	 */
	public TreePath findPathForEqualObject(Object search){
		return findPathForEqualObject(search, this);
	}

}
