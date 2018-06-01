package com.ni3.ag.navigator.client.gui.favorites;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.navigator.client.controller.favorites.FavoritesController;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.gui.reports.AbstractTreeModel;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class FavoritesTreeModel extends AbstractTreeModel{

	private static final long serialVersionUID = 1L;
	private Folder rootNode;
	private List<Favorite> favorites;
	private List<Folder> folders;
	private Ni3Document doc;

	public FavoritesTreeModel(Ni3Document doc, Folder rootNode, List<Folder> folders, List<Favorite> favorites){
		this.doc = doc;
		this.rootNode = rootNode;
		this.folders = folders;
		this.favorites = favorites;
	}

	@Override
	public Object getChild(Object parent, int index){
		Object child = null;
		if (parent instanceof Folder){
			List<Folder> childFolders = getChildFolders((Folder) parent);
			if (index < childFolders.size()){
				child = childFolders.get(index);
			} else{
				List<Favorite> childFavorites = getChildFavorites((Folder) parent);
				child = childFavorites.get(index - childFolders.size());
			}
		}
		return child;
	}

	@Override
	public int getChildCount(Object parent){
		int childCount = 0;
		if (parent instanceof Folder){
			List<Folder> childFolders = getChildFolders((Folder) parent);
			List<Favorite> childFavorites = getChildFavorites((Folder) parent);
			childCount = childFolders.size() + childFavorites.size();
		}
		return childCount;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		int index = -1;
		if (child == null){
			return index;
		}
		if (parent instanceof Folder){
			List<Folder> childFolders = getChildFolders((Folder) parent);
			if (child instanceof Folder){
				index = childFolders.indexOf(child);
			} else{
				List<Favorite> childFavorites = getChildFavorites((Folder) parent);
				index = childFolders.size() - 1 + childFavorites.indexOf(child);
			}
		}
		return index;
	}

	@Override
	public Object getRoot(){
		return rootNode;
	}

	@Override
	public boolean isLeaf(Object node){
		boolean leaf = true;
		if (node instanceof Folder){
			List<Folder> childFolders = getChildFolders((Folder) node);
			List<Favorite> childFavorites = getChildFavorites((Folder) node);
			leaf = childFolders.isEmpty() && childFavorites.isEmpty();
		}
		return leaf;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
		Object obj = path.getLastPathComponent();
		if (newValue instanceof String)
			newValue = ((String) newValue).trim();

		if (newValue != null && !"".equals(newValue)){
			if (obj instanceof Folder || obj instanceof Favorite){
				FavoritesController controller = new FavoritesController(doc);
				if (controller.rename(obj, (String) newValue)){
					fireTreeNodesChanged(new TreeModelEvent(this, path));
				}
			}
		}
	}

	private List<Folder> getChildFolders(Folder parent){
		List<Folder> result = new ArrayList<Folder>();
		for (Folder fld : folders){
			if (parent.getId() == fld.getParentFolderID()){
				result.add(fld);
			}
		}
		return result;
	}

	private List<Favorite> getChildFavorites(Folder parent){
		List<Favorite> result = new ArrayList<Favorite>();
		for (Favorite favorite : favorites){
			if (parent.getId() == favorite.getFolderId()){
				result.add(favorite);
			}
		}
		return result;
	}
}