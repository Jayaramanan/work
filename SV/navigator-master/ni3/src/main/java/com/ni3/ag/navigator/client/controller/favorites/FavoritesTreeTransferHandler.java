package com.ni3.ag.navigator.client.controller.favorites;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.model.FavoritesModel;
import com.ni3.ag.navigator.client.model.Ni3Document;
import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.gui.favorites.FavoritesTree;
import com.ni3.ag.navigator.client.gui.favorites.FavoritesTreeModel;

public class FavoritesTreeTransferHandler extends TransferHandler{
	private static final long serialVersionUID = 6596727316234462934L;
	private static final Logger log = Logger.getLogger(FavoritesTreeTransferHandler.class);
	private Ni3Document doc;
	private FavoritesTree tree;

	public FavoritesTreeTransferHandler(Ni3Document doc, FavoritesTree tree){
		this.doc = doc;
		this.tree = tree;
	}

	public int getSourceActions(JComponent c){
		return COPY | MOVE;
	}

	protected Transferable createTransferable(JComponent c){
		TreePath path = tree.getSelectionPath();
		if (path != null){
			Object obj = path.getLastPathComponent();

			if (obj instanceof Folder)
				return (com.ni3.ag.navigator.client.domain.Folder) obj;

			if (obj instanceof Favorite)
				return (com.ni3.ag.navigator.client.domain.Favorite) obj;
		}

		return null;
	}

	public boolean canImport(TransferHandler.TransferSupport support){
		int action = support.getDropAction();
		try{
			JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
			TreePath path = dropLocation.getPath();
			if (path == null || path.getPathCount() == 1)
				return false;

			Object dropTargetObject = path.getLastPathComponent();
			if (!(dropTargetObject instanceof Folder))
				return false;

			Folder dropTarget = (Folder) dropTargetObject;

			Object group = path.getPathComponent(1);

			if (!(group instanceof Folder))
				return false;

			Folder parent = (Folder) group;

			if (support.getTransferable().isDataFlavorSupported(new DataFlavor("ni3/folder"))){
				Object transfer = (support.getTransferable().getTransferData(new DataFlavor("ni3/folder")));
				Folder dropableFolder = (Folder) transfer;
				if (dropableFolder.getId() < 0)
					return false;
				if (action == COPY)
					// return true;//not implemented yet
					return false;
				if (dropTarget.getId() == dropableFolder.getId())
					return false;
				if (doc.getFavoritesModel().isFolderChildOfFolder(dropTarget, dropableFolder))
					return false;
				return (((Folder) transfer).isGroupFolder() == parent.isGroupFolder());
			}

			if (support.getTransferable().isDataFlavorSupported(new DataFlavor("ni3/favorite"))){
				Object transfer = (support.getTransferable().getTransferData(new DataFlavor("ni3/favorite")));
				Favorite dropableFavorite = (Favorite) transfer;

				if (action == COPY)
					return true;

				boolean result = (dropableFavorite.isGroupFavorite() == parent.isGroupFolder())
						|| (!dropableFavorite.isGroupFavorite() && parent.isGroupFolder());
				return result;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support){
		if (!canImport(support))
			return false;

		JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
		TreePath path = dropLocation.getPath();
		int action = support.getDropAction();

		Object targetFolderNode = path.getLastPathComponent();

		if (!(targetFolderNode instanceof Folder))
			return false;

		TreePath fromPath = tree.getSelectionPath();

		Folder targetFolder = (Folder) targetFolderNode;
		final FavoritesModel favoritesModel = doc.getFavoritesModel();
		try{
			final FavoritesController favoritesController = new FavoritesController(doc);
			if (support.getTransferable().isDataFlavorSupported(new DataFlavor("ni3/folder"))){
				Folder droppableFolder = (Folder) (support.getTransferable().getTransferData(new DataFlavor("ni3/folder")));

				if (action == MOVE){
					int newIndex = dropLocation.getChildIndex();
					if (newIndex == -1)
						newIndex = tree.getModel().getChildCount(path.getLastPathComponent());

					int currentIndex = favoritesModel.getCurrentIndexOfFolder(droppableFolder);
					boolean newParent = targetFolder.getId() != droppableFolder.getParentFolderID();
					// don't allow move folder to the same place as it is
					// now placed
					if (!newParent && (newIndex == (currentIndex + 1) || (newIndex == currentIndex)))
						return false;

					if (!favoritesModel.isUniqueFavoriteFolderName(droppableFolder.getName(), droppableFolder.getId(),
							targetFolder.getId())){
						JOptionPane.showMessageDialog(tree, UserSettings.getWord("MsgDuplicateFavoriteName",
								new Object[] { droppableFolder.getName() }));
						return false;
					}

					boolean isDown = newIndex > currentIndex && newIndex > 0;
					if (newParent){
						isDown = false;
					}

					if (isDown){ // if dragging down
						newIndex--;
					}
					favoritesController.moveFolderToFolder(droppableFolder, targetFolder, newIndex, isDown);
					refreshTreeNode(fromPath.getPath(), true);
					refreshTreeNode(path.getPath(), false);
				}
			} else if (support.getTransferable().isDataFlavorSupported(new DataFlavor("ni3/favorite"))){
				Favorite fvt = (Favorite) (support.getTransferable().getTransferData(new DataFlavor("ni3/favorite")));

				if (targetFolder == fvt.getFolder() && action == MOVE)
					return false;

				if (action == MOVE){
					if (!favoritesModel.isUniqueFavoriteName(fvt.getName(), fvt.getId(), targetFolder.getId())){
						JOptionPane.showMessageDialog(tree, UserSettings.getWord("MsgDuplicateFavoriteName",
								new Object[]{fvt.getName()}));
						return false;
					}
					favoritesController.moveFavoriteToFolder(fvt, targetFolder);
					refreshTreeNode(fromPath.getPath(), true);
					refreshTreeNode(path.getPath(), false);
				} else if (action == COPY){
					favoritesController.duplicate(fvt, targetFolder);
					refreshTreeNode(path.getPath(), false);
				} else
					// oops
					return false;

			}
		} catch (UnsupportedFlavorException e1){
			log.error(e1.getMessage());
		} catch (IOException e1){
			log.error(e1.getMessage());
		} catch (ClassNotFoundException e1){
			log.error(e1.getMessage());
		}

		return true;
	}

	private void refreshTreeNode(Object[] path, boolean refreshParent){
		final FavoritesTreeModel model = (FavoritesTreeModel) tree.getModel();
		if (refreshParent){
			path = Arrays.copyOf(path, path.length - 1);
		}
		model.fireTreeStructureChanged(new TreeModelEvent(tree, path));
	}
}