/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import com.ni3.ag.adminconsole.server.dao.*;
import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.service.SequenceValueService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.util.OfflineObjectId;
import com.ni3.ag.adminconsole.validation.ACException;

public class SequenceValueServiceImpl implements SequenceValueService{
	private static final Logger log = Logger.getLogger(SequenceValueServiceImpl.class);
	private FavoritesFoldersDAO favoritesFolderDAO;
	private FavoritesDAO favoritesDAO;
	private ObjectDAO objectDAO;
	private ThematicMapDAO thematicMapDAO;
	private ThematicFolderDAO thematicFolderDAO;
	private ThematicClusterDAO thematicClusterDAO;

	public void setFavoritesFolderDAO(FavoritesFoldersDAO favoritesFolderDAO){
		this.favoritesFolderDAO = favoritesFolderDAO;
	}

	public void setFavoritesDAO(FavoritesDAO favoritesDAO){
		this.favoritesDAO = favoritesDAO;
	}

	public void setObjectDAO(ObjectDAO objectDAO){
		this.objectDAO = objectDAO;
	}

	public void setThematicMapDAO(ThematicMapDAO thematicMapDAO){
		this.thematicMapDAO = thematicMapDAO;
	}

	public void setThematicFolderDAO(ThematicFolderDAO thematicFolderDAO){
		this.thematicFolderDAO = thematicFolderDAO;
	}

	public void setThematicClusterDAO(ThematicClusterDAO thematicClusterDAO){
		this.thematicClusterDAO = thematicClusterDAO;
	}

	@Override
	public int getCurrentValForSequence(String seqName, User usr) throws ACException{
		int userRangeStart = new OfflineObjectId(usr.getId(), 0, true).getResult();
		int userRangeEnd = new OfflineObjectId(usr.getId(), OfflineObjectId.ID_MASK, true).getResult();
		log.debug("Checking sequence " + seqName + " for user " + usr.getUserName());
		log.debug("User start id " + userRangeStart);
		log.debug("  User end id " + userRangeEnd);
		int result = 0;
		if (User.FAVORITES_FOLDER_SEQUENCE.equals(seqName))
			result = favoritesFolderDAO.getMaxIdForRange(userRangeStart, userRangeEnd);
		else if (User.FAVORITES_SEQUENCE.equals(seqName))
			result = favoritesDAO.getMaxIdForRange(userRangeStart, userRangeEnd);
		else if (User.OBJECT_SEQUENCE.equals(seqName))
			result = objectDAO.getMaxIdForRange(userRangeStart, userRangeEnd);
		else if(User.THEMATIC_MAP_SEQUENCE.equals(seqName))
			result = thematicMapDAO.getMaxIdForRange(userRangeStart, userRangeEnd);
		else if(User.THEMATIC_MAP_FOLDER_SEQUENCE.equals(seqName))
			result = thematicFolderDAO.getMaxIdForRange(userRangeStart, userRangeEnd);
		else if(User.THEMATIC_MAP_CLUSTER_SEQUENCE.equals(seqName))
			result = thematicClusterDAO.getMaxIdForRange(userRangeStart, userRangeEnd);
		log.debug("User current id " + result);
		if (result == 0){
			log.error("Calculated current id for sequence `" + seqName + "` == 0");
			throw new ACException(TextID.MsgErrorGetSequenceVal, new String[] { usr.getUserName(), seqName });
		}
		return result;
	}

}
