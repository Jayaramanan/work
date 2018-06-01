/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserAdminTreeModelTest extends ACTestCase{
	public void testConstructor(){
		SessionData.getInstance().setDbName("mydb");
		ArrayList<Group> groups = generateGroupsList();
		UserAdminTreeModel model = new UserAdminTreeModel(groups);
		assertEquals(model.getRoot(), SessionData.getInstance().getDbName());
	}

	public void testGetChild(){
		SessionData.getInstance().setDbName("mydb");
		ArrayList<Group> groups = generateGroupsList();
		UserAdminTreeModel model = new UserAdminTreeModel(groups);
		assertEquals(10, model.getChildCount(SessionData.getInstance().getDbName()));
		assertEquals(0, model.getChildCount(new Object()));
		for (int i = 0; i < 10; i++){
			
			assertEquals(groups.get(i), model.getChild(SessionData.getInstance().getDbName(), i));
			assertEquals(model.getIndexOfChild(SessionData.getInstance().getDbName(), groups.get(i)), i);
		}
		String[] ar = new String[] { Translation.get(TextID.GroupMembers), Translation.get(TextID.GroupPrivileges)};
		for (int i = 0; i < 2; i++){
			Object parent = new Object();
			Object child = model.generateNode(ar[i], parent);
			assertEquals(model.getChild(parent, i), child);
			assertEquals(model.getIndexOfChild(parent, child), i);
		}
	}
	
	public void testValuePathChanged(){
		SessionData.getInstance().setDbName("mydb");
		ArrayList<Group> groups = generateGroupsList();
		UserAdminTreeModel model = new UserAdminTreeModel(groups);
		TreePath tp = new TreePath(SessionData.getInstance().getDbName());
		for(int i = 0; i < 10; i++){
			Group currentgGroup = groups.get(i);
			TreePath current = tp.pathByAddingChild(currentgGroup);
			model.valueForPathChanged(current, "hello");
			assertEquals("hello", currentgGroup.getName());
			Group changed = (Group) model.getChild(SessionData.getInstance().getDbName(), i);
			assertEquals(changed.getName(), currentgGroup.getName());
		}
		
	}

	private ArrayList<Group> generateGroupsList(){
		ArrayList<Group> ar = new ArrayList<Group>();
		for (int id = 1; id <= 10; id++){
			Group g = new Group();
			g.setId(id);
			ar.add(g);
		}
		return ar;
	}

	/*
	 * /** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.view.useradmin;
	 * 
	 * import java.util.Arrays;
	 * 
	 * public class UserAdminTreeMode
	 * l extends AbstractTreeModel{
	 * 
	 * private List<String> leafs = Arrays.asList(new String[] { UserAdminView.USERS, UserAdminView.OBJECTS,
	 * UserAdminView.CONNECTIONS });
	 * 
	 * public Object getChild(Object node, int i){ if (rootNode.equals(node)){ return groupList.get(i); } else{ return
	 * leafs.get(i); } }
	 * 
	 * public int getChildCount(Object node){ if (rootNode.equals(node)){ return groupList.size(); } else{ return
	 * leafs.size(); } }
	 * 
	 * public int getIndexOfChild(Object parent, Object child){ if (rootNode.equals(parent)){ return
	 * groupList.indexOf(child); } else{ return leafs.indexOf(child); } }
	 * 
	 * public boolean isLeaf(Object node){ if (rootNode.equals(node)){ return (groupList.isEmpty()) ? true : false; }
	 * else{ return !(node instanceof Group); } }
	 * 
	 * public void valueForPathChanged(TreePath path, Object newValue){ log.debug("pathForValueChanged, newValue = " +
	 * newValue); Object lastPathComponent = path.getLastPathComponent(); log.debug("lastPathComponent= " +
	 * lastPathComponent); ((Group) lastPathComponent).setName((String) newValue); }
	 * 
	 * }
	 */
}
