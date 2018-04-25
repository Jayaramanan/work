package com.ni3.ag.navigator.server.services.impl;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.domain.FavoritesFolder;
import com.ni3.ag.navigator.server.services.FavoritesFolderService;
import com.ni3.ag.navigator.server.services.FavoritesService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;

import java.util.Collections;
import java.util.List;

import jxl.common.Logger;

public class FavoritesFolderServiceImpl implements FavoritesFolderService{
	private static final Logger log = Logger.getLogger(FavoritesFolderServiceImpl.class);
	private ThreadLocalStorage threadLocalStorage;
	private FavoritesFolderDAO favoritesFolderDAO;
	private FavoritesService favoritesService;
	private FavoriteDAO favoriteDAO;

	public void setThreadLocalStorage(ThreadLocalStorage threadLocalStorage){
		this.threadLocalStorage = threadLocalStorage;
	}

	public void setFavoritesFolderDAO(FavoritesFolderDAO favoritesFolderDAO){
		this.favoritesFolderDAO = favoritesFolderDAO;
	}

	public void setFavoritesService(FavoritesService favoritesService){
		this.favoritesService = favoritesService;
	}

	public void setFavoriteDAO(FavoriteDAO favoriteDAO){
		this.favoriteDAO = favoriteDAO;
	}

	@Override
	public List<FavoritesFolder> getAllFolders(int schemaId){
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		FavoritesFolderDAO folderDAO = NSpringFactory.getInstance().getFavoritesFolderDao();
		return folderDAO.getFolders(schemaId, storage.getCurrentUser().getId());
	}

	@Override
	public FavoritesFolder createFolder(FavoritesFolder folder){
		folder.setCreatorId(threadLocalStorage.getCurrentUser().getId());
		return favoritesFolderDAO.save(folder);
	}

	@Override
	public void deleteFolder(int folderId){
		final List<FavoritesFolder> folders = favoritesFolderDAO.getSubfolders(folderId);

		for (final FavoritesFolder f : folders){
			final List<Integer> favoriteIds = favoriteDAO.getFavoriteIdsByFolder(f.getId());
			for (Integer id : favoriteIds){
				favoritesService.deleteFavorite(id);
			}
		}

		final List<Integer> traverseListParentFirst = favoritesFolderDAO.getTraverseListParentFirst(folders, folderId);
		Collections.reverse(traverseListParentFirst);

		for (Integer id : traverseListParentFirst){
			favoritesFolderDAO.delete(id);
		}
	}

	@Override
	public FavoritesFolder updateFolder(FavoritesFolder folder){
		final FavoritesFolder existing = favoritesFolderDAO.get(folder.getId());
		if (existing == null){
			log.warn("Folder does not exists -> ID=" + folder.getId());
			return null;
		}
		existing.setFolderName(folder.getFolderName());
		existing.setParentId(folder.getParentId());
		existing.setSortOrder(folder.getSortOrder());

		return favoritesFolderDAO.save(existing);
	}
}
